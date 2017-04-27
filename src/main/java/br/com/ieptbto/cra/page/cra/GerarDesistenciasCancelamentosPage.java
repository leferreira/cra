package br.com.ieptbto.cra.page.cra;

import br.com.ieptbto.cra.component.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConvenioMediator;
import br.com.ieptbto.cra.mediator.SolicitacaoDesistenciaCancelamentoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class GerarDesistenciasCancelamentosPage extends BasePage<SolicitacaoDesistenciaCancelamento> {

	@SpringBean
	private ConvenioMediator convenioMediator;
	@SpringBean
	private SolicitacaoDesistenciaCancelamentoMediator solicitacaoMediator;

	static final long serialVersionUID = 1L;
	private List<SolicitacaoDesistenciaCancelamento> solicitacoes;
	private Usuario usuario;

	public GerarDesistenciasCancelamentosPage() {
		this.usuario = getUser();
		this.solicitacoes = solicitacaoMediator.buscarSolicitacoesDesistenciasCancelamentos();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formGerarDesistenciasCancelamentos();
		listaSolicitacoesDesistenciasCancelamento();
	}

	private void formGerarDesistenciasCancelamentos() {
		Form<SolicitacaoDesistenciaCancelamento> form = new Form<SolicitacaoDesistenciaCancelamento>("form", getModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					convenioMediator.gerarDesistenciasCancelamentosConvenio(usuario, solicitacoes);
					solicitacoes.clear();
					success("Os arquivos de desistências e cancelamentos de protesto foram gerados com sucesso!");

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					getFeedbackPanel().error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível gerar os arquivos de desistência e cancelamento de protesto! Favor entrar em contato com a CRA...");
				}
			}
		};
		add(form);
	}

	private void listaSolicitacoesDesistenciasCancelamento() {
		add(new ListView<SolicitacaoDesistenciaCancelamento>("listaDesistenciasCancelamento", solicitacoes) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<SolicitacaoDesistenciaCancelamento> item) {
				final SolicitacaoDesistenciaCancelamento solicitacaoCancelamento = item.getModelObject();
				item.add(new Label("nossoNumero", solicitacaoCancelamento.getTituloRemessa().getNossoNumero()));
				item.add(new Label("numeroTitulo", solicitacaoCancelamento.getTituloRemessa().getNumeroTitulo()));
				item.add(new Label("pracaProtesto", solicitacaoCancelamento.getTituloRemessa().getPracaProtesto().toUpperCase()));
				item.add(new Label("devedor", solicitacaoCancelamento.getTituloRemessa().getNomeDevedor()));
				item.add(new LabelValorMonetario<String>("valor", solicitacaoCancelamento.getTituloRemessa().getSaldoTitulo()));
				item.add(new Label("tipoSolicitacao", solicitacaoCancelamento.getTipoSolicitacao().getDescricao()));
			}
		});
	}

	@Override
	protected IModel<SolicitacaoDesistenciaCancelamento> getModel() {
		return null;
	}
}
