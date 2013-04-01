package cz.cuni.xrg.intlib.commons.app.data.dpu;

import java.util.jar.JarFile;

/**
 * Basic information of each DPU component.
 * 
 * @author Jiri Tomes
 */
public interface DPU {

    public String getDescription();

    public JarFile getJarFile();

    public String getName();

    public Type getType();

    public SubType getSubtype();

    public TemplateConfiguration getTemplateConfiguration();
}
