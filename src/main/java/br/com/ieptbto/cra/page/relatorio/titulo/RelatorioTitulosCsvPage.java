package br.com.ieptbto.cra.page.relatorio.titulo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.TituloRelatorioCSVBean;
import br.com.ieptbto.cra.component.CustomExportToolbar;
import br.com.ieptbto.cra.dataProvider.DataProvider2;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
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
	private Boolean relatorioCraPendencias;

	public RelatorioTitulosCsvPage(SituacaoTituloRelatorio situacaoTitulo, TipoInstituicaoCRA tipoInstituicao, Instituicao instituicao, Instituicao cartorio,
			LocalDate dataInicio, LocalDate dataFim) {
		this.tituloRemessa = new TituloRemessa();
		this.relatorioCraPendencias = false;
		this.titulos = relatorioMediator.relatorioTitulosPorSituacao(situacaoTitulo, tipoInstituicao, instituicao, cartorio, dataInicio, dataFim);

		adicionarComponentes();
	}

	public RelatorioTitulosCsvPage(FileUpload fileUpload) {
		this.tituloRemessa = new TituloRemessa();
		this.relatorioCraPendencias = true;
		this.titulos = relatorioMediator.relatorioTitulosPlanilhaPendencias(fileUpload);

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		List<IColumn<TituloRelatorioCSVBean, String>> columns = new ArrayList<>();
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("BCO/CONV."), "apresentante"));
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("NOSSO N."), "nossoNumero") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("TÍTULO"), "numeroTitulo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("PROTOC."), "numeroProtocoloCartorio") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("MUNICÍPIO"), "municipio") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<TituloRelatorioCSVBean>> item, String id, IModel<TituloRelatorioCSVBean> model) {
				String municipio = model.getObject().getMunicipio();
				if (municipio.length() > 14) {
					municipio = municipio.substring(0, 14);
				}
				item.add(new Label(id, municipio));
			}
		});
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("Nº DEV"), "numeroControleDevedor") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<TituloRelatorioCSVBean>> item, String id, IModel<TituloRelatorioCSVBean> model) {
				item.add(new Label(id, model.getObject().getNumeroControleDevedor()));
			}
		});
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("DEVEDOR"), "nomeDevedor") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<TituloRelatorioCSVBean>> item, String id, IModel<TituloRelatorioCSVBean> model) {
				String nomeDevedor = model.getObject().getNomeDevedor();
				if (nomeDevedor.length() > 25) {
					nomeDevedor = nomeDevedor.substring(0, 24);
				}
				item.add(new Label(id, nomeDevedor));
			}
		});
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("VALOR"), "saldoTitulo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-right valor";
			}
		});
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("REMESSA"), "remessa") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center valor";
			}
		});
		if (relatorioCraPendencias == false) {
			columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("CONFIR."), "dataConfirmacao") {
				/***/
				private static final long serialVersionUID = 1L;

				@Override
				public String getCssClass() {
					return "text-center";
				}
			});
			columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("RETORNO"), "retorno") {
				/***/
				private static final long serialVersionUID = 1L;

				@Override
				public String getCssClass() {
					return "text-center valor";
				}
			});
			columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("DATA OC."), "dataOcorrencia") {
				/***/
				private static final long serialVersionUID = 1L;

				@Override
				public String getCssClass() {
					return "text-center";
				}
			});
		} else {
			columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("CONF. LIB."), "dataConfirmacao") {
				/***/
				private static final long serialVersionUID = 1L;

				@Override
				public String getCssClass() {
					return "text-center";
				}

				@Override
				public void populateItem(Item<ICellPopulator<TituloRelatorioCSVBean>> item, String id, IModel<TituloRelatorioCSVBean> model) {
					String confirmacao = StringUtils.EMPTY;
					if (model.getObject().getTitulo().getConfirmacao() != null) {
						if (model.getObject().getTitulo().getConfirmacao().getRemessa().getSituacao().equals(true)) {
							item.add(new Label(id, model.getObject().getTitulo().getConfirmacao().getRemessa().getArquivoGeradoProBanco().getNomeArquivo()));
						} else {
							item.add(new Label(id, confirmacao));
						}
					} else {
						item.add(new Label(id, confirmacao));
					}
				}
			});
			columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("RET. LIB."), "retorno") {
				/***/
				private static final long serialVersionUID = 1L;

				@Override
				public String getCssClass() {
					return "text-center valor";
				}

				@Override
				public void populateItem(Item<ICellPopulator<TituloRelatorioCSVBean>> item, String id, IModel<TituloRelatorioCSVBean> model) {
					String retorno = StringUtils.EMPTY;
					if (model.getObject().getTitulo().getRetorno() != null) {
						if (model.getObject().getTitulo().getRetorno().getRemessa().getSituacao().equals(true)) {
							item.add(new Label(id, model.getObject().getTitulo().getRetorno().getRemessa().getArquivoGeradoProBanco().getNomeArquivo()));
						} else {
							item.add(new Label(id, retorno));
						}
					} else {
						item.add(new Label(id, retorno));
					}
				}
			});
		}
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("DP"), "desistencia") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<TituloRelatorioCSVBean, String>(new Model<String>("SITUAÇÃO"), "situacaoTitulo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}

			@Override
			public void populateItem(Item<ICellPopulator<TituloRelatorioCSVBean>> item, String componentId, IModel<TituloRelatorioCSVBean> rowModel) {
				Label label = new Label(componentId, rowModel.getObject().getSituacaoTitulo());
				label.setMarkupId("info-titulo");
				label.setOutputMarkupId(true);
				item.add(label);
			}
		});

		DataTable<TituloRelatorioCSVBean, String> dataTable = new DefaultDataTable<>("table", columns,
				new DataProvider2<TituloRelatorioCSVBean>(TituloRelatorioCSVBean.parseToListTituloRemessa(titulos)), 10000);
		dataTable.addBottomToolbar(new CustomExportToolbar(dataTable, "CRA_RELATORIO_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_")));
		add(dataTable);
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}

}