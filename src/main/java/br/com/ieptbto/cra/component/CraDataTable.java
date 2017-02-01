package br.com.ieptbto.cra.component;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.com.ieptbto.cra.dataProvider.DataProvider;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
public class CraDataTable<T> extends Panel{

	/***/
	private static final long serialVersionUID = 1L;
	private List<IColumn<T, String>> columns;
	private DataProvider<T> provider;
	private Integer paggination;

	public CraDataTable(String id, List<IColumn<T, String>> columns, DataProvider<T> provider, Integer rowsPerPage) {
		super(id);
		this.provider = provider;
		this.columns = columns;
		this.paggination = rowsPerPage;
		add(formFilter());
		add(dataTable());
	}
	
	private DataTable<T, String> dataTable() {
		DataTable<T, String> dataTable = new DataTable<T, String>("table", columns, provider, paggination);
        dataTable.setOutputMarkupId(true);
        dataTable.addBottomToolbar(new NavigationToolbar(dataTable));
        dataTable.addTopToolbar(new HeadersToolbar<String>(dataTable, provider));
		return dataTable;
	}

	private FilterForm<T> formFilter() {
		FilterForm<T> filterForm = new FilterForm<T>("filterForm", provider);
		filterForm.setOutputMarkupId(true);
		filterForm.add(new TextField<String>("search", PropertyModel.<String>of(provider, "genericFilter")));
		return filterForm;
	}
}