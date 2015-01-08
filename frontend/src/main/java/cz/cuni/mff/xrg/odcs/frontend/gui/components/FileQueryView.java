package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Bogo
 */
public class FileQueryView extends QueryView {

    /**
     * Constructor.
     */
    public FileQueryView() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        final Label label = new Label("Browser not available for FilesDataUnit");
        mainLayout.addComponent(label);
        mainLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

        setCompositionRoot(mainLayout);
    }

    @Override
    public void browseDataUnit() {

    }

    @Override
    public void setQueryingEnabled(boolean enabled) {

    }

    @Override
    public void reset() {

    }

}
