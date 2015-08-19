package in.xinyue.xinyue.model;

/**
 * Created by pzheng on 7/24/2015.
 */
public enum Category {
    all("all"),
    rings("rings"),
    bracelets("bracelets"),
    earrings("earrings"),
    brooches("brooches"),
    pendants("pendants"),
    necklaces("necklaces");

    private String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

}
