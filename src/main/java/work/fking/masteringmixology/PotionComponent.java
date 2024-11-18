package work.fking.masteringmixology;

import java.awt.Color;

public enum PotionComponent {
    MOX('M', "03a9f4", 5666, MasteringMixologyPlugin.VARP_MOX_RESIN),
    AGA('A', "00e676", 5667, MasteringMixologyPlugin.VARP_AGA_RESIN),
    LYE('L', "e91e63", 5668, MasteringMixologyPlugin.VARP_LYE_RESIN);

    private final char character;
    private final String colorCode;
    private final Color color;
    private final int spriteId;
    private final int resinVarpId;

    PotionComponent(char character, String colorCode, int spriteId, int resinVarpId) {
        this.character = character;
        this.colorCode = colorCode;
        this.color = Color.decode("#" + colorCode);
        this.spriteId = spriteId;
        this.resinVarpId = resinVarpId;
    }

    public char character() {
        return character;
    }

    public String colorCode() {
        return colorCode;
    }

    public Color color() {
        return color;
    }

    public int spriteId() {
        return spriteId;
    }

    public int resinVarpId() {
        return resinVarpId;
    }
}
