package br.com.ieptbto.cra.page.batimento;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@AuthorizeInstantiation(value = { CraRoles.USER })
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, })
public class BatimentoPage extends BasePage<Retorno> {

	/***/
	private static final long serialVersionUID = 1L;
	
	private Retorno retorno;
	
	
	public BatimentoPage() {
		super();
	}

	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}

	
}
