package cz.cuni.mff.xrg.odcs.commons.app.data;

/**
 * Class describe {@link DataUnit} class.
 *
 * @author Petyr
 *
 */
public class DataUnitDescription {

	/**
	 * Name.
	 */
	private final String name;

	/**
	 * Type class name.
	 */
	private final String typeName;

	/**
	 * Description provided by developer.
	 */
	private final String description;

	/**
	 * True if usage of {@link DataUnit} is optional.
	 */
	private final boolean optional;

	private DataUnitDescription(String name,
			String typeName,
			String description,
			boolean optional) {
		this.name = name;
		this.typeName = typeName;
		this.description = description;
		this.optional = optional;
	}

	/**
	 * Create description for output {@link DataUnit}.
	 *
	 * @param name
	 * @param typeName
	 * @param description
	 * @return
	 */
	public static DataUnitDescription createOutput(String name,
			String typeName,
			String description) {
		return new DataUnitDescription(name, typeName, description, false);
	}

	/**
	 * Create description for input {@link DataUnit}.
	 *
	 * @param name
	 * @param typeName
	 * @param description
	 * @param optional
	 * @return
	 */
	public static DataUnitDescription createInput(String name,
			String typeName,
			String description,
			boolean optional) {
		return new DataUnitDescription(name, typeName, description, optional);
	}

	public String getName() {
		return name;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Return true if the usage of this DataUnit is optional. For output
	 * {@link DataUnit} return false always.
	 *
	 * @return
	 */
	public boolean getOptional() {
		return this.optional;
	}

	@Override
	public String toString() {
		return name;
	}

}
