package in.xinyue.xinyue.request.json;

import java.util.List;

/**
 * Created by pzheng on 7/20/2015.
 */
public class TermsJson {
    private List<CategoryJson> category;

    public List<CategoryJson> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryJson> category) {
        this.category = category;
    }

    public class CategoryJson {
        private int ID;
        private String name;
        private String slug;
        private String description;
        private String taxonomy;
        private int parent;
        private int count;
        private String link;
        private Meta meta;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTaxonomy() {
            return taxonomy;
        }

        public void setTaxonomy(String taxonomy) {
            this.taxonomy = taxonomy;
        }

        public int getParent() {
            return parent;
        }

        public void setParent(int parent) {
            this.parent = parent;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
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
                private String collection;
                private String self;

                public String getCollection() {
                    return collection;
                }

                public void setCollection(String collection) {
                    this.collection = collection;
                }

                public String getSelf() {
                    return self;
                }

                public void setSelf(String self) {
                    this.self = self;
                }
            }
        }
    }
}
