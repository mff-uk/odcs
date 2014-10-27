package sk.eea.xxx.domain;


public class PipelineDTO {

    private Long id;

    private String name;

    private String description;

//    private User owner;

//    private ShareType shareType;
//
//    private Date lastChange;
//
//    public PipelineDTO(Pipeline pip) {
//        id = pip.getId();
//        name = pip.getName();
//        description = pip.getDescription();
//        owner = pip.getOwner();
//        shareType = pip.getShareType();
//        lastChange = pip.getLastChange();
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public User getOwner() {
//        return owner;
//    }
//
//    public void setOwner(User owner) {
//        this.owner = owner;
//    }
//
//    public ShareType getShareType() {
//        return shareType;
//    }
//
//    public void setShareType(ShareType shareType) {
//        this.shareType = shareType;
//    }
//
//    public Date getLastChange() {
//        return lastChange;
//    }
//
//    public void setLastChange(Date lastChange) {
//        this.lastChange = lastChange;
//    }

    @Override
    public String toString() {
        return "Pipeline [id=" + id + ", name=" + name + ", description="
                + description + "]";
    }
}
