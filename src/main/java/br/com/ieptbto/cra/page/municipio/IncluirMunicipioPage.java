package br.com.ieptbto.cra.page.municipio;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirMunicipioPage extends BasePage<Municipio> {

	private static final long serialVersionUID = 1L;

	private Municipio municipio;
	private MunicipioForm form;

	public IncluirMunicipioPage() {
		this.municipio = new Municipio();
		adicionarComponentes();
	}

	public IncluirMunicipioPage(Municipio municipio) {
		this.municipio = municipio;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		setFormulario();

	}

	private void setFormulario() {
		form = new MunicipioForm("form", getModel());
		form.add(new MunicipioInputPanel("municipioInputPanel", getModel()));
		add(form);
	}

	@Override
	protected IModel<Municipio> getModel() {
		return new CompoundPropertyModel<Municipio>(municipio);
	}

}
