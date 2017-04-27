package br.com.ieptbto.cra.page.instrumentoProtesto;

import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HistoricoInstrumentoProtestoPage extends BasePage<InstrumentoProtesto> {

	/***/
	private static final long serialVersionUID = 1L;

	private InstrumentoProtesto instrumentoProtesto;

	public HistoricoInstrumentoProtestoPage(InstrumentoProtesto instrumentoProtesto) {
		this.instrumentoProtesto = instrumentoProtesto;
	}

	@Override
	protected void adicionarComponentes() {
		// TODO Auto-generated method stub

	}

	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return new CompoundPropertyModel<InstrumentoProtesto>(instrumentoProtesto);
	}
}