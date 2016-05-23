package br.com.ieptbto.cra.page.cartorio;

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
public class IncluirCartorioPage extends BasePage<Instituicao> {

	/***/
	private static final long serialVersionUID = 1L;

	private Instituicao cartorio;

	public IncluirCartorioPage() {
		this.cartorio = new Instituicao();
		adicionarComponentes();
	}

	public IncluirCartorioPage(Instituicao cartorio) {
		this.cartorio = cartorio;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formCartorio();
	}

	private void formCartorio() {
		CartorioForm form = new CartorioForm("form", getModel());
		form.add(new CartorioInputPanel("cartorioInputPanel", getModel()));
		add(form);
	}

	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(cartorio);
	}
}
