package br.com.ieptbto.cra.page.instituicao;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirInstituicaoPage extends BasePage<Instituicao> {

	/***/
	private static final long serialVersionUID = 1L;
	private Instituicao instituicao;
	
	private InstituicaoForm form;

	public IncluirInstituicaoPage() {
		instituicao = new Instituicao();
		setForm();
	}

	public IncluirInstituicaoPage(Instituicao instituicao) {
		this.instituicao = instituicao;
		setForm();
	}

	public void setForm() {
		form = new InstituicaoForm("form", getModel());
		form.add(new InstituicaoInputPanel("instituicaoInputPanel", getModel()));
		add(form);
	}

	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(instituicao);
	}
}
