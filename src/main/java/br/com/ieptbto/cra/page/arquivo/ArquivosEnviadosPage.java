package br.com.ieptbto.cra.page.arquivo;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class ArquivosEnviadosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	private Arquivo arquivo;
	private Form<Arquivo> form;
	private Instituicao instituicao;

	public ArquivosEnviadosPage() {
		this.arquivo = new Arquivo();
		this.instituicao = getUser().getInstituicao();
		
		form = new Form<Arquivo>("form", getModel());
		if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("CRA")){
			form.add(new ArquivosCraPanel("enviadosPanel", getModel(), getInstituicao()));
		} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals("Cartório")){
			form.add(new ArquivosCartorioPanel("enviadosPanel", getModel(), getInstituicao()));
		} else {
			form.add(new ArquivosInstituicaoPanel("enviadosPanel", getModel(), getInstituicao()));
		}
		add(form);
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}
	
	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}