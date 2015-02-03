package br.com.ieptbto.cra.page.login;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
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
	private FeedbackPanel feedBackPanel;
	private String senha;
	private String novaSenha;
	private String confirmaSenha;
	private Usuario usuario;

	public TrocaSenhaPanel(String id, Usuario usuario) {
		super(id);
		this.usuario = usuario;
		adicionarCampos();
	}

	private void adicionarCampos() {
		feedBackPanel = new FeedbackPanel("mensagemRetorno");
		feedBackPanel.setOutputMarkupId(true);

		Form<?> trocaSenhaForm = new Form<>("form", getDefaultModel());
		trocaSenhaForm.add(campoSenhaAtual());
		trocaSenhaForm.add(campoNovaSenha());
		trocaSenhaForm.add(campoConfirmacaoSenha());
		trocaSenhaForm.add(addButaoSubmit());
		trocaSenhaForm.add(feedBackPanel);
		add(trocaSenhaForm);
	}

	private Button addButaoSubmit() {
		Button botao = new Button("botaoSalvar") {
			@Override
			public void onSubmit() {
				if (usuarioMediator.trocarSenha(senha, novaSenha, confirmaSenha, usuario)) {
					info("Senha Alterada com Sucesso");
				} else {
					error("Não foi possível Alterar a sunha Senha");
				}
			}
		};
		return botao;
	}

	private PasswordTextField campoConfirmacaoSenha() {
		return new PasswordTextField("confirmaSenha");
	}

	private PasswordTextField campoNovaSenha() {
		return new PasswordTextField("novaSenha");
	}

	private TextField<String> campoSenhaAtual() {
		return new PasswordTextField("senha");
	}

	public String getSenha() {
		return senha;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}

}
