package br.com.ieptbto.cra.page.tipoInstituicao;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirTipoInstituicaoPage extends BasePage<TipoInstituicao> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;

	private TipoInstituicao tipoInstituicao;

	public IncluirTipoInstituicaoPage() {
		this.tipoInstituicao = new TipoInstituicao();
		adicionarComponentes();
	}

	public IncluirTipoInstituicaoPage(TipoInstituicao tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		setForm();
	}

	private void setForm() {
		Form<TipoInstituicao> form = new Form<TipoInstituicao>("form", getModel()) {
			/** */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					TipoInstituicao tipoInstituicao = getModelObject();
					tipoInstituicaoMediator.alterarPermissoesTipoInstituicao(tipoInstituicao);
					setResponsePage(new ListaTipoInstituicaoPage("O tipo instituição foi atualizado com sucesso!"));
				} catch (Exception ex) {
					logger.info(ex.getMessage(), ex);
					error("Não foi possível alterar o tipo instituição. Favor entrar em contato com a CRA!");
				}
			}
		};
		form.add(campoTipoInstituicao());
		add(form);
	}

	private TextField<String> campoTipoInstituicao() {
		TextField<String> nomeTipo = new TextField<String>("tipoInstituicao");
		nomeTipo.setEnabled(false);
		return nomeTipo;
	}

	@Override
	protected IModel<TipoInstituicao> getModel() {
		return new CompoundPropertyModel<TipoInstituicao>(tipoInstituicao);
	}
}