package br.com.ieptbto.cra.page.cra;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.SolicitacaoCancelamento;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.StatusSolicitacaoCancelamento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.ConvenioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class GerarCancelamentosPage extends BasePage<SolicitacaoCancelamento> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	ConvenioMediator convenioMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoProtestoMediator;

	private SolicitacaoCancelamento solicitacaoCancelamento;
	private List<SolicitacaoCancelamento> solicitacoesCancelamentos;
	private Usuario usuario;

	public GerarCancelamentosPage() {
		this.solicitacaoCancelamento = new SolicitacaoCancelamento();
		this.usuario = getUser();

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formGerarCancelamentos();
		listaSolicitacoesCancelamento();
	}

	private void formGerarCancelamentos() {
		Form<SolicitacaoCancelamento> form = new Form<SolicitacaoCancelamento>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					convenioMediator.gerarCancelamentosConvenio(usuario, solicitacoesCancelamentos);
					success("Os arquivos de cancelamento de protesto foram gerados com sucesso!");

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					getFeedbackPanel().error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível gerar os arquivos de cancelamento de protesto! Favor entrar em contato com a CRA...");
				}
			}
		};
		add(form);
	}

	private void listaSolicitacoesCancelamento() {
		add(new ListView<SolicitacaoCancelamento>("listaTitulosCancelamento", buscarTituloParaCancelamento()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<SolicitacaoCancelamento> item) {
				final SolicitacaoCancelamento solicitacaoCancelamento = item.getModelObject();
				item.add(new Label("nossoNumero", solicitacaoCancelamento.getTituloRemessa().getNossoNumero()));
				item.add(new Label("numeroTitulo", solicitacaoCancelamento.getTituloRemessa().getNumeroTitulo()));
				item.add(new Label("pracaProtesto", solicitacaoCancelamento.getTituloRemessa().getPracaProtesto()));
				item.add(new Label("devedor", solicitacaoCancelamento.getTituloRemessa().getNomeDevedor()));
				item.add(new LabelValorMonetario<String>("valor", solicitacaoCancelamento.getTituloRemessa().getValorTitulo()));
				item.add(new Label("tipoSolicitacao", solicitacaoCancelamento.getStatusSolicitacaoCancelamento().getLabel()));
				if (solicitacaoCancelamento.getStatusSolicitacaoCancelamento() == StatusSolicitacaoCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO) {
					item.add(new Label("motivo", "Pagamento".toUpperCase()));
				} else {
					item.add(new Label("motivo", solicitacaoCancelamento.getCodigoIrregularidadeCancelamento().getMotivo().toUpperCase()));
				}
			}
		});

	}

	private IModel<List<SolicitacaoCancelamento>> buscarTituloParaCancelamento() {
		return new LoadableDetachableModel<List<SolicitacaoCancelamento>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<SolicitacaoCancelamento> load() {
				setSolicitacoesCancelamentos(cancelamentoProtestoMediator.buscarSolicitacoesCancelamentos());
				return solicitacoesCancelamentos;
			}
		};
	}

	public void setSolicitacoesCancelamentos(List<SolicitacaoCancelamento> titulosSolicitacaoCancelamento) {
		this.solicitacoesCancelamentos = titulosSolicitacaoCancelamento;
	}

	@Override
	protected IModel<SolicitacaoCancelamento> getModel() {
		return new CompoundPropertyModel<SolicitacaoCancelamento>(solicitacaoCancelamento);
	}
}
