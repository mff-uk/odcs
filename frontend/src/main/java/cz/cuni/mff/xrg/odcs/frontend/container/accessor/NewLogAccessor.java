package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import cz.cuni.mff.xrg.odcs.frontend.container.ClassAccessorBase;
import java.util.Date;

/**
 * Class accessor for logs.
 *
 * @author Petyr
 */
public class NewLogAccessor extends ClassAccessorBase<Log> {

	public NewLogAccessor() {
		super(Log.class);

		addInvisible(Long.class, "id", new ColumnGetter<Long>() {
			@Override
			public Long get(Log object) {
				return object.getId();
			}
		});

		add(Date.class, "timestamp", new ColumnGetter<Date>() {
			@Override
			public Date get(Log object) {
				return new Date(object.getTimestamp());
			}
		});

		add(Integer.class, "logLevel", new ColumnGetter<Integer>() {
			@Override
			public Integer get(Log object) {
				return object.getLogLevel();
			}
		});

		addInvisible(Long.class, "execution", new ColumnGetter<Long>() {
			@Override
			public Long get(Log object) {
				return object.getExecution();
			}
		});

		add(Long.class, "dpu", "dpu instance", false, true, new ColumnGetter<Long>() {
			@Override
			public Long get(Log object) {
				return object.getDpu();
			}
		});

		add(String.class, "message", new ColumnGetter<String>() {
			@Override
			public String get(Log object) {
				return object.getMessage();
			}
		});

		add(String.class, "source", new ColumnGetter<String>() {
			@Override
			public String get(Log object) {
				return object.getSource();
			}
		});

	}

}
