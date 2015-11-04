package br.com.ieptbto.cra.page.login;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;

/**
 * 
 * @author Leandro
 * 
 */
@SuppressWarnings({ "serial" })
public class TrocaSenhaPanel extends Panel {

	@SpringBean
	private UsuarioMediator usuarioMediator;
	private PasswordTextField senha;
	private PasswordTextField novaSenha;
	private PasswordTextField confirmaSenha;
	private Usuario usuario;

	public TrocaSenhaPanel(String id, Usuario usuario) {
		super(id);
		this.usuario = usuario;
		adicionarCampos();
	}

	private void adicionarCampos() {
		Form<?> trocaSenhaForm = new Form<>("form", getDefaultModel());
		trocaSenhaForm.add(campoSenhaAtual());
		trocaSenhaForm.add(campoNovaSenha());
		trocaSenhaForm.add(campoConfirmacaoSenha());
		trocaSenhaForm.add(addButaoSubmit());
		add(trocaSenhaForm);
	}

	private Button addButaoSubmit() {
		Button botao = new Button("botaoSalvar") {
			@Override
			public void onSubmit() {
				String senhaAtual = "";
				String senhaNova = "";
				String senhaConfirmacao = "";
				
				if (senha.getDefaultModel() != null)
					senhaAtual = senha.getModelObject();
				
				if (novaSenha.getDefaultModel() != null)
					senhaNova = novaSenha.getModelObject();
				
				if (confirmaSenha.getDefaultModel() != null)
					senhaConfirmacao = confirmaSenha.getModelObject();
					
				Usuario user = usuarioMediator.autenticar(usuario.getLogin(), senhaAtual);
				
				if (user != null) {
					user.setSenha(senhaNova);
					user.setConfirmarSenha(senhaConfirmacao);
				
					if (usuarioMediator.isSenhasIguais(user)) {
						usuarioMediator.trocarSenha(user);
						info("Sua senha foi alterada com sucesso !");
					} else 
						error("Não foi possível alterar a senha ! A nova senha não coincide com a confirmação |");
				} else 
					error("A senha informada está incorreta !");
			}
		};
		return botao;
	}

	private PasswordTextField campoConfirmacaoSenha() {
		confirmaSenha = new PasswordTextField("confirmaSenha", new Model<String>());
		confirmaSenha.setRequired(true);
		confirmaSenha.setLabel(new Model<String>("Confirmar Senha"));
		return confirmaSenha;
	}

	private PasswordTextField campoNovaSenha() {
		novaSenha = new PasswordTextField("novaSenha", new Model<String>());
		novaSenha.setRequired(true);
		novaSenha.setLabel(new Model<String>("Nova Senha"));
		return novaSenha;
	}

	private TextField<String> campoSenhaAtual() {
		senha = new PasswordTextField("senha", new Model<String>());
		senha.setRequired(true);
		senha.setLabel(new Model<String>("Senha Atual"));
		return senha;
	}
}
