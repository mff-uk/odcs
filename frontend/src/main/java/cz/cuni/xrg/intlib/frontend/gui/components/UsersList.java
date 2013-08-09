package cz.cuni.xrg.intlib.frontend.gui.components;

import java.sql.Timestamp;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.IntlibHelper;
import cz.cuni.xrg.intlib.frontend.gui.views.GenerateActionColumnMonitor;




public class UsersList {
	
	 private IntlibPagedTable usersTable;
	 private VerticalLayout usersListLayout;
	 static String[] visibleCols = new String[]{"id", "user", "role",
	        "total_pipelines", "actions"};
	 static String[] headers = new String[]{"Id", "User Name", "Role(s)",
	        "Total Pipelines", "Actions"};
	 private IndexedContainer tableData;
	 private Long userId;


	public VerticalLayout buildUsersListLayout(){
		
		
		usersListLayout = new VerticalLayout();
		usersListLayout.setMargin(true);
		usersListLayout.setSpacing(true);
		usersListLayout.setWidth("100%");

		usersListLayout.setImmediate(true);
		
		
		//Layout for buttons Add new user and Clear Filters on the top.
		HorizontalLayout topLine = new HorizontalLayout();
		topLine.setSpacing(true);
		topLine.setWidth(100, Unit.PERCENTAGE);

		Button addUserButton = new Button();
		addUserButton.setCaption("Create new user");
		addUserButton
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				boolean newUser = true;
				// open usercreation dialog
				UserCreate user = new UserCreate(newUser);
				App.getApp().addWindow(user);
				user.addCloseListener(new CloseListener() {
					@Override
					public void windowClose(CloseEvent e) {
						refreshData();
					}
				});
			}
		});
		topLine.addComponent(addUserButton);
		topLine.setComponentAlignment(addUserButton, Alignment.MIDDLE_RIGHT);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Clear Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("110px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				usersTable.resetFilters();
				usersTable.setFilterFieldVisible("actions", false);
			}
		});
		topLine.addComponent(buttonDeleteFilters);
		topLine.setComponentAlignment(buttonDeleteFilters, Alignment.MIDDLE_RIGHT);

		Label topLineFiller = new Label();
		topLine.addComponentAsFirst(topLineFiller);
		topLine.setExpandRatio(topLineFiller, 1.0f);
		usersListLayout.addComponent(topLine);

		
		
        tableData = getTableData();

        //table with pipeline execution records
        usersTable = new IntlibPagedTable();
        usersTable.setSelectable(true);
        usersTable.setContainerDataSource(tableData);
        usersTable.setWidth("100%");
        usersTable.setHeight("100%");
        usersTable.setImmediate(true);
        usersTable.setVisibleColumns(visibleCols); // Set visible columns
        usersTable.setColumnHeaders(headers);

        //Actions column. Contains actions buttons: Debug data, Show log, Stop.
        usersTable.addGeneratedColumn("actions",
                new actionColumnGenerator());

        usersListLayout.addComponent(usersTable);
        usersListLayout.addComponent(usersTable.createControls());
        usersTable.setPageLength(10);
        usersTable.setFilterDecorator(new filterDecorator());
        usersTable.setFilterBarVisible(true);
        usersTable.setFilterFieldVisible("actions", false);

		

		
		return usersListLayout;
	}
	
	/**
	 * Calls for refresh table {@link #schedulerTable}.
	 */
	private void refreshData() {
		int page = usersTable.getCurrentPage();
		tableData = getTableData();
		usersTable.setContainerDataSource(tableData);
		usersTable.setCurrentPage(page);
		usersTable.setVisibleColumns(visibleCols);
		usersTable.setFilterFieldVisible("actions", false);

	}
	
    /**
     * Container with data for {@link #usersTable}
     *
     * @param data. List of users
     * @return result. IndexedContainer with data for users table
     */
	public static IndexedContainer getTableData() {

/*		 static String[] visibleCols = new String[]{"id", "user", "role",
		        "total_pipelines", "actions"};*/
		
		String[] id = { "1", "2", "3" };
		String[] user = { "tomask", "mariak", "jirit" };
		String[] role = { "administrator", "user", "user" };
		String[] total_pipelines = { "7", "2", "4" };

		IndexedContainer result = new IndexedContainer();

		for (String p : visibleCols) {
			result.addContainerProperty(p, String.class, "");
		}

		int max = getMinLength(id, user, role, total_pipelines);

		for (int i = 0; i < max; i++) {
			Object num = result.addItem();
			result.getContainerProperty(num, "id").setValue(id[i]);
			result.getContainerProperty(num, "user").setValue(user[i]);
			result.getContainerProperty(num, "role").setValue(role[i]);
			result.getContainerProperty(num, "total_pipelines").setValue(total_pipelines[i]);


		}

		return result;

	}
	
	  private final static int UNDEFINED_LENGTH = -1;
	
	  public static int getMinLength(String[]... arraysLength) {
	    int min = UNDEFINED_LENGTH;
	    for (int i = 0; i < arraysLength.length; i++) {
	      if (min == UNDEFINED_LENGTH) {
	        min = arraysLength[i].length;
	      } else {
	        min = Math.min(min, arraysLength[i].length);
	      }
	    }
	    return min;
	
	  } 
    
	/**
	 * Generate column "actions" in the table {@link #usersTable}.
	 *
	 * @author Maria Kukhar
	 *
	 */
	class actionColumnGenerator implements CustomTable.ColumnGenerator {

		private ClickListener clickListener = null;

		@Override
		public Object generateCell(final CustomTable source, final Object itemId,
				Object columnId) {

			HorizontalLayout layout = new HorizontalLayout();

		
			//Delete button. Delete user's record from Database.
			Button deleteButton = new Button();
			deleteButton.setCaption("Delete");
/*			deleteButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					userId = (Long) tableData.getContainerProperty(itemId, "id")
							.getValue();
					Schedule schedule = App.getApp().getSchedules().getSchedule(userId);
					App.getApp().getSchedules().delete(schedule);
					refreshData();
				}
			});*/
			layout.addComponent(deleteButton);
			
			//Delete button. Delete user's record from Database.
			Button changeButton = new Button();
			changeButton.setCaption("Change settings");
			changeButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {

					
					boolean newUser = false;
					// open usercreation dialog
					UserCreate user = new UserCreate(newUser);
					App.getApp().addWindow(user);
					user.addCloseListener(new CloseListener() {
						@Override
						public void windowClose(CloseEvent e) {
							refreshData();
						}
					});
				
					
				}
			});
			
			layout.addComponent(changeButton);

			return layout;
		}
	}
	
	private class filterDecorator extends IntlibFilterDecorator {

        @Override
        public String getEnumFilterDisplayName(Object propertyId, Object value) {
            if (propertyId == "role") {
                return ((PipelineExecutionStatus) value).name();
            }
            return super.getEnumFilterDisplayName(propertyId, value);
        }
	};
}
