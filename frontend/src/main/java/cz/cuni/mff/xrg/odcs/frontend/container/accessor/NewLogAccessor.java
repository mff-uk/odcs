package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import java.util.Date;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessorBase;

/**
 * Class accessor for logs.
 * 
 * @author Petyr
 */
public class NewLogAccessor extends ClassAccessorBase<Log> {

    /**
     * Constructor.
     */
    public NewLogAccessor() {
        super(Log.class);

        addInvisible(Long.class, "id", new ColumnGetter<Long>() {
            @Override
            public Long get(Log object) {
                return object.getId();
            }
        });

        add(Integer.class, "logLevel", "Type", new ColumnGetter<Integer>() {
            @Override
            public Integer get(Log object) {
                return object.getLogLevel();
            }
        });

        add(Date.class, "timestamp", "Timestamp", new ColumnGetter<Date>() {
            @Override
            public Date get(Log object) {
                return new Date(object.getTimestamp());
            }
        });

        addInvisible(Long.class, "execution", new ColumnGetter<Long>() {
            @Override
            public Long get(Log object) {
                return object.getExecution();
            }
        });

        add(Long.class, "dpu", "DPU Instance", false, true, new ColumnGetter<Long>() {
            @Override
            public Long get(Log object) {
                return object.getDpu();
            }
        });

        add(String.class, "message", "Message", new ColumnGetter<String>() {
            @Override
            public String get(Log object) {
                return object.getMessage();
            }
        });

    }

}
