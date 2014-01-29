package cz.cuni.mff.xrg.odcs.frontend.doa.container.db;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccessRead;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryBuilder;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryCount;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessor;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.InMemorySource;

/**
 *
 * @author Petyr
 * @param <T>
 */
public class DbInMemorySource <T extends DataObject>
	extends InMemorySource<T, DbQueryBuilder<T>, DbQuery<T>, DbQueryCount<T> > {
	
	public DbInMemorySource(ClassAccessor<T> classAccessor) {
		super(classAccessor);
	}

	public DbInMemorySource(ClassAccessor<T> classAccessor, 
			DataAccessRead<T, DbQueryBuilder<T>, DbQuery<T>, DbQueryCount<T>> source) {
		super(classAccessor, source);
	}
	
	
}
