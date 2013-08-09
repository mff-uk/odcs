package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibPagedTable;
import cz.cuni.xrg.intlib.frontend.gui.components.UsersList;

class Administrator extends ViewComponent {

	private HorizontalLayout mainLayout;
	private VerticalLayout usersLayout;
	private VerticalLayout recordsLayout;
	private VerticalLayout pipelinesLayout;
	
	private VerticalLayout tabsLayout;
	private Button usersButton;
	private Button recordsButton;
	private Button pipelinesButton;
	private Button shownTab = null;
	private UsersList usersList;


	public Administrator() { }

	private HorizontalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new HorizontalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		
		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		
        tabsLayout = new VerticalLayout();

        tabsLayout.setWidth("100%");
        tabsLayout.setImmediate(true);
        


        
        usersLayout = new VerticalLayout();
        usersLayout.setImmediate(true);
        usersLayout.setWidth("100%");
        usersLayout.setHeight("100%");
        usersLayout.addComponent(new Label("Users"));
        
        usersList = new UsersList();
        usersLayout = usersList.buildUsersListLayout();
        usersLayout.setStyleName("settings");
        
        
        recordsLayout = new VerticalLayout();
        recordsLayout.setMargin(true);
        recordsLayout.setSpacing(true);
        recordsLayout.setImmediate(true);
        recordsLayout.setStyleName("settings");
        recordsLayout.setWidth("100%");
        recordsLayout.addComponent(new Label("Records"));
        
        
        pipelinesLayout = new VerticalLayout();
        pipelinesLayout.setMargin(true);
        pipelinesLayout.setSpacing(true);
        pipelinesLayout.setImmediate(true);
        pipelinesLayout.setStyleName("settings");
        pipelinesLayout.setWidth("100%");
        pipelinesLayout.addComponent(new Label("Pipelines"));
        
        usersButton = new NativeButton("Users");
        usersButton.setHeight("40px");
        usersButton.setWidth("90px");
        usersButton.setStyleName("selectedtab");
        usersButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=usersButton;
				shownTab.setStyleName("selectedtab");
				recordsButton.setStyleName("multiline");
				pipelinesButton.setStyleName("multiline");
				
				mainLayout.removeComponent(usersLayout);
				mainLayout.removeComponent(recordsLayout);
				mainLayout.removeComponent(pipelinesLayout);
				mainLayout.addComponent(usersLayout);
				mainLayout.setExpandRatio(usersLayout, 0.85f);

				
			}
		});
        tabsLayout.addComponent(usersButton);
        tabsLayout.setComponentAlignment(usersButton, Alignment.TOP_RIGHT);
        
        recordsButton = new NativeButton("Records");
        recordsButton.setHeight("40px");
        recordsButton.setWidth("90px");
        recordsButton.setStyleName("multiline");
        recordsButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=recordsButton;
				shownTab.setStyleName("selectedtab");
				usersButton.setStyleName("multiline");
				pipelinesButton.setStyleName("multiline");
				
				mainLayout.removeComponent(usersLayout);
				mainLayout.removeComponent(recordsLayout);
				mainLayout.removeComponent(pipelinesLayout);
				mainLayout.addComponent(recordsLayout);
				mainLayout.setExpandRatio(recordsLayout, 0.85f);

				
			}
		});
        tabsLayout.addComponent(recordsButton);
        tabsLayout.setComponentAlignment(recordsButton, Alignment.TOP_RIGHT);
        
        pipelinesButton = new NativeButton("Pipelines");
        pipelinesButton.setHeight("40px");
        pipelinesButton.setWidth("90px");
        pipelinesButton.setStyleName("multiline");
        pipelinesButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=pipelinesButton;
				shownTab.setStyleName("selectedtab");
				usersButton.setStyleName("multiline");
				recordsButton.setStyleName("multiline");
				
				mainLayout.removeComponent(usersLayout);
				mainLayout.removeComponent(recordsLayout);
				mainLayout.removeComponent(pipelinesLayout);
				mainLayout.addComponent(pipelinesLayout);
				mainLayout.setExpandRatio(pipelinesLayout, 0.85f);

				
			}
		});
        tabsLayout.addComponent(pipelinesButton);
        tabsLayout.setComponentAlignment(pipelinesButton, Alignment.TOP_RIGHT);


		
		
		mainLayout.addComponent(tabsLayout);
		mainLayout.addComponent(usersLayout);
		mainLayout.setExpandRatio(tabsLayout, 0.15f);
		mainLayout.setExpandRatio(usersLayout, 0.85f);
		
		return mainLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);		
	}

}
