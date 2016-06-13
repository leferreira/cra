package br.com.ieptbto.cra.page.batimento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.cra.MensagemPage;
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
	private static final Logger logger = Logger.getLogger(BatimentoPage.class);

	@SpringBean
	BatimentoMediator batimentoMediator;
	@SpringBean
	RetornoMediator retornoMediator;

	private Remessa batimento;
	private List<Deposito> depositos;
	private ListView<Remessa> remessas;

	public BatimentoPage() {
		this.batimento = new Remessa();
		this.depositos = batimentoMediator.buscarDepositosExtrato();

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormularioBatimento();

	}

	private void carregarFormularioBatimento() {
		final CheckGroup<Remessa> grupo = new CheckGroup<Remessa>("group", new ArrayList<Remessa>());
		Form<Remessa> form = new Form<Remessa>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				List<Remessa> arquivosRetornoSelecionados = (List<Remessa>) grupo.getModelObject();

				try {
					if (arquivosRetornoSelecionados.isEmpty() || arquivosRetornoSelecionados.size() == 0) {
						throw new InfraException("Ao menos um retorno deve ser selecionado!");
					}
					for (Remessa retorno : arquivosRetornoSelecionados) {
						if (retorno.getListaDepositos().isEmpty()) {
							if (!retorno.getInstituicaoDestino().getTipoBatimento().equals(TipoBatimento.LIBERACAO_SEM_IDENTIFICAÇÃO_DE_DEPOSITO)) {
								throw new InfraException("O arquivo " + retorno.getArquivo().getNomeArquivo() + " do "
										+ retorno.getInstituicaoOrigem().getNomeFantasia()
										+ " foi selecionado e não existe depósito vículado! Por favor, selecione novamente o depósito...");
							}
						}
					}
					retornoMediator.salvarBatimentos(arquivosRetornoSelecionados);
					setResponsePage(
							new MensagemPage<Batimento>(BatimentoPage.class, "BATIMENTO", "Batimento dos arquivos de retorno salvo com sucesso!"));

				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento! Entre em contato com a CRA.");
				}
			}
		};

		form.add(grupo);
		form.add(carregarArquivosRetorno());
		form.add(carregarExtrato());

		remessas.setReuseItems(true);
		grupo.add(remessas);
		add(form);
	}

	private ListView<Remessa> carregarArquivosRetorno() {
		return remessas = new ListView<Remessa>("retornos", retornoMediator.buscarRetornosParaBatimento()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa retorno = item.getModelObject();
				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(retorno));
					}
				};
				linkArquivo.add(new Label("arquivo.nomeArquivo", retorno.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				BigDecimal valorPagos = retornoMediator.buscarValorDeTitulosPagos(retorno);
				if (valorPagos == null || valorPagos.equals(BigDecimal.ZERO)) {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", BigDecimal.ZERO));
				} else {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", valorPagos));
				}
				ArrayList<Deposito> depositos = new ArrayList<Deposito>();
				retorno.setListaDepositos(depositos);
				item.add(new Check<Remessa>("checkbox", item.getModel()));
				item.add(new ListMultipleChoice<Deposito>("depositos", new Model<ArrayList<Deposito>>(depositos), getDepositos()));

				if (PeriodoDataUtil.diferencaDeDiasEntreData(retorno.getDataRecebimento().toDate(), new Date()) > 9) {
					item.setOutputMarkupId(true);
					item.setMarkupId("retornoPendente10Dias");
				}
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
		return new CompoundPropertyModel<Remessa>(batimento);
	}
}