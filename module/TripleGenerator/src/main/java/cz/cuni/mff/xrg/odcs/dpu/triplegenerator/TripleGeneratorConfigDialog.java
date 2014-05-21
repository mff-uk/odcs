package cz.cuni.mff.xrg.odcs.dpu.triplegenerator;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class TripleGeneratorConfigDialog extends BaseConfigDialog<TripleGeneratorConfig> {
    /**
     * 
     */
    private static final long serialVersionUID = -5668436075836909428L;

    private static final String TRIPLE_COUNT_LABEL = "Generate this count of triples";

    private static final String COMMIT_SIZE_LABEL  = "Commit transaction every this triples";

    private ObjectProperty<Integer> tripleCount = new ObjectProperty<Integer>(0);

    private ObjectProperty<Integer> commitSize = new ObjectProperty<Integer>(0);

    public TripleGeneratorConfigDialog() {
        super(TripleGeneratorConfig.class);
        initialize();
    }

    private void initialize() {
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        mainLayout.addComponent(new TextField(TRIPLE_COUNT_LABEL, tripleCount));
        mainLayout.addComponent(new TextField(COMMIT_SIZE_LABEL, commitSize));

        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(TripleGeneratorConfig conf) throws ConfigException {
        tripleCount.setValue(conf.getTripleCount());
        commitSize.setValue(conf.getCommitSize());
    }

    @Override
    public TripleGeneratorConfig getConfiguration() throws ConfigException {
        TripleGeneratorConfig config = new TripleGeneratorConfig();
        config.setTripleCount(tripleCount.getValue());
        config.setCommitSize(commitSize.getValue());
        return config;
    }

}
