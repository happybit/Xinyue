package in.xinyue.xinyue.request.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pzheng on 7/20/2015.
 */
public class FeaturedImageJson {
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
    private String except;
    private int menu_order;
    private String comment_status;
    private String ping_status;
    private boolean sticky;
    private String date_tz;
    private String date_gmt;
    private String modified_tz;
    private String modified_gmt;
    private Meta meta;
    private List<Term> terms;
    private String source;
    private boolean is_image;
    private AttachmentMeta attachment_meta;

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

    public String getExcept() {
        return except;
    }

    public void setExcept(String except) {
        this.except = except;
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

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean is_image() {
        return is_image;
    }

    public void setIs_image(boolean is_image) {
        this.is_image = is_image;
    }

    public AttachmentMeta getAttachment_meta() {
        return attachment_meta;
    }

    public void setAttachment_meta(AttachmentMeta attachment_meta) {
        this.attachment_meta = attachment_meta;
    }

    private class Meta {
        private Links links;

        public Links getLinks() {
            return links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }

        private class Links {
            private String self;
            private String author;
            private String collection;
            private String replies;
            @SerializedName("version-history")
            private String version_history;
            private String up;

            public String getSelf() {
                return self;
            }

            public void setSelf(String self) {
                this.self = self;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getCollection() {
                return collection;
            }

            public void setCollection(String collection) {
                this.collection = collection;
            }

            public String getReplies() {
                return replies;
            }

            public void setReplies(String replies) {
                this.replies = replies;
            }

            public String getVersion_history() {
                return version_history;
            }

            public void setVersion_history(String version_history) {
                this.version_history = version_history;
            }

            public String getUp() {
                return up;
            }

            public void setUp(String up) {
                this.up = up;
            }
        }
    }

    private class Term {}

    public class AttachmentMeta {
        private int width;
        private int height;
        private String file;
        private Sizes sizes;
        private ImageMeta image_meta;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public Sizes getSizes() {
            return sizes;
        }

        public void setSizes(Sizes sizes) {
            this.sizes = sizes;
        }

        public ImageMeta getImage_meta() {
            return image_meta;
        }

        public void setImage_meta(ImageMeta image_meta) {
            this.image_meta = image_meta;
        }

        public class Sizes {
            private ImageInfo thumbnail;
            private ImageInfo medium;
            @SerializedName("home-big-image")
            private ImageInfo home_big_image;
            @SerializedName("home-small-image")
            private ImageInfo home_small_image;
            @SerializedName("blog-image")
            private ImageInfo blog_image;

            public ImageInfo getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(ImageInfo thumbnail) {
                this.thumbnail = thumbnail;
            }

            public ImageInfo getMedium() {
                return medium;
            }

            public void setMedium(ImageInfo medium) {
                this.medium = medium;
            }

            public ImageInfo getHome_big_image() {
                return home_big_image;
            }

            public void setHome_big_image(ImageInfo home_big_image) {
                this.home_big_image = home_big_image;
            }

            public ImageInfo getHome_small_image() {
                return home_small_image;
            }

            public void setHome_small_image(ImageInfo home_small_image) {
                this.home_small_image = home_small_image;
            }

            public ImageInfo getBlog_image() {
                return blog_image;
            }

            public void setBlog_image(ImageInfo blog_image) {
                this.blog_image = blog_image;
            }

            public class ImageInfo {
                private String file;
                private int width;
                private int height;
                @SerializedName("mime-type")
                private String mime_type;
                private String url;

                public String getFile() {
                    return file;
                }

                public void setFile(String file) {
                    this.file = file;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public String getMime_type() {
                    return mime_type;
                }

                public void setMime_type(String mime_type) {
                    this.mime_type = mime_type;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }

        }

        private class ImageMeta {
            private int aperture;
            private String credit;
            private String camera;
            private String caption;
            private int created_timestamp;
            private String copyright;
            private int focal_length;
            private int iso;
            private int shutter_speed;
            private String title;

            public int getAperture() {
                return aperture;
            }

            public void setAperture(int aperture) {
                this.aperture = aperture;
            }

            public String getCredit() {
                return credit;
            }

            public void setCredit(String credit) {
                this.credit = credit;
            }

            public String getCamera() {
                return camera;
            }

            public void setCamera(String camera) {
                this.camera = camera;
            }

            public String getCaption() {
                return caption;
            }

            public void setCaption(String caption) {
                this.caption = caption;
            }

            public int getCreated_timestamp() {
                return created_timestamp;
            }

            public void setCreated_timestamp(int created_timestamp) {
                this.created_timestamp = created_timestamp;
            }

            public String getCopyright() {
                return copyright;
            }

            public void setCopyright(String copyright) {
                this.copyright = copyright;
            }

            public int getFocal_length() {
                return focal_length;
            }

            public void setFocal_length(int focal_length) {
                this.focal_length = focal_length;
            }

            public int getIso() {
                return iso;
            }

            public void setIso(int iso) {
                this.iso = iso;
            }

            public int getShutter_speed() {
                return shutter_speed;
            }

            public void setShutter_speed(int shutter_speed) {
                this.shutter_speed = shutter_speed;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }

}
