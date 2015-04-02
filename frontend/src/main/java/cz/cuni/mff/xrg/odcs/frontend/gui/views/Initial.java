package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;

import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;
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

    private Embedded logo;

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
        setSizeFull();
        // common part: create layout
        mainLayout = new AbsoluteLayout();
        mainLayout.setSizeFull();

        // label
        label = new Label();
        label.setImmediate(false);
        label.setWidth("-1px");
        label.setHeight("-1px");
        label.setValue(Messages.getString("Initial.welcome")
                + Messages.getString("Initial.p1")
                + Messages.getString("Initial.p2")
                + Messages.getString("Initial.p3")
                + "</p>"
                + Messages.getString("Initial.more.info"));

        label.setContentMode(ContentMode.HTML);
        mainLayout.addComponent(label, "top:100.0px;left:30px;");

        logo = new Embedded(null, new ThemeResource("img/unifiedviews_logo.svg"));
        mainLayout.addComponent(logo, "top:30.0px; left:100px;");

        return mainLayout;
    }
}
