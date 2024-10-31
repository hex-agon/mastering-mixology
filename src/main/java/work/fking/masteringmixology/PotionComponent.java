package work.fking.masteringmixology;

public enum PotionComponent {
    AGA("00e676"),
    LYE("e91e63"),
    MOX("03a9f4");

    private final String formattedName;
    private final char character;
    private final String color;

    PotionComponent(String color) {
        this.formattedName = name().charAt(0) + name().substring(1).toLowerCase();
        this.character = name().charAt(0);
        this.color = color;
    }

    public String formattedName() {
        return formattedName;
    }

    public char character() {
        return character;
    }

    public String color() {
        return color;
    }
}
