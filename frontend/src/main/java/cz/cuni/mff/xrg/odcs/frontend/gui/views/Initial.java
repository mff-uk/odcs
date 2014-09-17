package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;

import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;

/**
 * Initial view with short description of the tool.
 * 
 * @author Bogo
 */
@Component
@Scope("prototype")
@Address(url = "")
public class Initial extends ViewComponent {

    private AbsoluteLayout mainLayout;

    private Label label;

    /**
     * Constructor.
     */
    public Initial() {
    }

    @Override
    public boolean isModified() {
        //There are no editable fields.
        return false;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    private AbsoluteLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new AbsoluteLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("1024px");
        mainLayout.setHeight("768px");

        // top-level component properties
        this.setWidth("100.0%");
        this.setHeight("100.0%");

        // label
        label = new Label();
        label.setImmediate(false);
        label.setWidth("-1px");
        label.setHeight("-1px");
        label.setValue("<h2>Welcome to the administration interface of UnifiedViews - ETL tool for RDF data.</h2>"
                + "<p>The tool uses data processing pipelines for obtaining, processing, and storing RDF data;<br/>"
                + "makes data processing highly customizable by allowing to use custom plugins -- data processing units (DPUs) -- on the pipelines;<br/>"
                + "provides monitoring, debugging, and schedulling of ETL tasks.<br/>"
                + "</p>"
                + "<p>For more information, please visit the <a href=\"https://grips.semantic-web.at/display/UDDOC/Introduction\"> UnifiedViews</a> documentation.</p>");

        label.setContentMode(ContentMode.HTML);
        mainLayout.addComponent(label, "top:30.0px;left:30px;");

        return mainLayout;
    }
}
