package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;

/**
 * Login screen of application.
 *
 * @author Bogo
 */
public class Login extends ViewComponent {

    private CssLayout mainLayout;
    private VerticalLayout layout;
    private TextField login;
    private PasswordField password;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    private void buildMainLayout() {
        mainLayout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                if (c instanceof VerticalLayout) {
                    return "position: absolute;" +
"    top:0;" +
"    bottom: 0;" +
"    left: 0;" +
"    right: 0;" +
"    margin: auto;";
                }
                return null;
            }
        };
        mainLayout.setSizeFull();
        mainLayout.setHeight(600, Unit.PIXELS);
        //setWidth("100%");
        layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        Label logo = new Label();
        logo.setValue("<h1>Intlib</h1>");
        logo.setContentMode(ContentMode.HTML);
        layout.addComponent(logo);

        login = new TextField("Login:");
        layout.addComponent(login);

        password = new PasswordField("Password:");
        layout.addComponent(password);

        Button loginButton = new Button("Login", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                App.getApp().getNavigator().navigateTo(ViewNames.Initial.getUrl());
            }
        });
        layout.addComponent(loginButton);
        layout.setSizeUndefined();
        mainLayout.addComponent(layout);

    }
}
