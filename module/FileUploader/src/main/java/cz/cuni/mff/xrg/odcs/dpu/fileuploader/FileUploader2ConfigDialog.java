package cz.cuni.mff.xrg.odcs.dpu.fileuploader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FileUploader2ConfigDialog extends BaseConfigDialog<FileUploader2Config> {
    /**
     * 
     */
    private static final long serialVersionUID = -5668436075836909428L;

    private static final String MAP_TEXT = "Symbolic name to baseURI and Format map. Line format: symbolicName;baseURI(optional);FileFormat(optional)";

    private ObjectProperty<String> mapText = new ObjectProperty<String>("");

    public FileUploader2ConfigDialog() {
        super(FileUploader2Config.class);
        initialize();
    }

    private void initialize() {
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        TextArea ta = new TextArea(MAP_TEXT, mapText);
        ta.setRows(50);
        ta.setColumns(50);
        mainLayout.addComponent(ta);
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(FileUploader2Config conf) throws ConfigException {
        StringBuilder sb = new StringBuilder();
        for (String key : conf.getSymbolicNameToBaseURIMap().keySet()) {
            sb.append(key);
            sb.append(";");
            sb.append(conf.getSymbolicNameToBaseURIMap().get(key));
            sb.append(";");
            if (conf.getSymbolicNameToFormatMap().get(key) != null) {
                sb.append(conf.getSymbolicNameToFormatMap().get(key));
            }
            sb.append("\n");
        }
        mapText.setValue(sb.toString());
    }

    @Override
    public FileUploader2Config getConfiguration() throws ConfigException {
        Map<String, String> symbolicNameToBaseURIMap = new LinkedHashMap<>();
        Map<String, String> symbolicNameToFormatMap = new LinkedHashMap<>();
        BufferedReader br = new BufferedReader(new StringReader(mapText.getValue()));

        String line;
        int i = 1;
        try {
            while ((line = br.readLine()) != null) {
                String[] val = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, ";");
                if (val.length < 2) {
                    throw new ConfigException(String.format("Line %d %s has invalid format.", i, line));
                }

                if (symbolicNameToBaseURIMap.containsKey(val[0])) {
                    throw new ConfigException(String.format("Duplicate symbolic name %s on line %d.", val[0], i));
                }

                if (val[1] != null && val[1].length() > 0) {
                    try {
                        new URIImpl(val[1]);
                    } catch (IllegalArgumentException ex) {
                        throw new ConfigException(String.format("Wrong URI on line %d symbolic name", i, val[0]), ex);
                    }
                    if (!val[1].startsWith("http://")) {
                        throw new ConfigException(String.format("Wrong base URI on line %d symbolic name", i, val[0]));
                    }
                    symbolicNameToBaseURIMap.put(val[0], val[1]);
                }

                if (val[2] != null && val[2].length() > 0) {
                    if (null == RDFFormat.valueOf(val[2])) {
                        throw new ConfigException(String.format("Unsupported format %s on line %d symbolic name", val[2], i, val[0]));
                    }
                    symbolicNameToFormatMap.put(val[0], val[2]);
                }
                i++;
            }
        } catch (IOException ex) {
            throw new ConfigException(ex);
        }

        FileUploader2Config fileUploader2Config = new FileUploader2Config();
        fileUploader2Config.setSymbolicNameToBaseURIMap(symbolicNameToBaseURIMap);
        fileUploader2Config.setSymbolicNameToFormatMap(symbolicNameToFormatMap);
        return fileUploader2Config;
    }

}
