package br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.beans.TituloBean;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.BuscarTitulosInputPanel;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class SolicitarDesistenciaCancelamentoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	MunicipioMediator municipioMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	private TituloRemessa titulo;
	private Form<TituloBean> form;

	public SolicitarDesistenciaCancelamentoPage() {
		this.titulo = new TituloRemessa();

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		setForm();
	}

	private void setForm() {
		TituloBean beanForm = new TituloBean();
		this.form = new Form<TituloBean>("form", new CompoundPropertyModel<TituloBean>(beanForm)) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				TituloBean tituloBean = getModelObject();

				try {
					if (tituloBean.isTodosCamposEmBranco()) {
						throw new InfraException(
								"Por favor, preencha um ou mais campos para realizar a busca, ou informe o período com um Banco/Convênio ou Município!");
					}
					setResponsePage(new ListaTitulosDesistenciaCancelamentoPage(tituloBean));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca de títulos para cancelamento. Favor entrar em contato com a CRA!");
				}
			}
		};
		form.add(new BuscarTitulosInputPanel("buscarTitulosInputPanel", new CompoundPropertyModel<TituloBean>(beanForm), getUser()));
		add(form);
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}
}
