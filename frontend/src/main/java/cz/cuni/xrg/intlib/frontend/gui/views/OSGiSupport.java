package cz.cuni.xrg.intlib.frontend.gui.views;

import java.io.File;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Set;

import org.osgi.framework.Bundle;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.auxiliaries.ModuleDialogGetter;
import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.osgi.Framework;
import cz.cuni.xrg.intlib.commons.app.module.osgi.OSGiException;

public class OSGiSupport extends CustomComponent implements View {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Button btnRefreshList;
	@AutoGenerated
	private Table table_1;
	@AutoGenerated
	private Label label_2;
	@AutoGenerated
	private Label label_1;
	@AutoGenerated
	private Button btnInstall;
	@AutoGenerated
	private Button btnInstallDirectory;
	@AutoGenerated
	private Button btnStart;
	@AutoGenerated
	private Button btnHeader;
	@AutoGenerated
	private Button btnDialog;
	@AutoGenerated
	private Button btnLoadDPU;
	@AutoGenerated
	private TextArea txtOutput;
	@AutoGenerated
	private TextField txtUri;
	@AutoGenerated
	private Label label_0;
	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);


		table_1.addContainerProperty("path", String.class,  null);
		table_1.addContainerProperty("SymbolicName", String.class,  null);

		final Framework frame = App.getApp().getModules().HACK_getFramework();
		final org.osgi.framework.launch.Framework osgiFrame = frame.HACK_getFramework();

		btnLoadDPU.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// load DPU
				txtOutput.setValue("");
				try {
					frame.loadDPU(txtUri.getValue());
				} catch (ModuleException e) {
					txtOutput.setValue(e.getMessage() + " > " + e.getOriginal().getMessage());
				} catch(Exception e) {
					txtOutput.setValue("Exceptin: " + e.getMessage());
				}

			}
		});

		btnDialog.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// load DPU and get dialog
				CustomComponent configComponenet = null;
				txtOutput.setValue("");
				try {
					DPUExecutive executive = frame.loadDPU(txtUri.getValue());
					configComponenet = ModuleDialogGetter.getDialog(executive);
				} catch (ModuleException e) {
					txtOutput.setValue(e.getMessage() + " > " + e.getOriginal().getMessage());
				} catch(Exception e) {
					txtOutput.setValue("Exceptin: " + e.getMessage());
				}

				if (configComponenet == null) {
					Notification.show("Loading dialog .. FAILED", "", Type.ERROR_MESSAGE);
				} else {
					Notification.show("Loading dialog .. OK", "", Type.HUMANIZED_MESSAGE);
				}

			}
		});

		btnHeader.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// show header for bundle
				txtOutput.setValue("");
				Bundle bundle = null;
				try {
					bundle = frame.installBundle(txtUri.getValue());
					// list header
					String headerText = "";
					Dictionary<String, String> headers = bundle.getHeaders();
					Enumeration<String> keys = headers.keys();
					while(keys.hasMoreElements()){
						String param = (String) keys.nextElement();
						headerText += param + ":" + headers.get(param).toString() + "\n";
					}
					//
					txtOutput.setValue(headerText);
				} catch (ModuleException e) {
					txtOutput.setValue(e.getMessage() + " > " + e.getOriginal().getMessage());
				} catch(Exception e) {
					txtOutput.setValue("Exception: " + e.getMessage());
				}
			}
		});

		btnInstall.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// install bundle
				txtOutput.setValue("");
				try {
					frame.installBundle(txtUri.getValue());
				} catch (ModuleException e) {
					txtOutput.setValue(e.getMessage() + " > " + e.getOriginal().getMessage());
				} catch(Exception e) {
					txtOutput.setValue("Exception: " + e.getMessage());
				}
			}
		});

		btnRefreshList.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// clear table
				table_1.removeAllItems();
				// refresh list of dpu
				java.util.Map<String, Bundle> installed = frame.HACK_installed();

				int id = 0;

				//Collection<Bundle> bundles = installed.keySet()();
				Set<String> bundlesKeys = installed.keySet();
				for (String key : bundlesKeys) {
					Bundle item = installed.get(key);

					String SymbolicName = (String)item.getHeaders().get("Bundle-SymbolicName");

					table_1.addItem(new Object[] {key, SymbolicName}, id++);
				}
			}
		});

		btnInstallDirectory.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String message = "";
				File directory = new File(txtUri.getValue());
				File[] fList = directory.listFiles();
				for (File file : fList){
					if (file.isFile()){
						if (file.getName().contains("jar")) {
							// load as bundle
							// install bundle
							String path = "file:///" + file.getAbsolutePath().replace('\\', '/');
							message += "loading: " + path + "\n";
							try {
								frame.installBundle( path );
							} catch (OSGiException e) {
								message += e.getMessage() + " > " + e.getOriginal().getMessage() + "\n";
							} catch(Exception e) {
								message += "Exception: " + e.getMessage() + "\n";
							}
						}

					}
				}
				txtOutput.setValue(message);
			}
		});

		btnStart.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// start bundle
				txtOutput.setValue("");
				try {
					frame.startBundle(txtUri.getValue());

					txtOutput.setValue("bundle started");
				} catch (ModuleException e) {
					txtOutput.setValue(e.getMessage() + " > " + e.getOriginal().getMessage());
				} catch(Exception e) {
					txtOutput.setValue("Exception: " + e.getMessage());
				}
			}
		});
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("1280px");
		mainLayout.setHeight("768px");

		// top-level component properties
		setWidth("1280px");
		setHeight("768px");

		// label_0
		label_0 = new Label();
		label_0.setImmediate(false);
		label_0.setWidth("-1px");
		label_0.setHeight("-1px");
		label_0.setValue("Uri:");
		mainLayout.addComponent(label_0, "top:20.0px;left:20.0px;");

		// txtUri
		txtUri = new TextField();
		txtUri.setImmediate(false);
		txtUri.setWidth("600px");
		txtUri.setHeight("-1px");
		mainLayout.addComponent(txtUri, "top:20.0px;left:80.0px;");

		// txtOutput
		txtOutput = new TextArea();
		txtOutput.setImmediate(false);
		txtOutput.setWidth("572px");
		txtOutput.setHeight("320px");
		mainLayout.addComponent(txtOutput, "top:122.0px;left:29.0px;");

		// btnLoadDPU
		btnLoadDPU = new Button();
		btnLoadDPU.setCaption("Load DPU");
		btnLoadDPU.setImmediate(true);
		btnLoadDPU.setWidth("-1px");
		btnLoadDPU.setHeight("-1px");
		mainLayout.addComponent(btnLoadDPU, "top:60.0px;left:100.0px;");

		// btnDialog
		btnDialog = new Button();
		btnDialog.setCaption("Show dialog");
		btnDialog.setImmediate(true);
		btnDialog.setWidth("-1px");
		btnDialog.setHeight("-1px");
		mainLayout.addComponent(btnDialog, "top:60.0px;left:200.0px;");

		// btnHeader
		btnHeader = new Button();
		btnHeader.setCaption("Show header");
		btnHeader.setImmediate(true);
		btnHeader.setWidth("-1px");
		btnHeader.setHeight("-1px");
		mainLayout.addComponent(btnHeader, "top:60.0px;left:320.0px;");

		// btnInstall
		btnInstall = new Button();
		btnInstall.setCaption("Install bundle");
		btnInstall.setImmediate(true);
		btnInstall.setWidth("-1px");
		btnInstall.setHeight("-1px");
		mainLayout.addComponent(btnInstall, "top:60.0px;left:440.0px;");

		// btnInstallDirectory
		btnInstallDirectory = new Button();
		btnInstallDirectory.setCaption("Install directory");
		btnInstallDirectory.setImmediate(true);
		btnInstallDirectory.setWidth("-1px");
		btnInstallDirectory.setHeight("-1px");
		mainLayout.addComponent(btnInstallDirectory, "top:60.0px;left:560.0px;");

		// btnStart
		btnStart = new Button();
		btnStart.setCaption("Start");
		btnStart.setImmediate(true);
		btnStart.setWidth("-1px");
		btnStart.setHeight("-1px");
		mainLayout.addComponent(btnStart, "top:60.0px;left:700.0px;");

		// label_1
		label_1 = new Label();
		label_1.setImmediate(false);
		label_1.setWidth("-1px");
		label_1.setHeight("-1px");
		label_1.setValue("Output:");
		mainLayout.addComponent(label_1, "top:100.0px;left:20.0px;");

		// label_2
		label_2 = new Label();
		label_2.setImmediate(false);
		label_2.setWidth("-1px");
		label_2.setHeight("-1px");
		label_2.setValue("Installed:");
		mainLayout.addComponent(label_2, "top:100.0px;left:631.0px;");

		// table_1
		table_1 = new Table();
		table_1.setImmediate(false);
		table_1.setWidth("600px");
		table_1.setHeight("320px");
		mainLayout.addComponent(table_1, "top:120.0px;left:640.0px;");

		// btnRefreshList
		btnRefreshList = new Button();
		btnRefreshList.setCaption("Refresh");
		btnRefreshList.setImmediate(true);
		btnRefreshList.setWidth("-1px");
		btnRefreshList.setHeight("-1px");
		mainLayout.addComponent(btnRefreshList, "top:454.0px;left:1107.0px;");

		return mainLayout;
	}

}
