package work.fking.masteringmixology;

public enum PotionComponent {
    AGA("Aga", 'A', "00e676", 850),
    LYE("Lye", 'L', "e91e63", 1250),
    MOX("Mox", 'M', "03a9f4", 450);

    private final String name;
    private final char character;
    private final String color;
    private final int experience;

    PotionComponent(String name, char character, String color, int experience) {
        this.name = name;
        this.character = character;
        this.color = color;
        this.experience = experience;
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

    public int experience() {
        return experience;
    }
}
