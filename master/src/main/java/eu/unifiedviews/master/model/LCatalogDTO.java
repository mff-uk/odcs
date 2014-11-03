package eu.unifiedviews.master.model;

public class LCatalogDTO {

    private String name;

    private String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[name=" + name + ", uri=" + uri + "]";
    }
}
