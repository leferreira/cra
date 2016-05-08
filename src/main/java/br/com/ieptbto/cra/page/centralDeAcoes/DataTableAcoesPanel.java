package br.com.ieptbto.cra.page.centralDeAcoes;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.dataProvider.DataProvider;
import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.util.DataUtil;

public class DataTableAcoesPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DataProvider<LogCra> dataProvider;

	private Integer pagination;

	public DataTableAcoesPanel(String id, DataProvider<LogCra> dataProvider, Integer pagination) {
		super(id);
		this.dataProvider = dataProvider;
		this.pagination = pagination;
		adicionarComponentes();
	}

	private void adicionarComponentes() {
		add(dataTableLogCra());

	}

	private Component dataTableLogCra() {
		List<IColumn<LogCra, String>> columns = new ArrayList<>();
		columns.add(new PropertyColumn<LogCra, String>(new Model<String>("AÇÃO"), "acao.label"));
		columns.add(new PropertyColumn<LogCra, String>(new Model<String>("INSTITUIÇÃO"), "instituicao"));
		columns.add(new PropertyColumn<LogCra, String>(new Model<String>("DATA E HORA"), "data") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<LogCra>> item, String id, IModel<LogCra> model) {
				item.add(new Label(id, DataUtil.localDateToString(model.getObject().getData()) + " ás "
						+ DataUtil.localTimeToString(model.getObject().getHora())));
			}
		});
		columns.add(new PropertyColumn<LogCra, String>(new Model<String>("DESCRIÇÃO"), "descricao") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<LogCra>> item, String id, IModel<LogCra> rowModel) {
				if (rowModel.getObject().getDescricao().length() > 90) {
					item.add(new Label(id, rowModel.getObject().getDescricao().substring(0, 89)));
				} else {
					item.add(new Label(id, rowModel.getObject().getDescricao()));
				}
			}
		});
		columns.add(new PropertyColumn<LogCra, String>(new Model<String>("OCORRÊNCIA"), "ocorrencia") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}

			@Override
			public void populateItem(Item<ICellPopulator<LogCra>> item, String componentId, IModel<LogCra> rowModel) {
				Label label = new Label(componentId, rowModel.getObject().getTipoLog().getLabel());
				label.setMarkupId(rowModel.getObject().getTipoLog().getIdHtml());
				label.setOutputMarkupId(true);
				item.add(label);
			}
		});
		columns.add(new AbstractColumn<LogCra, String>(new Model<String>("AÇÕES")) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<LogCra>> cellItem, String id, IModel<LogCra> model) {
				cellItem.add(new LogCraActionPanel(id, model));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center col-action";
			}
		});
		return new DefaultDataTable<>("table", columns, dataProvider, getPagination());
	}

	public Integer getPagination() {
		if (pagination == null) {
			pagination = 10;
		}
		return pagination;
	}
}
