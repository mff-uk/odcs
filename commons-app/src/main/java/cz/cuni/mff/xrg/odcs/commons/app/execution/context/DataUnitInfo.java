package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;

import javax.persistence.*;

/**
 * Holds information about single
 * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} context.
 * 
 * @author Petyr
 * 
 */
@Entity
@Table(name = "exec_dataunit_info")
public class DataUnitInfo implements DataObject {

	/**
	 * Primary key.
	 */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exec_dataunit_info")
	@SequenceGenerator(name = "seq_exec_dataunit_info", allocationSize = 1)
	private Long id;

	/**
	 * Index of DataUnit. Used to determine folder.
	 */
	@Column(name = "idx")
	private Integer index;

	/**
	 * Name of DataUnit given to the DataUnit by DPU or changed by user (on the
	 * edge).
	 */
	@Column(name = "name")
	private String name;

	/**
	 * DataUnit type.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type")
	private DataUnitType type;

	/**
	 * True if use as input otherwise false.
	 */
	@Column(name = "is_input")
	private boolean isInput;

	/**
	 * Empty constructor because of JAP.
	 */
	public DataUnitInfo() { }

	/**
	 * 
	 * @param name Name of DataUnit.
	 * @param index Index of data unit.
	 * @param type Type of DataUnit.
	 * @param isInput Is used as input?
	 */
	public DataUnitInfo(Integer index,
			String name,
			DataUnitType type,
			boolean isInput) {
		this.index = index;
		this.name = name;
		this.type = type;
		this.isInput = isInput;
	}

	@Override
	public Long getId() {
		return id;
	}

	public Integer getIndex() {
		return index;
	}

	/**
	 * 
	 * @return DataUnit's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return DateUnit'stype.
	 */
	public DataUnitType getType() {
		return type;
	}

	/**
	 * 
	 * @return True it represented DataUnit is used as an input.
	 */
	public boolean isInput() {
		return isInput;
	}
       
    @Override
    public String toString() {
        return name;
    }

}