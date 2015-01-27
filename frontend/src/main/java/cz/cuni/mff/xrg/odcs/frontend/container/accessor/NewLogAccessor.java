package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import java.util.Date;

import org.springframework.context.i18n.LocaleContextHolder;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import cz.cuni.mff.xrg.odcs.frontend.FrontendMessages;
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
        FrontendMessages messages = new FrontendMessages(LocaleContextHolder.getLocale(), this.getClass().getClassLoader());
        addInvisible(Long.class, "id", new ColumnGetter<Long>() {
            @Override
            public Long get(Log object) {
                return object.getId();
            }
        });

        add(Integer.class, "logLevel", messages.getString("NewLogAccessor.type"), new ColumnGetter<Integer>() {
            @Override
            public Integer get(Log object) {
                return object.getLogLevel();
            }
        });

        add(Date.class, "timestamp", messages.getString("NewLogAccessor.timestamp"), new ColumnGetter<Date>() {
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

        add(Long.class, "dpu", messages.getString("NewLogAccessor.dpu"), false, true, new ColumnGetter<Long>() {
            @Override
            public Long get(Log object) {
                return object.getDpu();
            }
        });

        add(String.class, "message", messages.getString("NewLogAccessor.message"), new ColumnGetter<String>() {
            @Override
            public String get(Log object) {
                return object.getMessage();
            }
        });

    }

}
