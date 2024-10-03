package work.fking.masteringmixology;

public enum PotionComponent {
    AGA('A', "00e676"),
    LYE('L', "e91e63"),
    MOX('M', "03a9f4"),
    NONE('?', "");

    private final char character;
    private final String color;

    PotionComponent(char character, String color) {
        this.character = character;
        this.color = color;
    }

    public static PotionComponent fromVarbitValue(int varbitValue) {
        switch (varbitValue) {
            case 1:
                return MOX;
            case 2:
                return AGA;
            case 3:
                return LYE;
            default:
                return NONE;
        }
    }

    public char character() {
        return character;
    }

    public String color() {
        return color;
    }
}
