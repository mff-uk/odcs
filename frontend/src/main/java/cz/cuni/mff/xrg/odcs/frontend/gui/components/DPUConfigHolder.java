package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Component for holding the DPU's configuration dialog. The component show message if the configuration
 * component is null.
 *
 * @author Škoda Petr
 */
public class DPUConfigHolder extends CustomComponent {

    /**
     * Holder for dialog, will provide scroll bars if needed.
     */
    private final Panel dialogHolder;

    public DPUConfigHolder() {
        setSizeFull();
        // create holder
        dialogHolder = new Panel();
        dialogHolder.setSizeFull();
    }

    /**
     * Set the component representing the dialog.
     *
     * @param confDialog
     *            Can be null.
     */
    public void setConfigComponent(Component confDialog) {
        dialogHolder.setContent(confDialog);

        if (confDialog == null) {
            // Create label.
            final Label infoLabel = new Label();
            infoLabel.setSizeUndefined();
            infoLabel.setValue("This DPU doesn't provide configuration component.");

            final VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            layout.addComponent(infoLabel);
            layout.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);

            dialogHolder.setContent(null);
            setCompositionRoot(layout);
        } else {
            dialogHolder.setContent(confDialog);
            setCompositionRoot(dialogHolder);
        }
    }

}
