package work.fking.masteringmixology;

public enum PotionComponent {
    AGA('A', "00e676", 850),
    LYE('L', "e91e63", 1250),
    MOX('M', "03a9f4", 450);

    private final char character;
    private final String color;
    private final int experience;

    PotionComponent(char character, String color, int experience) {
        this.character = character;
        this.color = color;
        this.experience = experience;
    }

    public char character() {
        return character;
    }

    public String color() {
        return color;
    }

    public int experience() {
        return experience;
    }
}
