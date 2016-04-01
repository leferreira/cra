package br.com.ieptbto.cra.page.administracao;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Aráujo
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class SobreCraPage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/***/
	private static final long serialVersionUID = 1L;

	public SobreCraPage() {
		carregarInformacoesVersao();
	}

	private void carregarInformacoesVersao() {
		add(new Label("versao", ""));
		add(new Label("dataVersao", "31/03/2016"));
	}

	@Override
	protected IModel<T> getModel() {
		// TODO Auto-generated method stub
		return null;
	}
}
