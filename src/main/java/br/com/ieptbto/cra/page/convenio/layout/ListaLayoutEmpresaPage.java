package br.com.ieptbto.cra.page.convenio.layout;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 */

@SuppressWarnings({ "serial" })
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaLayoutEmpresaPage extends BasePage<LayoutFiliado> {

	private IModel<LayoutFiliado> layoutEmpresa;

	public ListaLayoutEmpresaPage() {
		carregarDados();
	}

	private void carregarDados() {
		// TODO Auto-generated method stub

	}

	@Override
	protected IModel<LayoutFiliado> getModel() {
		return new CompoundPropertyModel<LayoutFiliado>(layoutEmpresa);
	}

}
