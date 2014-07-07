package cz.cuni.mff.xrg.odcs.dpu.filestosparqlloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FilesToSPARQLLoaderConfigDialog extends BaseConfigDialog<FilesToSPARQLLoaderConfig> {
    /**
     * 
     */
    private static final long serialVersionUID = -5668436075836909428L;

    private static final String QUERY_ENDPOINT_URL_LABEL = "Query endpoint URL";

    private static final String UPDATE_ENDPOINT_URL_LABEL = "Update endpoint URL";

    private static final String COMMIT_SIZE_LABEL = "Commit size (0 = one file, one transaction, 1 = autocommit connection, n = commit every n triples)";

    private static final String SKIP_ON_ERROR_LABEL = "Skip file on error";

    private static final String TARGET_CONTEXTS_LABEL = "Target contexts";

    private ObjectProperty<String> queryEndpointUrl = new ObjectProperty<String>("");

    private ObjectProperty<String> updateEndpointUrl = new ObjectProperty<String>("");

    private ObjectProperty<Integer> commitSize = new ObjectProperty<Integer>(0);

    private ObjectProperty<Boolean> skipOnError = new ObjectProperty<Boolean>(
            false);

    private ObjectProperty<String> targetContexts = new ObjectProperty<String>("");

    public FilesToSPARQLLoaderConfigDialog() {
        super(FilesToSPARQLLoaderConfig.class);
        initialize();
    }

    private void initialize() {
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        mainLayout.addComponent(new TextField(QUERY_ENDPOINT_URL_LABEL, queryEndpointUrl));
        mainLayout.addComponent(new TextField(UPDATE_ENDPOINT_URL_LABEL, updateEndpointUrl));
        mainLayout.addComponent(new TextField(COMMIT_SIZE_LABEL, commitSize));
        mainLayout.addComponent(new CheckBox(SKIP_ON_ERROR_LABEL, skipOnError));

        TextArea ta = new TextArea(TARGET_CONTEXTS_LABEL, targetContexts);
        ta.setRows(10);
        ta.setColumns(50);
        mainLayout.addComponent(ta);
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(FilesToSPARQLLoaderConfig conf) throws DPUConfigException {
        queryEndpointUrl.setValue(conf.getQueryEndpointUrl());
        updateEndpointUrl.setValue(conf.getUpdateEndpointUrl());
        commitSize.setValue(conf.getCommitSize());
        skipOnError.setValue(conf.isSkipOnError());

        StringBuilder sb = new StringBuilder();
        for (String key : conf.getTargetContexts()) {
            sb.append(key);
            sb.append("\n");
        }
        targetContexts.setValue(sb.toString());
    }

    @Override
    public FilesToSPARQLLoaderConfig getConfiguration() throws DPUConfigException {
        BufferedReader br = new BufferedReader(new StringReader(targetContexts.getValue()));

        String line;
        Set<String> targetContexts = new LinkedHashSet<>();
        try {
            while ((line = br.readLine()) != null) {
                targetContexts.add(line);
            }
        } catch (IOException ex) {
            throw new DPUConfigException(ex);
        }

        FilesToSPARQLLoaderConfig conf = new FilesToSPARQLLoaderConfig();
        conf.setQueryEndpointUrl(queryEndpointUrl.getValue());
        conf.setUpdateEndpointUrl(updateEndpointUrl.getValue());
        conf.setCommitSize(commitSize.getValue());
        conf.setSkipOnError(skipOnError.getValue());
        conf.setTargetContexts(targetContexts);
        return conf;
    }

}
