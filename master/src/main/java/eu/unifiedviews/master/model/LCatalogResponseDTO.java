package eu.unifiedviews.master.model;

public class LCatalogResponseDTO {

    private String name;

    private String uri;

    private boolean created = false;

    private boolean updated = false;

    private boolean success = false;

    private String message;

    public LCatalogResponseDTO(String name, String uri, boolean created, boolean updated, boolean success, String message) {
        this.name = name;
        this.uri = uri;
        this.created = created;
        this.updated = updated;
        this.success = success;
        this.message = message;
    }

    public LCatalogResponseDTO() {

    }

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

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[name=" + name + ", uri=" + uri + ", created=" + created + ", updated=" + updated + ", success=" + success + ", message=" + message + "]";
    }
}
