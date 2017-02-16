package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.beans.TituloBean;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Aráujo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class BuscarTitulosPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	private TituloRemessa titulo;
	private Form<TituloBean> form;

	public BuscarTitulosPage() {
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
				TituloBean tituloFormBean = getModelObject();

				try {
					if (tituloFormBean.getDataInicio() != null) {
						if (tituloFormBean.getDataFim() != null) {
							LocalDate dataInicio = new LocalDate(tituloFormBean.getDataInicio());
							LocalDate dataFim = new LocalDate(tituloFormBean.getDataFim());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						} else
							throw new InfraException("As duas datas devem ser preenchidas.");
					}
					if (tituloFormBean.isTodosCamposEmBranco()) {
						throw new InfraException(
								"Por favor, preencha um ou mais campos para realizar a busca, ou informe o período com um Banco/Convênio ou Município!");
					}

					setResponsePage(new ListaTitulosPage(tituloFormBean));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca ! Favor entrar em contato com a CRA...");
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
