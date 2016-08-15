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
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
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
public class GerarCancelamentosPage extends BasePage<PedidoAutorizacaoCancelamento> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	ConvenioMediator convenioMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoProtestoMediator;

	private PedidoAutorizacaoCancelamento pedidoAutorizacaoCancelamento;
	private List<TituloRemessa> titulosCancelamento;

	public GerarCancelamentosPage() {
		this.pedidoAutorizacaoCancelamento = new PedidoAutorizacaoCancelamento();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formGerarCancelamentos();
		listaPedidosCancelamento();

	}

	private void formGerarCancelamentos() {
		Form<TituloRemessa> form = new Form<TituloRemessa>("form") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					convenioMediator.gerarCancelamentos(getUser(), titulosCancelamento);
					success("Arquivos de cancelamento gerados com sucesso!");

				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					getFeedbackPanel().error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível gerar os arquivos de cancelamento ! \n Entre em contato com a CRA ");
				}
			}
		};
		add(form);
	}

	private void listaPedidosCancelamento() {
		add(new ListView<TituloRemessa>("listaTitulosCancelamento", buscarTituloParaCancelamento()) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa titulo = item.getModelObject();
				item.add(new Label("nossoNumero", titulo.getNossoNumero()));
				item.add(new Label("numeroTitulo", titulo.getNumeroTitulo()));
				item.add(new Label("pracaProtesto", titulo.getPracaProtesto()));
				item.add(new Label("devedor", titulo.getNomeDevedor()));
				item.add(new LabelValorMonetario<String>("valor", titulo.getValorTitulo()));
				item.add(new Label("tipoSolicitacao", titulo.getStatusSolicitacaoCancelamento().getLabel()));
				if (titulo.getStatusSolicitacaoCancelamento() == StatusSolicitacaoCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO) {
					item.add(new Label("motivo", "Pagamento".toUpperCase()));
				} else {
					item.add(new Label("motivo", titulo.getCodigoIrregularidadeCancelamento().getMotivo().toUpperCase()));
				}
			}
		});

	}

	private IModel<List<TituloRemessa>> buscarTituloParaCancelamento() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				titulosCancelamento = cancelamentoProtestoMediator.buscarCancelamentosSolicitados();
				return titulosCancelamento;
			}
		};
	}

	@Override
	protected IModel<PedidoAutorizacaoCancelamento> getModel() {
		return new CompoundPropertyModel<PedidoAutorizacaoCancelamento>(pedidoAutorizacaoCancelamento);
	}

}
