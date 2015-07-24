package cz.cuni.mff.xrg.odcs.commons.app.execution.server;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;

@Entity
@Table(name = "backend_servers")
public class ExecutionServer implements Serializable, DataObject {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_backend_servers")
    @SequenceGenerator(name = "seq_backend_servers", allocationSize = 1)
    private Long id;

    @Column(name = "last_update")
    private Date lastUpdate;

    @Column(name = "backend_id")
    private String backendId;

    @Override
    public Long getId() {
        return this.id;
    }

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getBackendId() {
        return this.backendId;
    }

    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

}
