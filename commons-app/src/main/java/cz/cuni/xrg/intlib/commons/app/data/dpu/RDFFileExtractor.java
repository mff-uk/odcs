package cz.cuni.xrg.intlib.commons.app.data.dpu;

import cz.cuni.xrg.intlib.commons.SubType;
import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.TemplateConfiguration;
import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.extract.ExtractContext;
import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.extract.ExtractException;
import cz.cuni.xrg.intlib.commons.app.data.Extractor;
import cz.cuni.xrg.intlib.commons.configuration.ExceptionSetting;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import org.openrdf.rio.RDFHandler;

/**
 * Component for extracting data from RDF file.
 *
 * @author Jiri Tomes;
 */
public class RDFFileExtractor implements Extractor {

    private String name;
    private String description;
    private JarFile jarFile;
    private TemplateConfiguration templateConfiguration;
    private Map<String, Object> mapConfiguration = new HashMap<String, Object>();

    public RDFFileExtractor(String name, String description, JarFile jarFile) {
        this.name = name;
        this.description = description;
        this.jarFile = jarFile;
        this.templateConfiguration = new TemplateConfiguration();

    }

    public String getDescription() {
        return description;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return Type.EXTRACTOR;
    }

    public SubType getSubtype() {
        return SubType.RDF_EXTRACTOR;
    }

    public TemplateConfiguration getTemplateConfiguration() {
        return templateConfiguration;
    }

    public Map<String, Object> getSettings() throws ExceptionSetting {
        return mapConfiguration;
    }

    public void setSettings(Map<String, Object> configuration) {
        mapConfiguration = configuration;
    }

    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
