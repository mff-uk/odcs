package cz.cuni.xrg.intlib.commons.module.data;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;

/**
 * Class provide functionality for advance work with DataUnits.
 * 
 * @author Petyr
 * 
 */
public class DataUnitList<T extends DataUnit> {

	/**
	 * List of stored data units.
	 */
	protected List<T> dataUnits;

	/**
	 * List of dataUnits to work with.
	 * 
	 * @param dataUnits
	 */
	protected DataUnitList(List<T> dataUnits) {
		this.dataUnits = dataUnits;
	}

	/**
	 * Create instance of {@link DataUnitList} for working with DataUnits.
	 * 
	 * @param dataUnits
	 * @return
	 */
	public static <NewT extends DataUnit> DataUnitList<DataUnit> create(List<DataUnit> dataUnits) {
		return new DataUnitList<DataUnit>(dataUnits);
	}

	/**
	 * Create instance of {@link DataUnitList} for working with DataUnits that
	 * are stored in given context.
	 * 
	 * @param context
	 * @return
	 */
	public static DataUnitList<DataUnit> create(LoadContext context) {
		return new DataUnitList<DataUnit>(context.getInputs());
	}

	/**
	 * Create instance of {@link DataUnitList} for working with DataUnits that
	 * are stored in given context.
	 * 
	 * @param context
	 * @return
	 */
	public static DataUnitList<DataUnit> create(TransformContext context) {
		return new DataUnitList<DataUnit>(context.getInputs());
	}

	/**
	 * Return {@link DataUnitList} with the DataUnits that have given name.
	 * 
	 * @param name Required DataUnit name.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DataUnitList<T> FilterByName(String name) {
		List<T> result = new LinkedList<>();
		for (DataUnit item : dataUnits) {
			if (item.getName().compareToIgnoreCase(name) == 0) {
				result.add((T) item);
			}
		}
		return new DataUnitList<T>(result);
	}

	/**
	 * Return {@link DataUnitList} with the DataUnits of given type that are in
	 * current instance. of given class.
	 * 
	 * @param type Required class type.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <NewT extends DataUnit> DataUnitList<NewT> FilterByClass(Class<NewT> type) {
		List<NewT> result = new LinkedList<>();
		for (DataUnit item : dataUnits) {
			if (type.isInstance(item)) {
				result.add((NewT) item);
			}
		}
		return new DataUnitList<NewT>(result);
	}

	/**
	 * Return first DataUnit, if there is no such DataUnit throw exception.
	 * 
	 * @return First stored DataUnit.
	 * @throws MissingInputException
	 * @throws IndexOutOfBoundsException
	 */
	public T getFirst() throws MissingInputException {
		if (dataUnits.isEmpty()) {
			throw new MissingInputException();
		} else {
			return dataUnits.get(0);
		}
	}

	/**
	 * Return list of currently stored DataUnits.
	 * 
	 * @return
	 */
	public List<T> getList() {
		return dataUnits;
	}

	/**
	 * Check if there are some DataUnits stored.
	 * 
	 * @return False if the {@link DataUnitList} is empty.
	 */
	public boolean isEmpty() {
		return dataUnits.isEmpty();
	}

}
