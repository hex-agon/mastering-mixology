package work.fking.masteringmixology.enums;

public enum PotionSelectionStrategy {
	REWARD_SHOP_BALANCE,
	HIGHEST_XP,
	PREFER_RETORT,
	MOX,
	AGA,
	LYE;

	@Override
	public String toString() {
		switch (this) {
			case REWARD_SHOP_BALANCE:
				return "Reward shop balance";
			case HIGHEST_XP:
				return "Highest XP";
			case PREFER_RETORT:
				return "Prefer retort";
			case MOX:
				return "Most Mox rewards";
			case AGA:
				return "Most Aga rewards";
			case LYE:
				return "Most Lye rewards";
			default:
				return name();
		}
	}
}
