package br.com.ieptbto.cra.page.cartorio;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN,
		CraRoles.SUPER, })
public class DetalharCartorioPage extends BasePage<Instituicao> {

	/***/
	private static final long serialVersionUID = 1L;
	private Form<Instituicao> form;
	private Instituicao cartorio;
	
	public DetalharCartorioPage(Instituicao c){
		super();
		this.cartorio = c;
		carregarComponentes();
		info("Os dados foram salvos com sucesso!");
	}
	
	public void carregarComponentes(){
		form = new Form<Instituicao>("form");
		form.add(new CartorioLabelPanel<Instituicao>("cartorioLabelPanel", getModel(), cartorio));
		adicionarBotoes();
		add(form);
	}
	
	private void adicionarBotoes() {
		form.add(obterBotaoNovo());
		form.add(obterBotaoLista());
	}
	
	private Button obterBotaoNovo() {
		return new Button("botaoNovo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new IncluirCartorioPage());
			}
		};
	}
	
	private Button obterBotaoLista() {
		return new Button("botaoLista") {
			/***/
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(new ListaCartorioPage());
			}
		};
	}

	
	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(cartorio);
	}
}
