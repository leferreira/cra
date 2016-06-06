package br.com.ieptbto.cra.page.login;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.component.CustomFeedbackPanel;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.webpage.AbstractWebPage;

@SuppressWarnings("serial")
/**
 * 
 * @author Leandro
 *
 */
public class LoginPage extends AbstractWebPage<Usuario> {

	private Usuario usuario;
	private CustomFeedbackPanel feedBackPanel;

	public LoginPage() {
		inicializarObjetos();
		adicionarCampos();
	}

	private void inicializarObjetos() {
		usuario = new Usuario();
	}

	private void adicionarCampos() {
		getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().clear();
		feedBackPanel = new CustomFeedbackPanel(WID_FEEDBACK);
		feedBackPanel.setOutputMarkupId(true);

		LoginForm loginForm = new LoginForm("loginForm", getModel());
		loginForm.add(campoUsuario());
		loginForm.add(campoSenha());
		loginForm.add(new Button("botaoSalvar"));
		loginForm.add(feedBackPanel);
		add(loginForm);
	}

	private PasswordTextField campoSenha() {
		return new PasswordTextField("senha");
	}

	private TextField<String> campoUsuario() {
		return new RequiredTextField<String>("login");
	}

	protected IModel<Usuario> getModel() {
		return new CompoundPropertyModel<Usuario>(usuario);
	}
}
