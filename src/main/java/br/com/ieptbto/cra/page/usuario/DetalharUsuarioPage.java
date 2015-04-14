package br.com.ieptbto.cra.page.usuario;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@SuppressWarnings("serial")

@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN,
		CraRoles.SUPER, })
public class DetalharUsuarioPage extends BasePage<Usuario> {

	private Form<Usuario> form;
	private Usuario usuario;
	
	public DetalharUsuarioPage(Usuario u){
		super();
		this.usuario = u;
		carregarComponentes();
		info("Os dados foram salvos com sucesso!");
	}
	
	public void carregarComponentes(){
		form = new Form<Usuario>("form");
		adicionarPainelUsuario();
		adicionarBotoes();
		add(form);
	}
	
	private void adicionarBotoes() {
		form.add(obterBotaoNovoUsuario());
		form.add(obterBotaoVoltar());
	}
	
	private Button obterBotaoNovoUsuario() {
		return new Button("botaoNovoUsuario") {
			@Override
			public void onSubmit() {
				setResponsePage(new IncluirUsuarioPage());
			}
		};
	}
	
	private Button obterBotaoVoltar() {
		return new Button("botaoVoltarListaUsuario") {
			@Override
			public void onSubmit() {
				setResponsePage(new ListaUsuarioPage());
			}
		};
	}

	public void adicionarPainelUsuario(){
		form.add(new UsuarioLabelPanel<Usuario>("usuarioLabelPanel", getModel(), usuario));
	}
	
	@Override
	protected IModel<Usuario> getModel() {
		return new CompoundPropertyModel<Usuario>(usuario);
	}

}
