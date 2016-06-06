package br.com.ieptbto.cra.page.usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.GrupoUsuario;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.GrupoUsuarioMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.EmailValidator;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class PerfilUsuarioPage extends BasePage<Usuario> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PerfilUsuarioPage.class);

	@SpringBean
	GrupoUsuarioMediator grupoUsuarioMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	UsuarioMediator usuarioMediator;

	private Usuario usuario;
	private String senhaAtual;

	public PerfilUsuarioPage(Usuario usuario) {
		this.usuario = usuario;
		this.senhaAtual = usuario.getSenha();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		setFormulario();

	}

	private void setFormulario() {
		Form<Usuario> form = new Form<Usuario>("formUsuario", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				Usuario usuario = getModelObject();

				try {
					if (usuario.getSenha() != null) {
						usuario.setSenha(Usuario.cryptPass(usuario.getSenha()));
					} else {
						usuario.setSenha(getSenhaAtual());
					}
					usuarioMediator.alterar(usuario);
					info("Os dados do usuário foram salvos com sucesso!");

				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(campoNome());
		form.add(campoLogin());
		form.add(campoSenha());
		form.add(campoEmail());
		form.add(campoConfirmarSenha());
		form.add(campoContato());
		form.add(campoStatus());
		form.add(comboInstituicao());
		form.add(comboGrupoDoUsuario());
		form.add(new Button("botaoSalvar"));
		add(form);
	}

	private TextField<String> campoNome() {
		TextField<String> textField = new TextField<String>("nome");
		textField.setLabel(new Model<String>("Nome"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> campoLogin() {
		TextField<String> textField = new TextField<String>("login");
		textField.setLabel(new Model<String>("Login"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		textField.setEnabled(false);
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
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		DropDownChoice<Instituicao> comboInstituicao = new DropDownChoice<Instituicao>("instituicao", instituicaoMediator.listarTodas(), renderer);
		comboInstituicao.setLabel(new Model<String>("Instituição"));
		comboInstituicao.setRequired(true);
		comboInstituicao.setEnabled(false);
		return comboInstituicao;
	}

	private DropDownChoice<GrupoUsuario> comboGrupoDoUsuario() {
		List<GrupoUsuario> grupoUsuarioPermitido = new ArrayList<GrupoUsuario>();
		for (GrupoUsuario grupo : grupoUsuarioMediator.listaDeGrupos()) {
			if (grupo.getId() >= getUser().getGrupoUsuario().getId()) {
				grupoUsuarioPermitido.add(grupo);
			}
		}

		IChoiceRenderer<GrupoUsuario> renderer = new ChoiceRenderer<GrupoUsuario>("grupo");
		DropDownChoice<GrupoUsuario> comboGrupoUsuario = new DropDownChoice<GrupoUsuario>("grupoUsuario", grupoUsuarioPermitido, renderer);
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

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	@Override
	protected IModel<Usuario> getModel() {
		return new CompoundPropertyModel<Usuario>(usuario);
	}
}
