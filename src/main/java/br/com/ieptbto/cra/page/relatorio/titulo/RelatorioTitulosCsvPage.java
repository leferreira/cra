package br.com.ieptbto.cra.page.relatorio.titulo;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.TituloBean;
import br.com.ieptbto.cra.component.CustomExportToolbar;
import br.com.ieptbto.cra.dataProvider.DataProvider;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioTitulosCsvPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RelatorioMediator relatorioMediator;
	@SpringBean
	TituloMediator tituloMediator;

	private TituloRemessa tituloRemessa;
	private List<TituloRemessa> titulos;

	public RelatorioTitulosCsvPage(SituacaoTituloRelatorio situacaoTitulo, Instituicao instituicao, Instituicao cartorio,
			LocalDate dataInicio, LocalDate dataFim) {
		this.tituloRemessa = new TituloRemessa();
		this.titulos = relatorioMediator.relatorioTitulosPorSituacao(situacaoTitulo, instituicao, cartorio, dataInicio, dataFim);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		List<IColumn<TituloBean, String>> columns = new ArrayList<>();
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("BCO/CONV."), "apresentante"));
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("NOSSO N."), "nossoNumero") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("TÍTULO"), "numeroTitulo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("PROTOC."), "numeroProtocoloCartorio") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("MUNICÍPIO"), "municipio"));
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("DEVEDOR"), "nomeDevedor"));
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("VALOR"), "saldoTitulo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-right valor";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("REMESSA"), "remessa") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center valor";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("CONFIR."), "dataConfirmacao") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("RETORNO"), "retorno") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center valor";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("DATA OC."), "dataOcorrencia") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("DP"), "desistencia") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloBean, String>(new Model<String>("SITUAÇÃO"), "situacaoTitulo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}

			@Override
			public void populateItem(Item<ICellPopulator<TituloBean>> item, String componentId, IModel<TituloBean> rowModel) {
				Label label = new Label(componentId, rowModel.getObject().getSituacaoTitulo());
				label.setMarkupId("info-titulo");
				label.setOutputMarkupId(true);
				item.add(label);
			}
		});

		DataTable<TituloBean, String> dataTable =
				new DefaultDataTable<>("table", columns, new DataProvider<TituloBean>(TituloBean.parseToListTituloRemessa(titulos)), 1000);
		dataTable.addBottomToolbar(
				new CustomExportToolbar(dataTable, "CRA_RELATORIO_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_")));
		add(dataTable);
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}

}
