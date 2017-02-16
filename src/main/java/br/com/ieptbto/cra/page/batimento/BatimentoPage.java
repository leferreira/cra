package br.com.ieptbto.cra.page.batimento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.CheckboxPanel;
import br.com.ieptbto.cra.component.MultipleChoiceDepositosBatimentoPanel;
import br.com.ieptbto.cra.component.dataTable.CraLinksPanel;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.view.ViewBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
import br.com.ieptbto.cra.mediator.DepositoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

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
	private CheckGroup<ViewBatimentoRetorno> grupo;
	private ListView<ViewBatimentoRetorno> remessas;

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
				List<ViewBatimentoRetorno> arquivosSelecionados = (List<ViewBatimentoRetorno>) grupo.getModelObject();

				try {
					if (arquivosSelecionados.isEmpty()) {
						throw new InfraException("Ao menos um arquivo de retorno deve ser selecionado para realizar o batimento!");
					}
					for (ViewBatimentoRetorno batimentoRetorno : arquivosSelecionados) {
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
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento. Favor entrar em contato com a CRA...");
				}
			}
		};
		form.setOutputMarkupId(true);
		grupo = new CheckGroup<ViewBatimentoRetorno>("group", new ArrayList<ViewBatimentoRetorno>());
		form.add(grupo);
		form.add(carregarArquivosRetorno());
		grupo.add(remessas);
		form.add(carregarExtrato());
		return form;
	}
	
	private ListView<ViewBatimentoRetorno> carregarArquivosRetorno() {
		return remessas = new ListView<ViewBatimentoRetorno>("retornos", batimentoMediator.buscarRetornoBatimentoNaoConfimados()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<ViewBatimentoRetorno> item) {
				final ViewBatimentoRetorno object = item.getModelObject();
				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(object.getDataEnvio_Arquivo())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", object.getNomeFantasia_Cartorio()));
				item.add(new CraLinksPanel("linkArquivo", object.getNomeArquivo_Arquivo(), object.getIdRemessa_Remessa()));
				item.add(new CheckboxPanel<ViewBatimentoRetorno>("checkbox", item.getModel(), grupo));
				item.add(new LabelValorMonetario<BigDecimal>("valorCustas", object.getTotalCustasCartorio()));
				item.add(new LabelValorMonetario<BigDecimal>("valorPagos", object.getTotalValorlPagos()));
				item.add(new MultipleChoiceDepositosBatimentoPanel("depositos", item.getModel(), depositos));
				if (PeriodoDataUtil.diferencaDeDiasEntreData(object.getDataRecebimento_Arquivo(), new Date()) > 9) {
					item.setOutputMarkupId(true);
					item.setMarkupId("retornoPendente10Dias");
				}
			}
			
			@Override
			public String getMarkupId(boolean createIfDoesNotExist) {
				return super.getMarkupId(true);
			}
		};
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