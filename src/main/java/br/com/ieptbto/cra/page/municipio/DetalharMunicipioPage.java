package br.com.ieptbto.cra.page.municipio;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, })
public class DetalharMunicipioPage extends BasePage<Municipio> {

	/***/
	private static final long serialVersionUID = 1L;
	
	private Form<Municipio> form;
	private Municipio municipio;
	
	public DetalharMunicipioPage(Municipio m){
		super();
		this.municipio = m;
		info("Os dados foram salvos com sucesso!");
		form = new Form<Municipio>("form");
		form.add(new MunicipioLabelPanel("municipioLabelPanel", getModel(), municipio));
		form.add(obterBotaoNovo());
		add(form);
	}
	
	@SuppressWarnings("serial")
	private Button obterBotaoNovo() {
		return new Button("botaoNovo") {
			@Override
			public void onSubmit() {
				setResponsePage(new IncluirMunicipioPage());
			}
		};
	}
	
	@Override
	protected IModel<Municipio> getModel() {
		return new CompoundPropertyModel<Municipio>(municipio);
	}

}
