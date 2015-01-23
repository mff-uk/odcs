package eu.unifiedviews.commons.dataunit.core;

/**
 *
 * @author Škoda Petr
 */
public class CoreServiceBusImpl implements CoreServiceBus {

    private final ConnectionSource connectionSource;

    private final FaultTolerant faultTolerant;

    public CoreServiceBusImpl(ConnectionSource connectionSource, FaultTolerant faultTolerant) {
        this.connectionSource = connectionSource;
        this.faultTolerant = faultTolerant;
    }

    @Override
    public <T> T getService(Class<T> serviceClass) throws IllegalArgumentException {
        if (serviceClass.isAssignableFrom(ConnectionSource.class)) {
            return (T)connectionSource;
        } else if (serviceClass.isAssignableFrom(FaultTolerant.class)) {
            return (T) faultTolerant;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
