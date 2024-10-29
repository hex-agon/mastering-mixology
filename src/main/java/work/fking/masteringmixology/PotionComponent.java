package work.fking.masteringmixology;

public enum PotionComponent {
    AGA('A', "00e676", 5667),
    LYE('L', "e91e63", 5668),
    MOX('M', "03a9f4", 5666);

    private final char character;
    private final String color;
    private final int spriteId;

    PotionComponent(char character, String color, int spriteId) {
        this.character = character;
        this.color = color;
        this.spriteId = spriteId;
    }

    public char character() {
        return character;
    }

    public String color() {
        return color;
    }

    public int spriteId() {
        return spriteId;
    }
}
