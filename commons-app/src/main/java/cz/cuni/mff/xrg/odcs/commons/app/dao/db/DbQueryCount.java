package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import javax.persistence.Query;

/**
 * Query for number of records. 
 * 
 * @author Petyr
 *
 * @param <T>
 */
public class DbQueryCount<T> extends DbQuery<T> {

	DbQueryCount(Query query) {
		super(query);
	}

}
