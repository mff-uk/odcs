package cz.cuni.mff.xrg.odcs.backend.logback;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.DbLogRead;
import java.util.HashMap;

/**
 * Class used to manage the values of last {@link Log#relativeId}.
 * 
 * @author Škoda Petr <skodapetr@gmail.com>
 */
public class RelativeIdHolder {
	
	/**
	 * Holder for relative IDs.
	 */
	class IdHolder {
		
		public Long id;

		public IdHolder(Long id) {
			if (id == null) {
				// new execution
				this.id = 1l;
			} else {
				// continue with existing
				this.id = id;
			}
		}
				
	}
	
	private final DbLogRead logRead;

	private final HashMap<Long, IdHolder> relativeIds = new HashMap<>();
	
	public RelativeIdHolder(DbLogRead logRead) {
		this.logRead = logRead;
	}
	
	/**
	 * This method is not thread save!
	 * 
	 * @param execution
	 * @return Relative id for next log for given execution.
	 */
	public Long getNextId(Long execution) {
		IdHolder holder = relativeIds.get(execution);
		if (holder == null) {
			// check for size
			if (relativeIds.size() > 100) {
				// delete all
				relativeIds.clear();
			}			
			// create new
			holder = new IdHolder(logRead.getLastRelativeIndex(execution));
			relativeIds.put(execution, holder);
		}
		return holder.id++;
	}
	
}
