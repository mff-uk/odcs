package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Reindeer;
import java.text.NumberFormat;
import java.util.Locale;
import org.tepi.filtertable.paged.PagedFilterTable;
import org.tepi.filtertable.paged.PagedFilterTableContainer;
import org.tepi.filtertable.paged.PagedTableChangeEvent;

/**
 * Intlib extension of FilterTable add-on. PagedFilterTable provides paging for
 * custom Vaadin Table.
 *
 * @author Bogo
 */
public class IntlibPagedTable extends PagedFilterTable {
	
	public IntlibPagedTable() {
		super();
		setFilterDecorator(new IntlibFilterDecorator());
	}

	/**
	 * Creates controls for navigating between pages of table. Hides the page size selector.
	 * @return {@link ControlsLayout} for accessing table controls.
	 */
	@Override
	public HorizontalLayout createControls() {
		
        Label pageLabel = new Label("Page:&nbsp;", ContentMode.HTML);
        final TextField currentPageTextField = new TextField();
        currentPageTextField.setValue(String.valueOf(getCurrentPage()));
        currentPageTextField.setConverter(new StringToIntegerConverter() {
            @Override
            protected NumberFormat getFormat(Locale locale) {
                return super.getFormat(UI.getCurrent().getLocale());
            }
        });
        Label separatorLabel = new Label("&nbsp;/&nbsp;", ContentMode.HTML);
        final Label totalPagesLabel = new Label(
                String.valueOf(getTotalAmountOfPages()), ContentMode.HTML);
        currentPageTextField.setStyleName(Reindeer.TEXTFIELD_SMALL);
        currentPageTextField.setImmediate(true);
        currentPageTextField.addValueChangeListener(new ValueChangeListener() {
            private static final long serialVersionUID = -2255853716069800092L;

			@Override
            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                if (currentPageTextField.isValid()
                        && currentPageTextField.getValue() != null) {
					Object value = currentPageTextField.getConvertedValue();
                    int page = (int)value;
                    setCurrentPage(page);
                }
            }
        });
        pageLabel.setWidth(null);
        currentPageTextField.setColumns(3);
        separatorLabel.setWidth(null);
        totalPagesLabel.setWidth(null);

        HorizontalLayout controlBar = new HorizontalLayout();
        HorizontalLayout pageManagement = new HorizontalLayout();
        final Button first = new Button("<<", new Button.ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

			@Override
            public void buttonClick(Button.ClickEvent event) {
                setCurrentPage(0);
            }
        });
        final Button previous = new Button("<", new Button.ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

			@Override
            public void buttonClick(Button.ClickEvent event) {
                previousPage();
            }
        });
        final Button next = new Button(">", new Button.ClickListener() {
            private static final long serialVersionUID = -1927138212640638452L;

			@Override
            public void buttonClick(Button.ClickEvent event) {
                nextPage();
            }
        });
        final Button last = new Button(">>", new Button.ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

			@Override
            public void buttonClick(Button.ClickEvent event) {
                setCurrentPage(getTotalAmountOfPages());
            }
        });
        first.setStyleName(Reindeer.BUTTON_LINK);
        previous.setStyleName(Reindeer.BUTTON_LINK);
        next.setStyleName(Reindeer.BUTTON_LINK);
        last.setStyleName(Reindeer.BUTTON_LINK);

        pageLabel.addStyleName("pagedtable-pagecaption");
        currentPageTextField.addStyleName("pagedtable-pagefield");
        separatorLabel.addStyleName("pagedtable-separator");
        totalPagesLabel.addStyleName("pagedtable-total");
        first.addStyleName("pagedtable-first");
        previous.addStyleName("pagedtable-previous");
        next.addStyleName("pagedtable-next");
        last.addStyleName("pagedtable-last");

        pageLabel.addStyleName("pagedtable-label");
        currentPageTextField.addStyleName("pagedtable-label");
        separatorLabel.addStyleName("pagedtable-label");
        totalPagesLabel.addStyleName("pagedtable-label");
        first.addStyleName("pagedtable-button");
        previous.addStyleName("pagedtable-button");
        next.addStyleName("pagedtable-button");
        last.addStyleName("pagedtable-button");

        pageManagement.addComponent(first);
        pageManagement.addComponent(previous);
        pageManagement.addComponent(pageLabel);
        pageManagement.addComponent(currentPageTextField);
        pageManagement.addComponent(separatorLabel);
        pageManagement.addComponent(totalPagesLabel);
        pageManagement.addComponent(next);
        pageManagement.addComponent(last);
        pageManagement.setComponentAlignment(first, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(pageLabel, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(currentPageTextField,
                Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(separatorLabel,
                Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(totalPagesLabel,
                Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(next, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(last, Alignment.MIDDLE_LEFT);
        pageManagement.setWidth(null);
        pageManagement.setSpacing(true);
        controlBar.addComponent(pageManagement);
        controlBar.setComponentAlignment(pageManagement,
                Alignment.MIDDLE_CENTER);
        controlBar.setWidth(100, Unit.PERCENTAGE);
        addListener(new PageChangeListener() {
            private boolean inMiddleOfValueChange;

			@Override
            public void pageChanged(PagedTableChangeEvent event) {
                if (!inMiddleOfValueChange) {
                    inMiddleOfValueChange = true;
					PagedFilterTableContainer container = getContainerDataSource();
                    first.setEnabled(container.getStartIndex() > 0);
                    previous.setEnabled(container.getStartIndex() > 0);
                    next.setEnabled(container.getStartIndex() < container
                            .getRealSize() - getPageLength());
                    last.setEnabled(container.getStartIndex() < container
                            .getRealSize() - getPageLength());
                    currentPageTextField.setValue(String
                            .valueOf(getCurrentPage()));
                    totalPagesLabel.setValue(Integer
                            .toString(getTotalAmountOfPages()));
                    inMiddleOfValueChange = false;
                }
            }
        });
        return controlBar;
	}
        
        public void setFilterLayout() {
            for (Object id : getVisibleColumns()) {
                Component filterField = getFilterField(id);
                if(filterField != null) {
                    filterField.setWidth(100, Unit.PERCENTAGE);
                    filterField.setHeight(100, Unit.PERCENTAGE);
                }
            }
        }

    @Override
    public void setFilterBarVisible(boolean filtersVisible) {
        super.setFilterBarVisible(filtersVisible);
        setFilterLayout();
    }

    @Override
    public void resetFilters() {
        super.resetFilters(); 
        setFilterLayout();
    }
	
    
    
        
        
	
}
