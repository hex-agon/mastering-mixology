package work.fking.masteringmixology;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Adaptive meta-policy that decides which of the 3 visible orders the
 * player should brew this turn. Implements the state machine documented in
 * {@code mixology-sim/STRATEGY.md} §7 with the recommended thresholds.
 *
 * <p>State is maintained across calls so the policy can detect when the
 * deficit shape transitions between (single-bottleneck / two-bottleneck /
 * balanced) regimes and switch sub-policy accordingly. Call {@link #reset()}
 * at the start of a fresh session (player enters the lab, plugin starts up).
 *
 * <p>The decision returned is the set of order indices (0..2) the player
 * should brew. The plugin maps that to UI colouring (green vs red text).
 */
public class MetaPolicy {

    public enum State { SINGLE_BN, DUAL_BN, BALANCED }

    /** Recommended thresholds from the sweep at 61k/53k/71k. See STRATEGY.md §7. */
    public static final double T_DUAL_IN       = 0.20;
    public static final double T_DUAL_OUT      = 0.25;
    public static final double T_BALANCED_IN   = 0.10;
    public static final double T_BALANCED_OUT  = 0.15;

    private State state = State.SINGLE_BN;

    public void reset() {
        state = State.SINGLE_BN;
    }

    public State currentState() {
        return state;
    }

    /**
     * Decide which slots to brew.
     *
     * @param orders     the 3 current orders' PotionTypes (slot index = array index)
     * @param resin      [mox, aga, lye] current resin from varps
     * @param target     [mox, aga, lye] total target (sum of selected reward costs)
     * @return set of order indices (0..2) the player should brew this turn
     */
    public Set<Integer> decide(PotionType[] orders, int[] resin, int[] target) {
        int[] deficit = new int[3];
        for (int c = 0; c < 3; c++) {
            deficit[c] = Math.max(target[c] - resin[c], 0);
        }

        // Top-2 deficit indices (R's order(..., decreasing=TRUE) tie-break:
        // lower original index first). Stable sort with deficit-desc primary,
        // index-asc secondary matches.
        Integer[] colorIdx = {0, 1, 2};
        Arrays.sort(colorIdx, (a, b) -> {
            int cmp = Integer.compare(deficit[b], deficit[a]);
            return cmp != 0 ? cmp : Integer.compare(a, b);
        });
        int dMax = deficit[colorIdx[0]];
        int dMid = deficit[colorIdx[1]];
        int dMin = deficit[colorIdx[2]];

        double gap12 = dMax == 0 ? Double.POSITIVE_INFINITY : (dMax - dMid) / (double) dMax;
        double gap13 = dMax == 0 ? Double.POSITIVE_INFINITY : (dMax - dMin) / (double) dMax;

        // State transition (with hysteresis). When leaving balanced, the next
        // state is dual_bn if the top-2 are still tight, single_bn otherwise.
        switch (state) {
            case SINGLE_BN:
                if (gap13 < T_BALANCED_IN) {
                    state = State.BALANCED;
                } else if (gap12 < T_DUAL_IN) {
                    state = State.DUAL_BN;
                }
                break;
            case DUAL_BN:
                if (gap13 < T_BALANCED_IN) {
                    state = State.BALANCED;
                } else if (gap12 > T_DUAL_OUT) {
                    state = State.SINGLE_BN;
                }
                break;
            case BALANCED:
                if (gap13 > T_BALANCED_OUT) {
                    state = (gap12 < T_DUAL_IN) ? State.DUAL_BN : State.SINGLE_BN;
                }
                break;
        }

        switch (state) {
            case SINGLE_BN:
                return singleBn(orders, deficit, colorIdx[0]);
            case DUAL_BN:
                return dualBn(orders, deficit, colorIdx[0], colorIdx[1]);
            case BALANCED:
            default:
                return balanced(orders, deficit);
        }
    }

    // ------------------------------------------------------------------
    // Sub-policies
    // ------------------------------------------------------------------

    /**
     * single_bn: if ≥2 of the 3 orders give the top-deficit colour, brew all
     * 3; else brew the slots that give that colour; else brew the single
     * best-deficit slot.
     */
    private Set<Integer> singleBn(PotionType[] orders, int[] deficit, int topColorOrd) {
        PotionComponent top = PotionComponent.ENTRIES[topColorOrd];
        Set<Integer> giving = slotsGiving(orders, top);
        if (giving.size() >= 2) {
            return all3();
        }
        if (!giving.isEmpty()) {
            return giving;
        }
        return singleton(bestDeficitSlot(orders, deficit));
    }

    /**
     * dual_bn: if ≥2 of the 3 orders give *both* top-2 deficit colours, brew
     * all 3; else fall through to the single_bn rule.
     */
    private Set<Integer> dualBn(PotionType[] orders, int[] deficit, int topOrd, int midOrd) {
        PotionComponent top = PotionComponent.ENTRIES[topOrd];
        PotionComponent mid = PotionComponent.ENTRIES[midOrd];
        if (countSlotsGivingBoth(orders, top, mid) >= 2) {
            return all3();
        }
        return singleBn(orders, deficit, topOrd);
    }

    /**
     * balanced: if all 3 orders are multi-resin (not MMM/AAA/LLL), brew all
     * 3; else brew the single best-deficit slot.
     */
    private Set<Integer> balanced(PotionType[] orders, int[] deficit) {
        if (allMultiResin(orders)) {
            return all3();
        }
        return singleton(bestDeficitSlot(orders, deficit));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static boolean givesResin(PotionType pt, PotionComponent color) {
        if (pt == null) {
            return false;
        }
        for (PotionComponent c : pt.components()) {
            if (c == color) {
                return true;
            }
        }
        return false;
    }

    private static Set<Integer> slotsGiving(PotionType[] orders, PotionComponent color) {
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < orders.length; i++) {
            if (givesResin(orders[i], color)) {
                result.add(i);
            }
        }
        return result;
    }

    private static int countSlotsGivingBoth(PotionType[] orders, PotionComponent a, PotionComponent b) {
        int count = 0;
        for (PotionType pt : orders) {
            if (givesResin(pt, a) && givesResin(pt, b)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isMultiResin(PotionType pt) {
        if (pt == null) {
            return false;
        }
        EnumSet<PotionComponent> uniq = EnumSet.noneOf(PotionComponent.class);
        for (PotionComponent c : pt.components()) {
            uniq.add(c);
        }
        return uniq.size() >= 2;
    }

    private static boolean allMultiResin(PotionType[] orders) {
        for (PotionType pt : orders) {
            if (!isMultiResin(pt)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Deficit-reduction score for a slot: sum over colours of
     * resin_in_color * max(deficit_color, 0). Resin yield per potion:
     *   XXX (single component repeated 3x)     -> 20 of X
     *   XXY (one component doubled, one single)-> 20 of doubled + 10 of single
     *   XYZ (three distinct components = MAL)  -> 20 of each
     */
    private static int slotScore(PotionType pt, int[] deficit) {
        if (pt == null) {
            return Integer.MIN_VALUE;
        }
        int[] count = new int[3];
        for (PotionComponent c : pt.components()) {
            count[c.ordinal()]++;
        }
        int distinct = 0;
        for (int c = 0; c < 3; c++) {
            if (count[c] > 0) {
                distinct++;
            }
        }
        int score = 0;
        for (int c = 0; c < 3; c++) {
            if (count[c] == 0) {
                continue;
            }
            // XXY single colour gets 10; otherwise 20 (XXX, XXY's doubled, all of MAL).
            int yield = (count[c] == 1 && distinct == 2) ? 10 : 20;
            score += yield * Math.max(deficit[c], 0);
        }
        return score;
    }

    private static int bestDeficitSlot(PotionType[] orders, int[] deficit) {
        int bestIdx = 0;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < orders.length; i++) {
            int s = slotScore(orders[i], deficit);
            if (s > bestScore) {
                bestScore = s;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    private static Set<Integer> all3() {
        Set<Integer> s = new HashSet<>(3);
        s.add(0);
        s.add(1);
        s.add(2);
        return s;
    }

    private static Set<Integer> singleton(int idx) {
        Set<Integer> s = new HashSet<>(1);
        s.add(idx);
        return s;
    }
}
