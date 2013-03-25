package dpu;

import java.util.jar.JarFile;

/**
 *
 * @author Jiri Tomes;
 */
public class RdfFileExtractor implements DPU {

    private String name;
    private String description;
    private JarFile jarFile;

    public RdfFileExtractor(String name, String description, JarFile jarFile) {
        this.name = name;
        this.description = description;
        this.jarFile = jarFile;

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
}
