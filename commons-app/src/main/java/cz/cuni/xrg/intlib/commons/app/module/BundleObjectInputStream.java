package cz.cuni.xrg.intlib.commons.app.module;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.osgi.framework.Bundle;

public class BundleObjectInputStream extends ObjectInputStream {

	private final Bundle bundle;
	
	public BundleObjectInputStream(InputStream in, Bundle bundle) throws IOException {
		super(in);
		this.bundle = bundle;
	}
 
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException
    {
        return this.bundle.loadClass(desc.getName());
    }
    
}
