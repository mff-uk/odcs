package cz.cuni.mff.xrg.odcs.commons.app;

import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

public class SelectImplementationFactoryBean implements FactoryBean<Object> {
    private String key;

    private Map<String, Object> implementations;

    public Object getObject() {
        if (implementations == null) {
            return null;
        }
        Object impl = implementations.get(key);
        if (impl == null) {
            throw new IllegalStateException(
                    "There is no implementation for key = " + key);
        }
        return impl;
    }

    public Class<? extends Object> getObjectType() {
        if (getObject() != null) {
            return getObject().getClass();
        }
        return null;
    }

    public boolean isSingleton() {
        return false;
    }

    @Required
    public void setKey(String key) {
        this.key = key;
    }

    @Required
    public void setImplementations(Map<String, Object> implementations) {
        this.implementations = implementations;
    }
}
