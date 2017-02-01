package br.com.ieptbto.cra.page.batimento;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.CheckboxPanel;
import br.com.ieptbto.cra.component.CraDataTable;
import br.com.ieptbto.cra.component.DataTableLinksPanel;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.dataProvider.BatimentoProvider;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.ViewBatimento;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
import br.com.ieptbto.cra.mediator.DepositoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class BatimentoPage extends BasePage<Remessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	BatimentoMediator batimentoMediator;
	@SpringBean
	DepositoMediator depositoMediator;
	private Remessa remessa;
	private List<Deposito> depositos;
	private CheckGroup<ViewBatimento> grupo;
	private CraDataTable<ViewBatimento> dataTable;

	public BatimentoPage() {
		this.remessa = new Remessa();
		this.depositos = depositoMediator.buscarDepositosNaoIdentificados();
		adicionarComponentes();
	}
	
	public BatimentoPage(String message) {
		this.remessa = new Remessa();
		this.depositos = depositoMediator.buscarDepositosNaoIdentificados();
		success(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formularioBatimento());
	}

	private Form<Remessa> formularioBatimento() {
		Form<Remessa> form = new Form<Remessa>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				List<ViewBatimento> arquivosSelecionados = (List<ViewBatimento>) grupo.getModelObject();

				try {
					if (arquivosSelecionados.isEmpty()) {
						throw new InfraException("Ao menos um arquivo de retorno deve ser selecionado para realizar o batimento!");
					}
					for (ViewBatimento batimentoRetorno : arquivosSelecionados) {
						if (batimentoRetorno.getListaDepositos().isEmpty()) {
							TipoBatimento tipoBatimento = TipoBatimento.getTipoBatimento(batimentoRetorno.getTipoBatimento_Instituicao());
							if (!tipoBatimento.equals(TipoBatimento.LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO)) {
								throw new InfraException("O arquivo " + batimentoRetorno.getNomeArquivo_Arquivo() + " do "
										+ batimentoRetorno.getNomeFantasia_Cartorio() + " foi selecionado e não existe depósito vículado! Por favor, selecione novamente o depósito...");
							}
						}
					}
					batimentoMediator.salvarBatimentos(arquivosSelecionados);
					setResponsePage(new BatimentoPage("O Batimento dos Arquivos de Retorno foi salvo com sucesso!"));

				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento. Favor entrar em contato com a CRA...");
				}
			}
		};
		form.setOutputMarkupId(true);
		grupo = new CheckGroup<ViewBatimento>("group", new ArrayList<ViewBatimento>());
		form.add(grupo);
		form.add(tableArquivosRetornoBatimento());
		grupo.add(dataTable);
		form.add(carregarExtrato());
		return form;
	}
	
	private CraDataTable<ViewBatimento> tableArquivosRetornoBatimento() {
		BatimentoProvider dataProvider = new BatimentoProvider(batimentoMediator.buscarArquivosViewBatimento());
		List<IColumn<ViewBatimento, String>> columns = new ArrayList<IColumn<ViewBatimento, String>>();
		columns.add(new PropertyColumn<ViewBatimento, String>(new Model<String>("DATA"), "dataEnvio"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimento>> item, String id, IModel<ViewBatimento> model) {
				item.add(new Label(id, DataUtil.localDateToString(model.getObject().getDataEnvio_Arquivo())));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center";
			}
		});
		columns.add(new PropertyColumn<ViewBatimento, String>(new Model<String>("CARTÓRIO"), "nomeFantasia_Cartorio"));
		columns.add(new PropertyColumn<ViewBatimento, String>(new Model<String>("ARQUIVO"), "nomeArquivo_Arquivo") {
		
			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimento>> item, String id, IModel<ViewBatimento> model) {
				item.add(new DataTableLinksPanel(id, model.getObject().getNomeArquivo_Arquivo(), model.getObject().getIdRemessa_Remessa()));
			}

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewBatimento, String>(new Model<String>(" "), "check"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimento>> item, String id, IModel<ViewBatimento> model) {
				item.add(new CheckboxPanel<ViewBatimento>(id, model, grupo));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center";
			}
		});
		columns.add(new PropertyColumn<ViewBatimento, String>(new Model<String>("CUSTAS CART."), "check"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimento>> item, String id, IModel<ViewBatimento> model) {
				item.add(new LabelValorMonetario<>(id, model.getObject().getTotalCustasCartorio()));
			}

			@Override
			public String getCssClass() {
				return "col-right valor";
			}
		});		
		columns.add(new PropertyColumn<ViewBatimento, String>(new Model<String>("VALOR PAGOS"), "check"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimento>> item, String id, IModel<ViewBatimento> model) {
				item.add(new LabelValorMonetario<>(id, model.getObject().getTotalValorlPagos()));
			}

			@Override
			public String getCssClass() {
				return "col-right valor";
			}
		});
		columns.add(new PropertyColumn<ViewBatimento, String>(new Model<String>("DEPÓSITOS"), "check"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimento>> item, String id, IModel<ViewBatimento> model) {
				item.add(new SelectDepositosBatimentoPanel(id, model, depositos));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center";
			}
		});
		dataTable = new CraDataTable<ViewBatimento>("dataTable", columns, dataProvider, 15);
		dataTable.setOutputMarkupPlaceholderTag(true);
		dataTable.setOutputMarkupId(true);
		return dataTable;
	}

	private ListView<Deposito> carregarExtrato() {
		return new ListView<Deposito>("extrato", getDepositos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Deposito> item) {
				final Deposito deposito = item.getModelObject();
				item.add(new Label("data", DataUtil.localDateToString(deposito.getData())));
				item.add(new Label("lancamento", deposito.getLancamento().toUpperCase()));
				item.add(new Label("valor", "R$ " + deposito.getValorCredito()));
				if (StringUtils.isBlank(deposito.getDescricao())) {
					item.add(new Label("tooltip", "").setVisible(false));
				} else {
					item.add(new Label("tooltip", deposito.getDescricao()));
				}
			}
		};
	}

	public List<Deposito> getDepositos() {
		return depositos;
	}

	@Override
	protected IModel<Remessa> getModel() {
		return new CompoundPropertyModel<Remessa>(remessa);
	}
}