package cz.cuni.mff.xrg.odcs.frontend.gui;

import java.util.Collection;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.RequestHolder;
import cz.cuni.mff.xrg.odcs.frontend.auth.AuthenticationService;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Initial;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Login;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.PostLogoutCleaner;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Scheduler;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Settings;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu.DPUPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist.ExecutionListPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist.PipelineListPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigatorHolder;

/**
 * Class represent main application component. The component contains menu bar
 * and a place where to place application view.
 * 
 * @author Petyr
 */
public class MenuLayout extends CustomComponent {

    private ClassNavigator navigator;

    /**
     * Authentication context used to render menu with respect to currently
     * logged in user.
     */
    @Autowired
    private AuthenticationContext authCtx;

    /**
     * Authentication service handling logging in and out.
     */
    @Autowired
    private AuthenticationService authService;

    /**
     * Used layout.
     */
    private VerticalLayout mainLayout;
    private HorizontalLayout headerLayout;

    /**
     * Menu bar.
     */
    private MenuBar menuBar;

    /**
     * Layout for application views.
     */
    private Panel viewLayout;

    private Label userName;

    private Button logOutButton;

    private Embedded backendStatus;
    
    private Embedded Logo;

    private HashMap<String, MenuItem> menuItems = new HashMap<>();

    /**
     * Build the layout.
     */
    public void build() {
        // top-level component properties
        setWidth("100%");
        // we can set height to the main component
        // as it will not show scroll bars then
        setHeight("100%");

        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setMargin(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        
        // Add Logo.
        Logo = new Embedded(null, new ThemeResource("img/unifiedviews_logo.svg"));   
        Logo.setStyleName("logo");

        // menuBar
        menuBar = new MenuBar();
        menuBar.setImmediate(false);
        menuBar.setWidth("100.0%");
        menuBar.setHeight("45px");
        menuBar.setHtmlContentAllowed(true);

        backendStatus = new Embedded();
        backendStatus.setWidth("16px");
        backendStatus.setHeight("16px");
        backendStatus.setStyleName("backendStatus");

        userName = new Label(authCtx.getUsername());
        userName.setIcon(new ThemeResource("img/user.svg"));
        userName.setWidth("100px");
        userName.addStyleName("username");

        logOutButton = new Button();
        logOutButton.setCaption("Logout");
        logOutButton.setVisible(authCtx.isAuthenticated());
        logOutButton.setStyleName(BaseTheme.BUTTON_LINK);
        logOutButton.addStyleName("logout");
        logOutButton.setIcon(new ThemeResource("img/logout.svg"), "Log out");
        logOutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                authService.logout(RequestHolder.getRequest());
                authCtx.clear();
                refreshUserBar();
                navigator.navigateTo(Login.class);
                doAfterLogoutCleaning();
            }
        });
        
        HorizontalLayout headerLine = new HorizontalLayout(Logo, backendStatus, logOutButton, userName);
        headerLine.setSpacing(false);
        headerLine.setMargin(new MarginInfo(false, true, false, true));
        headerLine.setHeight("75px");
        headerLine.addStyleName("headerPanel");
        headerLine.setComponentAlignment(Logo, Alignment.MIDDLE_LEFT);
        headerLine.setComponentAlignment(backendStatus, Alignment.MIDDLE_RIGHT);
        headerLine.setComponentAlignment(userName, Alignment.MIDDLE_RIGHT);
        headerLine.setComponentAlignment(logOutButton, Alignment.MIDDLE_RIGHT);
        
        mainLayout.addComponent(headerLine);
        mainLayout.setExpandRatio(headerLine, 0.0f);

        HorizontalLayout menuLine = new HorizontalLayout(menuBar);
        menuLine.setSpacing(false);
        menuLine.setWidth("100%");
        menuLine.setHeight("45px");
        menuLine.addStyleName("loginPanel");
        menuLine.setComponentAlignment(menuBar, Alignment.MIDDLE_CENTER);
        menuLine.setExpandRatio(menuBar, 1.0f);

        mainLayout.addComponent(menuLine);
        mainLayout.setExpandRatio(menuLine, 0.0f);

        // viewLayout
        viewLayout = new Panel();
        viewLayout.setSizeFull();
        viewLayout.setStyleName("viewLayout");

        mainLayout.addComponent(viewLayout);
        mainLayout.setExpandRatio(viewLayout, 1.0f);

        refreshBackendStatus(false);

        setCompositionRoot(mainLayout);
    }

    /**
     * finds all classes that implement PostLogoutCleaner interface
     * and calls doAfterLogout method. These classes need to have
     * session scope
     */
    private void doAfterLogoutCleaning() {
    	AppEntry appEntry = (AppEntry) getParent();
    	Collection<PostLogoutCleaner> classesToDoCleaning = 
    			appEntry.getBeans(PostLogoutCleaner.class).values();
    	for (PostLogoutCleaner presenterClass : classesToDoCleaning) {
    		presenterClass.doAfterLogout();
		}
	}

	/**
     * Return layout for application views.
     * 
     * @return layout for application views
     */
    public Panel getViewLayout() {
        return this.viewLayout;
    }

    /**
     * Refresh user bar.
     */
    public void refreshUserBar() {
        userName.setValue(authCtx.getUsername());
        logOutButton.setVisible(authCtx.isAuthenticated());
    }

    /**
     * Refreshes the status of backend. Green/red icon in header.
     * 
     * @param isRunning
     */
    public void refreshBackendStatus(boolean isRunning) {
        backendStatus.setDescription(isRunning ? "Backend is online!" : "Backend is offline!");
        backendStatus.setSource(new ThemeResource(isRunning ? "icons/online.svg" : "icons/offline.svg"));
    }

    /**
     * Setup navigation and menu.
     * 
     * @param navigatorHolder
     */
    public void setNavigation(ClassNavigatorHolder navigatorHolder) {
        this.navigator = navigatorHolder;
        // init menuBar
        menuItems.put("", menuBar.addItem("Home", new NavigateToCommand(Initial.class, navigator)));
        menuItems.put("PipelineList", menuBar.addItem("Pipelines", new NavigateToCommand(PipelineListPresenterImpl.class, navigator)));
        menuItems.put("DPURecord", menuBar.addItem("DPU Templates", new NavigateToCommand(DPUPresenterImpl.class, navigator)));
        menuItems.put("ExecutionList", menuBar.addItem("Execution Monitor", new NavigateToCommand(ExecutionListPresenterImpl.class, navigator)));
        menuItems.put("Scheduler", menuBar.addItem("Scheduler", new NavigateToCommand(Scheduler.class, navigator)));
        menuItems.put("Administrator", menuBar.addItem("Settings", new NavigateToCommand(Settings.class, navigator)));
    }

    /**
     * Sets active menu item.
     * 
     * @param viewName
     *            Item to set as active.
     */
    public void setActiveMenuItem(String viewName) {
        for (MenuItem item : menuBar.getItems()) {
            item.setCheckable(true);
            item.setChecked(false);
        }
        MenuItem activeMenu = menuItems.get(viewName);
        if (activeMenu != null) {
            activeMenu.setChecked(true);
        }
    }

    /**
     * Class use as command to change sub-pages.
     * 
     * @author Petyr
     */
    private class NavigateToCommand implements Command {

        private final Class<?> clazz;

        private final ClassNavigator navigator;

        public NavigateToCommand(Class<?> clazz, ClassNavigator navigator) {
            this.clazz = clazz;
            this.navigator = navigator;
        }

        @Override
        public void menuSelected(MenuItem selectedItem) {
            navigator.navigateTo(this.clazz);
        }
    }

}
