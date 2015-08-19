package in.xinyue.xinyue.json;

/**
 * Created by pzheng on 7/20/2015.
 */
public class AuthorJson {
    private int ID;
    private String username;
    private String name;
    private String first_name;
    private String last_name;
    private String nickname;
    private String slug;
    private String URL;
    private String avatar;
    private String description;
    private String regeisterd;
    private Meta meta;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegeisterd() {
        return regeisterd;
    }

    public void setRegeisterd(String regeisterd) {
        this.regeisterd = regeisterd;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
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
            private String archives;

            public String getSelf() {
                return self;
            }

            public void setSelf(String self) {
                this.self = self;
            }

            public String getArchives() {
                return archives;
            }

            public void setArchives(String archives) {
                this.archives = archives;
            }
        }
    }
}
