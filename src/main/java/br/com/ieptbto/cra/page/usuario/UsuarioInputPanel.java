package br.com.ieptbto.cra.page.usuario;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.util.EmailValidator;

@SuppressWarnings("serial")
public class UsuarioInputPanel extends Panel {

	private Usuario usuario;

	public UsuarioInputPanel(String id, IModel<Usuario> model, Usuario usuario) {
		super(id, model);
		this.usuario = usuario;
		adicionarCampos();
	}
	
	public UsuarioInputPanel(String id){
		super(id);
		adicionarCampos();
	}

	private void adicionarCampos() {
		add(campoNome());
		add(campoLogin());
		add(campoSenha());
		add(campoEmail());
		//add(campoConfirmarSenha());
	}

	private TextField<String> campoNome() {
		TextField<String> textField = new TextField<String>("nome");
		textField.setLabel(new Model<String>("Nome"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	private TextField<String> campoLogin() {
		TextField<String> textField = new TextField<String>("login");
		textField.setLabel(new Model<String>("Login"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}
	

	private TextField<String> campoSenha() {
		PasswordTextField senha = new PasswordTextField("senha");
		senha.setLabel(new Model<String>("Senha"));
		senha.setOutputMarkupId(true);
		senha.setRequired(true);
		return senha;
	}

	/*private TextField<String> campoConfirmarSenha(){
		PasswordTextField confirmarSenha = new PasswordTextField("confirmarSenha");
		confirmarSenha.setLabel(new Model<String>("Confimar Senha"));
		//confirmarSenha.setOutputMarkupId(true);
		confirmarSenha.setRequired(true);
		return confirmarSenha;
	}*/
	
	private TextField<String> campoEmail() {
		TextField<String> textField = new TextField<String>("email");
		textField.setLabel(new Model<String>("E-mail"));
		textField.add(new EmailValidator());
		return textField;
	}
}