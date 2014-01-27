package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import java.util.Iterator;

/**
 * Iterator to iterate over {@link DirectoryHandler}. Secure type cast from
 * {@link ManageableHandler} to {@link Handler}. 
 * 
 * This iterator does not support {@link #remove()} method.
 * 
 * The iterator is not recursive!
 * 
 * @author Petyr
 */
public class DirectoryHandlerIterator implements Iterator<Handler> {
	
	/**
	 * Iterator over underlying collection.
	 */
	private final Iterator<ManageableHandler> iterator;
		
	DirectoryHandlerIterator(DirectoryHandlerImpl directory, Iterator<ManageableHandler> iterator) {
		this.iterator = iterator;
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Handler next() {
		return this.iterator.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
