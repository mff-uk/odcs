package cz.cuni.xrg.intlib.frontend.gui.components;

import com.jensjansson.pagedtable.PagedTable;
import com.jensjansson.pagedtable.PagedTableContainer;
import com.vaadin.data.Container;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author Bogo
 */
public class IntlibPagedTable extends PagedTable {

//	private PagedTableContainer container;
//
//	@Override
//	public void setContainerDataSource(Container container)
//	{
//		this.container = new PagedTableContainer((Container.Indexed) container);
//		super.setContainerDataSource(container);
//	}
//
//	@Override
//public HorizontalLayout createControls() {
//		HorizontalLayout controlBar = new HorizontalLayout(); //super.createControls();
//		//controlBar.removeAllComponents();
//
//        Label pageLabel = new Label("Page:&nbsp;", ContentMode.HTML);
//        final TextField currentPageTextField = new TextField();
//        currentPageTextField.setValue(String.valueOf(getCurrentPage()));
//        currentPageTextField.setConverter(Integer.class);
//        currentPageTextField.addValidator(new IntegerRangeValidator("Wrong page number", 1, getTotalAmountOfPages()));
//        Label separatorLabel = new Label("&nbsp;/&nbsp;", ContentMode.HTML);
//        final Label totalPagesLabel = new Label(
//                String.valueOf(getTotalAmountOfPages()), ContentMode.HTML);
//        currentPageTextField.setStyleName(Reindeer.TEXTFIELD_SMALL);
//        currentPageTextField.setImmediate(true);
//        currentPageTextField.addValueChangeListener(new ValueChangeListener() {
//            private static final long serialVersionUID = -2255853716069800092L;
//
//			@Override
//            public void valueChange(
//                    com.vaadin.data.Property.ValueChangeEvent event) {
//                if (currentPageTextField.isValid()
//                        && currentPageTextField.getValue() != null) {
//                    int page = Integer.valueOf(String
//                            .valueOf(currentPageTextField.getValue()));
//                    setCurrentPage(page);
//                }
//            }
//        });
//        pageLabel.setWidth(null);
//        currentPageTextField.setWidth("20px");
//        separatorLabel.setWidth(null);
//        totalPagesLabel.setWidth(null);
//
//        HorizontalLayout pageSize = new HorizontalLayout();
//        HorizontalLayout pageManagement = new HorizontalLayout();
//        final Button first = new Button("<<", new ClickListener() {
//            private static final long serialVersionUID = -355520120491283992L;
//
//			@Override
//            public void buttonClick(ClickEvent event) {
//                setCurrentPage(0);
//            }
//        });
//        final Button previous = new Button("<", new ClickListener() {
//            private static final long serialVersionUID = -355520120491283992L;
//
//			@Override
//            public void buttonClick(ClickEvent event) {
//                previousPage();
//            }
//        });
//        final Button next = new Button(">", new ClickListener() {
//            private static final long serialVersionUID = -1927138212640638452L;
//
//			@Override
//            public void buttonClick(ClickEvent event) {
//                nextPage();
//            }
//        });
//        final Button last = new Button(">>", new ClickListener() {
//            private static final long serialVersionUID = -355520120491283992L;
//
//			@Override
//            public void buttonClick(ClickEvent event) {
//                setCurrentPage(getTotalAmountOfPages());
//            }
//        });
//        first.setStyleName(Reindeer.BUTTON_LINK);
//        previous.setStyleName(Reindeer.BUTTON_LINK);
//        next.setStyleName(Reindeer.BUTTON_LINK);
//        last.setStyleName(Reindeer.BUTTON_LINK);
//
//        pageLabel.addStyleName("pagedtable-pagecaption");
//        currentPageTextField.addStyleName("pagedtable-pagefield");
//        separatorLabel.addStyleName("pagedtable-separator");
//        totalPagesLabel.addStyleName("pagedtable-total");
//        first.addStyleName("pagedtable-first");
//        previous.addStyleName("pagedtable-previous");
//        next.addStyleName("pagedtable-next");
//        last.addStyleName("pagedtable-last");
//
//        pageLabel.addStyleName("pagedtable-label");
//        currentPageTextField.addStyleName("pagedtable-label");
//        separatorLabel.addStyleName("pagedtable-label");
//        totalPagesLabel.addStyleName("pagedtable-label");
//        first.addStyleName("pagedtable-button");
//        previous.addStyleName("pagedtable-button");
//        next.addStyleName("pagedtable-button");
//        last.addStyleName("pagedtable-button");
//
//        pageSize.setSpacing(true);
//        pageManagement.addComponent(first);
//        pageManagement.addComponent(previous);
//        pageManagement.addComponent(pageLabel);
//        pageManagement.addComponent(currentPageTextField);
//        pageManagement.addComponent(separatorLabel);
//        pageManagement.addComponent(totalPagesLabel);
//        pageManagement.addComponent(next);
//        pageManagement.addComponent(last);
//        pageManagement.setComponentAlignment(first, Alignment.MIDDLE_LEFT);
//        pageManagement.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
//        pageManagement.setComponentAlignment(pageLabel, Alignment.MIDDLE_LEFT);
//        pageManagement.setComponentAlignment(currentPageTextField,
//                Alignment.MIDDLE_LEFT);
//        pageManagement.setComponentAlignment(separatorLabel,
//                Alignment.MIDDLE_LEFT);
//        pageManagement.setComponentAlignment(totalPagesLabel,
//                Alignment.MIDDLE_LEFT);
//        pageManagement.setComponentAlignment(next, Alignment.MIDDLE_LEFT);
//        pageManagement.setComponentAlignment(last, Alignment.MIDDLE_LEFT);
//        pageManagement.setWidth(null);
//        pageManagement.setSpacing(true);
//        controlBar.addComponent(pageSize);
//        controlBar.addComponent(pageManagement);
//        controlBar.setComponentAlignment(pageManagement,
//                Alignment.MIDDLE_CENTER);
//        controlBar.setWidth("100%");
//        controlBar.setExpandRatio(pageSize, 1);
//		setPageLength(20);
//		addListener(new PageChangeListener() {
//			@Override
//            public void pageChanged(PagedTableChangeEvent event) {
//                first.setEnabled(container.getStartIndex() > 0);
//                previous.setEnabled(container.getStartIndex() > 0);
//                next.setEnabled(container.getStartIndex() < container
//                        .getRealSize() - getPageLength());
//                last.setEnabled(container.getStartIndex() < container
//                        .getRealSize() - getPageLength());
//                currentPageTextField.setValue(String.valueOf(getCurrentPage()));
//                totalPagesLabel.setValue(String.valueOf(getTotalAmountOfPages()));
//            }
//        });
//
//        return controlBar;
//    }


}
