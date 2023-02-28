package me.beeland.dunmoore.archaeology.artifact;

public enum ArtifactRarity {

    COMMON("Rarities.COMMON.Chance", "Rarities.COMMON.Color"),

    UNCOMMON("Rarities.UNCOMMON.Chance", "Rarities.UNCOMMON.Color"),

    RARE("Rarities.RARE.Chance", "Rarities.RARE.Color"),

    EPIC("Rarities.EPIC.Chance", "Rarities.EPIC.Color"),

    LEGENDARY("Rarities.LEGENDARY.Chance", "Rarities.LEGENDARY.Color");

    private String chancePath;
    private String colorPath;

    ArtifactRarity(String chancePath, String colorPath) {
        this.chancePath = chancePath;
        this.colorPath = colorPath;
    }

    public String getChancePath() {
        return chancePath;
    }

    public String getColorPath() {
        return colorPath;
    }
}
