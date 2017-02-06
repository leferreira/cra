package br.com.ieptbto.cra.component.dataTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.com.ieptbto.cra.component.CustomExportToolbar;
import br.com.ieptbto.cra.dataProvider.DataProvider;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 * @param <T>
 */
public class CraDataTable<T> extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	private static final String CRA_DATA_TABLE_JS = "cra_data_table/cra_data_table.js";
	private DataTable<T, String> dataTable;
	private List<IColumn<T, String>> columns;
	private DataProvider<T> provider;
	private Integer paggination;
	private boolean exporter;

	public CraDataTable(String id, IModel<T> model, List<IColumn<T, String>> columns, DataProvider<T> provider) {
		super(id, model);
		this.provider = provider;
		this.columns = columns;
		this.paggination = 10;
		add(formFilter());
		add(dataTable());
	}
	
	public CraDataTable(String id, IModel<T> model, List<IColumn<T, String>> columns, DataProvider<T> provider, boolean exporter) {
		super(id, model);
		this.provider = provider;
		this.columns = columns;
		this.paggination = 10;
		this.exporter = exporter;
		add(formFilter());
		add(dataTable());
	}

	private DataTable<T, String> dataTable() {
		this.dataTable = new DataTable<T, String>("table", columns, provider, paggination);
		this.dataTable.setMarkupId("dataTable");
		this.dataTable.setOutputMarkupId(true);
		if (exporter) {
			this.dataTable.addTopToolbar(new CustomExportToolbar(dataTable, 
					new Model<String>(provider.getFilterState().getClass().getSimpleName() + "_" + DataUtil.getDataAtual().replace("/", "-"))));
		}
		this.dataTable.addTopToolbar(new HeadersToolbar<String>(dataTable, provider));
		this.dataTable.addBottomToolbar(new NavigationToolbar(dataTable));

        List<Integer> arrayList = new ArrayList<Integer>(Arrays.asList(10,25,50,100));
        final DropDownChoice<Integer> dropDownItensPerPage = new DropDownChoice<Integer>("itens", new Model<Integer>(paggination), arrayList);
        dropDownItensPerPage.add(new OnChangeAjaxBehavior(){

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Integer paggination = dropDownItensPerPage.getModelObject();
				dataTable.setItemsPerPage(paggination);
				target.appendJavaScript("initCraDatatable();");
				target.add(dataTable);
			}
		});
		add(dropDownItensPerPage);
        return dataTable;
	}

	private FilterForm<T> formFilter() {
		FilterForm<T> filterForm = new FilterForm<T>("filterForm", provider);
		filterForm.setOutputMarkupId(true);
		filterForm.add(new TextField<String>("search", PropertyModel.<String>of(provider, "genericFilter")));
		return filterForm;
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		 response.render(JavaScriptReferenceHeaderItem.forUrl(CRA_DATA_TABLE_JS));
		 response.render(OnDomReadyHeaderItem.forScript("initCraDatatable()"));
	}
}