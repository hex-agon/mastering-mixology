package work.fking.masteringmixology;

public enum PotionComponent {
    AGA("Aga", 'A', "00e676"),
    LYE("Lye", 'L', "e91e63"),
    MOX("Mox", 'M', "03a9f4");

    private final String name;
    private final char character;
    private final String color;

    PotionComponent(String name, char character, String color) {
        this.name = name;
        this.character = character;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public char character() {
        return character;
    }

    public String color() {
        return color;
    }
}
