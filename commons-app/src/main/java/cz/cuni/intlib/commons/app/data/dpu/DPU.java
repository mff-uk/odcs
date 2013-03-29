package cz.cuni.intlib.commons.app.data.dpu;

/**
 *
 * @author Jiri Tomes
 */
public interface DPU {

    public String getDescription();

    // public JarFile getJarFile();

    public String getName();

    public Type getType();

    // public SubType getSubtype();

    public TemplateConfiguration getTemplateConfiguration();
}
