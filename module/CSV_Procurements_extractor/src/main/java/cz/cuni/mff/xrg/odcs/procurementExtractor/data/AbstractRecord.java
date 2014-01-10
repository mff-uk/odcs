package cz.cuni.mff.xrg.odcs.procurementExtractor.data;

/**
 * Attributes and methods common to all records harvested by Open Data Node.
 */
public abstract class AbstractRecord {

    private String id;

    /**
     * @return ID of the record unique for whole Open Data Node (i.e. even between multiple data sets)
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the record ID. ID have to be unique for whole Open Data Node, i.e. also between data sets.
     * 
     * Implementation note: Say we're harvesting two data sets. Both data sets already do have an ID fields, both use sequence from 0. The easiest way to
     * generate non-colliding ID for this setter is prepending data set name/id to the field ID, for example:
     * 
     * record.setId("dataset1_" + harvestedRecord.getId())
     * 
     * @param id
     *            record ID
     */
    public void setId(String id) {
        this.id = id;
    }

}
