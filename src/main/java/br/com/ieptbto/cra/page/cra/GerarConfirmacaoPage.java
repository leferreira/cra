package br.com.ieptbto.cra.page.cra;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfirmacaoMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */

@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class GerarConfirmacaoPage extends BasePage<Confirmacao> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	ConfirmacaoMediator confirmacaoMediator;
	private Confirmacao confirmacao;
	private ListView<Remessa> confirmacoesPendentes;

	public GerarConfirmacaoPage() {
		this.confirmacao = new Confirmacao();
		adicionarComponentes();
	}

	public GerarConfirmacaoPage(String message) {
		this.confirmacao = new Confirmacao();
		success(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formGerarConfirmacao());
	}

	private Form<Confirmacao> formGerarConfirmacao() {
		Form<Confirmacao> form = new Form<Confirmacao>("formConfirmacao") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				List<Remessa> confirmacoes = confirmacoesPendentes.getModelObject();
				
				try {
					if (confirmacaoMediator.verificarArquivoConfirmacaoCra().equals(true)) {
						throw new InfraException("Não é possível gerar as confirmações novamente, arquivos já liberados na data atual!");
					}
					if (confirmacoes.isEmpty()) {
						throw new InfraException("Não há confirmações pendentes para liberação!");
					}
					confirmacaoMediator.gerarConfirmacoes(getUser(), confirmacoes);
					setResponsePage(new GerarConfirmacaoPage("Os arquivos de confirmações foram gerados com sucesso !"));

				} catch (InfraException e) {
					logger.error(e.getMessage(), e);
					error(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível gerar a confirmação! Entre em contato com a CRA.");
				}
			}
		};
		form.add(carregarListaConfirmacao());
		form.add(new Button("botaoConfirmacao"));
		return form;
	}

	private ListView<Remessa> carregarListaConfirmacao() {
		return confirmacoesPendentes = new ListView<Remessa>("confirmacao", buscarConfirmacoesParaLiberacao()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa retorno = item.getModelObject();
				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(retorno.getArquivo().getHoraEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				item.add(new Label("instituicaoDestino.nomeFantasia", retorno.getInstituicaoDestino().getNomeFantasia()));
				item.add(new Label("sequencialCabecalho", retorno.getCabecalho().getNumeroSequencialRemessa()));
				Link<Remessa> linkArquivo = new Link<Remessa>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new TitulosArquivoPage(retorno));
					}
				};
				linkArquivo.add(new Label("arquivo.nomeArquivo", retorno.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
			}
		};
	}

	public IModel<List<Remessa>> buscarConfirmacoesParaLiberacao() {
		return new LoadableDetachableModel<List<Remessa>>() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Remessa> load() {
				return confirmacaoMediator.buscarConfirmacoesPendentesDeEnvio();
			}
		};
	}

	@Override
	protected IModel<Confirmacao> getModel() {
		return new CompoundPropertyModel<Confirmacao>(confirmacao);
	}
}
