package dpu;

import java.util.jar.JarFile;

/**
 *
 * @author Jiri Tomes
 */
public interface DPU {

    public String getDescription();

    public JarFile getJarFile();

    public String getName();

    public Type getType();

    public SubType getSubtype();
}
