package in.xinyue.xinyue.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pzheng on 7/20/2015.
 */
public class MetaJson {
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
    }
}
