package br.com.ieptbto.cra.page.usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.GrupoUsuario;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.GrupoUsuarioMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.EmailValidator;

public class UsuarioInputPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	GrupoUsuarioMediator grupoUsuarioMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private Usuario usuario;

	public UsuarioInputPanel(String id, IModel<?> model, Usuario user) {
		super(id, model);
		this.usuario = user;

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(campoNome());
		add(campoLogin());
		add(campoSenha());
		add(campoEmail());
		add(campoConfirmarSenha());
		add(campoContato());
		add(campoStatus());
		add(comboInstituicao());
		add(comboGrupoDoUsuario());
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
		senha.setRequired(verificarExistencia());
		return senha;
	}

	private TextField<String> campoConfirmarSenha() {
		PasswordTextField confirmarSenha = new PasswordTextField("confirmarSenha");
		confirmarSenha.setLabel(new Model<String>("Confirmar Senha"));
		confirmarSenha.setRequired(false);
		return confirmarSenha;
	}

	private TextField<String> campoEmail() {
		TextField<String> textField = new TextField<String>("email");
		textField.setLabel(new Model<String>("E-mail"));
		textField.add(new EmailValidator());
		return textField;
	}

	private TextField<String> campoContato() {
		TextField<String> textField = new TextField<String>("contato");
		textField.setLabel(new Model<String>("Contato"));
		textField.setOutputMarkupId(true);
		textField.setMarkupId("telefone");
		return textField;
	}

	private Component campoStatus() {
		List<String> status = Arrays.asList(new String[] { "Ativo", "Não Ativo" });
		return new RadioChoice<String>("situacao", status);
	}

	private DropDownChoice<Instituicao> comboInstituicao() {
		DropDownChoice<Instituicao> comboInstituicao = new DropDownChoice<Instituicao>("instituicao", instituicaoMediator.listarTodas(),
				new ChoiceRenderer<Instituicao>("nomeFantasia"));
		comboInstituicao.setLabel(new Model<String>("Instituição"));
		comboInstituicao.setRequired(true);
		return comboInstituicao;
	}

	private DropDownChoice<GrupoUsuario> comboGrupoDoUsuario() {
		List<GrupoUsuario> grupoUsuarioPermitido = new ArrayList<GrupoUsuario>();
		for (GrupoUsuario grupo : grupoUsuarioMediator.listaDeGrupos()) {
			if (grupo.getId() >= usuario.getGrupoUsuario().getId()) {
				grupoUsuarioPermitido.add(grupo);
			}
		}

		DropDownChoice<GrupoUsuario> comboGrupoUsuario =
				new DropDownChoice<GrupoUsuario>("grupoUsuario", grupoUsuarioPermitido, new ChoiceRenderer<GrupoUsuario>("grupo"));
		comboGrupoUsuario.setLabel(new Model<String>("Grupo Usuário"));
		comboGrupoUsuario.setRequired(true);
		return comboGrupoUsuario;
	}

	private boolean verificarExistencia() {
		if (usuario.getId() == 0) {
			return true;
		}
		return false;
	}
}
