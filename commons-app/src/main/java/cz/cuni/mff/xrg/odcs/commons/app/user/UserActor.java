package cz.cuni.mff.xrg.odcs.commons.app.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;

@Entity
@Table(name = "user_actor")
public class UserActor implements DataObject {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_actor")
    @SequenceGenerator(name = "seq_user_actor", allocationSize = 1)
    private Long id;

    @Column(name = "id_extuser", unique = true)
    private String externalId;

    @Column
    private String name;

    @Override
    public Long getId() {
        return this.id;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
