package br.com.ieptbto.cra.page.usuario;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
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

@SuppressWarnings({ "serial", "unused" })
public class UsuarioInputPanel extends Panel {

	private Usuario usuario;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private GrupoUsuarioMediator grupoUsuarioMediator;
	private DropDownChoice<Instituicao> comboInstituicao;
	private DropDownChoice<GrupoUsuario> comboGrupoUsuario;
	private RadioChoice<String> radioStatus;
	private Component button;

	public UsuarioInputPanel(String id, IModel<Usuario> model, Usuario usuario) {
		super(id, model);
		this.usuario = usuario;
		adicionarCampos();
	}

	public UsuarioInputPanel(String id) {
		super(id);
		adicionarCampos();
	}

	private void adicionarCampos() {
		add(campoNome());
		add(campoLogin());
		add(campoSenha());
		add(campoEmail());
		add(campoConfirmarSenha());
		add(botaoSalvar());
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
		senha.setOutputMarkupId(true);
		senha.setRequired(true);
		return senha;
	}

	private TextField<String> campoConfirmarSenha() {
		PasswordTextField confirmarSenha = new PasswordTextField("confirmarSenha");
		confirmarSenha.setLabel(new Model<String>("Confirmar Senha"));
		confirmarSenha.setOutputMarkupId(true);
		confirmarSenha.setRequired(true);
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
		return textField;
	}

	private Component campoStatus() {
		List<String> status = Arrays.asList(new String[] { "Ativo", "Não Ativo" });
		return new RadioChoice<String>("situacao", status);
	}

	private Component comboInstituicao() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		comboInstituicao = new DropDownChoice<Instituicao>("instituicao",instituicaoMediator.buscarCartoriosInstituicoes(), renderer);
		comboInstituicao.setLabel(new Model<String>("Instituição"));
		comboInstituicao.setRequired(true);
		return comboInstituicao;
	}

	private Component comboGrupoDoUsuario() {
		IChoiceRenderer<GrupoUsuario> renderer = new ChoiceRenderer<GrupoUsuario>("grupo");
		comboGrupoUsuario = new DropDownChoice<GrupoUsuario>("grupoUsuario",grupoUsuarioMediator.listaDeGrupos(), renderer);
		comboGrupoUsuario.setLabel(new Model<String>("Grupo Usuário"));
		comboGrupoUsuario.setRequired(true);
		return comboGrupoUsuario;
	}

	private Component botaoSalvar() {
		button = new Button("botaoSalvar") {
		};
		return button;
	}
}