package cz.cuni.mff.xrg.odcs.dpu.httpdownloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class HTTPDownloaderConfigDialog extends BaseConfigDialog<HTTPDownloaderConfig> {
    /**
     * 
     */
    private static final long serialVersionUID = -5668436075836909428L;

    private static final String READ_TIMEOUT_LABEL = "Read timeout (HTTP)";

    private static final String CONNECTION_TIMEOUT_LABEL = "Connection timeout (HTTP)";

    private static final String MAP_TEXT = "Files to download (each on one line in format symbolicName;URL)";

    private ObjectProperty<Integer> connectionTimeout = new ObjectProperty<Integer>(0);

    private ObjectProperty<Integer> readTimeout = new ObjectProperty<Integer>(0);

    private ObjectProperty<String> mapText = new ObjectProperty<String>("");

    public HTTPDownloaderConfigDialog() {
        super(HTTPDownloaderConfig.class);
        initialize();
    }

    private void initialize() {
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        mainLayout.addComponent(new TextField(CONNECTION_TIMEOUT_LABEL, connectionTimeout));
        mainLayout.addComponent(new TextField(READ_TIMEOUT_LABEL, readTimeout));

        TextArea ta = new TextArea(MAP_TEXT, mapText);
        ta.setRows(50);
        ta.setColumns(50);
        mainLayout.addComponent(ta);
        connectionTimeout.setValue(325);
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(HTTPDownloaderConfig conf) throws ConfigException {
        connectionTimeout.setValue(conf.getConnectionTimeout());
        readTimeout.setValue(conf.getReadTimeout());

        StringBuilder sb = new StringBuilder();
        for (String key : conf.getSymbolicNameToURIMap().keySet()) {
            sb.append(key);
            sb.append(";");
            sb.append(conf.getSymbolicNameToURIMap().get(key));
            sb.append("\n");
        }
        mapText.setValue(sb.toString());
    }

    @Override
    public HTTPDownloaderConfig getConfiguration() throws ConfigException {
        Map<String, String> symbolicNameToURIMap = new LinkedHashMap<>();
        BufferedReader br = new BufferedReader(new StringReader(mapText.getValue()));

        String line;
        int i = 1;
        try {
            while ((line = br.readLine()) != null) {
                String[] val = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, ";");
                if (val.length != 2) {
                    throw new ConfigException(String.format("Line %d %s has invalid format.", i, line));
                }

                if (symbolicNameToURIMap.containsKey(val[0])) {
                    throw new ConfigException(String.format("Duplicate symbolic name %s on line %d.", val[0], i));
                }

                try {
                    new java.net.URL(val[1]);
                } catch (MalformedURLException ex) {
                    throw new ConfigException(String.format("Wrong URL on line %d symbolic name", i, val[0]), ex);
                }
                symbolicNameToURIMap.put(val[0], val[1]);
                i++;
            }
        } catch (IOException ex) {
            throw new ConfigException(ex);
        }
        
        HTTPDownloaderConfig httpDownloaderConfig = new HTTPDownloaderConfig();
        httpDownloaderConfig.setSymbolicNameToURIMap(symbolicNameToURIMap);
        httpDownloaderConfig.setConnectionTimeout(connectionTimeout.getValue());
        httpDownloaderConfig.setReadTimeout(readTimeout.getValue());
        return httpDownloaderConfig;
    }

}
