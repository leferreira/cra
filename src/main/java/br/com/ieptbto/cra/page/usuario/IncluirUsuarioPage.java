package br.com.ieptbto.cra.page.usuario;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class IncluirUsuarioPage extends BasePage<Usuario> {

	/**
	 * 
	 **/
	private static final long serialVersionUID = 1L;

	private Usuario usuario;

	public IncluirUsuarioPage() {
		this.usuario = new Usuario();
		adicionarComponentes();
	}

	public IncluirUsuarioPage(Usuario usuario) {
		this.usuario = usuario;
		this.usuario.setSenhaAtual(usuario.getSenha());

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioUsuario();

	}

	private void formularioUsuario() {
		UsuarioForm form = new UsuarioForm("formUsuario", getModel());
		form.add(new UsuarioInputPanel("usuarioInputPanel", getModel(), getUser()));
		add(form);
	}

	@Override
	protected IModel<Usuario> getModel() {
		return new CompoundPropertyModel<Usuario>(usuario);
	}
}
