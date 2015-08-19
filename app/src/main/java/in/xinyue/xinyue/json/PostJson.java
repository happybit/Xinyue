package in.xinyue.xinyue.json;

/**
 * Created by pzheng on 7/20/2015.
 */
public class PostJson {

    private int ID;
    private String title;
    private String status;
    private String type;
    private AuthorJson author;
    private String content;
    private int parent;
    private String link;
    private String date;
    private String modified;
    private String format;
    private String slug;
    private String guid;
    private String excerpt;
    private int menu_order;
    private String comment_status;
    private String ping_status;
    private boolean sticky;
    private String date_tz;
    private String date_gmt;
    private String modified_tz;
    private String modified_gmt;
    private MetaJson meta;
    private FeaturedImageJson featured_image;
    private TermsJson terms;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AuthorJson getAuthor() {
        return author;
    }

    public void setAuthor(AuthorJson author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public int getMenu_order() {
        return menu_order;
    }

    public void setMenu_order(int menu_order) {
        this.menu_order = menu_order;
    }

    public String getComment_status() {
        return comment_status;
    }

    public void setComment_status(String comment_status) {
        this.comment_status = comment_status;
    }

    public String getPing_status() {
        return ping_status;
    }

    public void setPing_status(String ping_status) {
        this.ping_status = ping_status;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public String getDate_tz() {
        return date_tz;
    }

    public void setDate_tz(String date_tz) {
        this.date_tz = date_tz;
    }

    public String getDate_gmt() {
        return date_gmt;
    }

    public void setDate_gmt(String date_gmt) {
        this.date_gmt = date_gmt;
    }

    public String getModified_tz() {
        return modified_tz;
    }

    public void setModified_tz(String modified_tz) {
        this.modified_tz = modified_tz;
    }

    public String getModified_gmt() {
        return modified_gmt;
    }

    public void setModified_gmt(String modified_gmt) {
        this.modified_gmt = modified_gmt;
    }

    public MetaJson getMeta() {
        return meta;
    }

    public void setMeta(MetaJson meta) {
        this.meta = meta;
    }

    public FeaturedImageJson getFeatured_image() {
        return featured_image;
    }

    public void setFeatured_image(FeaturedImageJson featured_image) {
        this.featured_image = featured_image;
    }

    public TermsJson getTerms() {
        return terms;
    }

    public void setTerms(TermsJson terms) {
        this.terms = terms;
    }
}
