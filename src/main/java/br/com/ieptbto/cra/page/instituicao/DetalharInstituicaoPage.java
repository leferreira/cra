package br.com.ieptbto.cra.page.instituicao;

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
public class DetalharInstituicaoPage extends BasePage<Instituicao> {

	/***/
	private static final long serialVersionUID = 1L;
	private Form<Instituicao> form;
	private Instituicao instituicao;
	
	public DetalharInstituicaoPage(Instituicao i){
		super();
		this.instituicao = i;
		carregarComponentes();
		info("Os dados foram salvos com sucesso!");
	}
	
	public void carregarComponentes(){
		form = new Form<Instituicao>("form");
		form.add(new InstituicaoLabelPanel<Instituicao>("instituicaoLabelPanel", getModel(), instituicao));
		form.add(obterBotaoNovo());
		add(form);
	}
	
	private Button obterBotaoNovo() {
		return new Button("botaoNovo") {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new IncluirInstituicaoPage());
			}
		};
	}
	
	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(instituicao);
	}
}
