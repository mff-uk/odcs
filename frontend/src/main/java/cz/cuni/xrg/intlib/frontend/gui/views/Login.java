package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.frontend.AuthenticationService;
import cz.cuni.xrg.intlib.frontend.RequestHolder;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * LOGIN screen of application.
 *
 * @author Bogo
 */
public class Login extends ViewComponent {

	private static final Logger LOG = LoggerFactory.getLogger(Login.class);
	
    private CssLayout mainLayout;
    private VerticalLayout layout;
    private TextField login;
    private PasswordField password;
	
	private AuthenticationService authService;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        buildMainLayout();
        setCompositionRoot(mainLayout);
		authService = App.getApp().getBean(AuthenticationService.class);
    }

    private void buildMainLayout() {
        mainLayout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                if (c instanceof VerticalLayout) {
                    return "position: absolute;"
                            + "    top:0;"
                            + "    bottom: 0;"
                            + "    left: 0;"
                            + "    right: 0;"
                            + "    margin: auto;";
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
		login.focus();
        layout.addComponent(login);

        password = new PasswordField("Password:");
        layout.addComponent(password);

        Button loginButton = new Button("Login", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                login();
            }
        });
        layout.addComponent(loginButton);
		password.addShortcutListener(new Button.ClickShortcut(loginButton, ShortcutAction.KeyCode.ENTER));
		Label info = new Label(String.format("For account creation, please contact admin at: <a href='mailto:%1$s'>%1$s</a>.", App.getAppConfig().getString(ConfigProperty.EMAIL_ADMIN)));
		info.setContentMode(ContentMode.HTML);
		layout.addComponent(info);
        layout.setSizeUndefined();
        mainLayout.addComponent(layout);

    }

    private void login() {
		
		final Authentication authToken = new UsernamePasswordAuthenticationToken(
			login.getValue(), password.getValue()
		);

        try {
			authService.login(login.getValue(), password.getValue(), RequestHolder.getRequest());

			// login is successful
			App.getApp().getMain().refreshUserBar();
			App.getApp().getNavigator().navigateTo(ViewNames.INITIAL.getUrl());

		} catch (AuthenticationException ex) {
			password.setValue("");
			LOG.info(String.format("Invalid credentials for username ?.", login.getValue()));
			Notification.show(String.format("Invalid credentials for username %s.", login.getValue()), Notification.Type.ERROR_MESSAGE);
		} 
    }
}
