package in.xinyue.xinyue.model;

public enum Category {
    all("all"),
    earrings("earrings"),
    pendants("pendants"),
    necklaces("necklaces"),
    rings("rings"),
    bracelets("bracelets"),
    brooches("brooches");

    private String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

}
