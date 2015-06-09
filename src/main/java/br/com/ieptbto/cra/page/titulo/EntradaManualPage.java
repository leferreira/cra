package br.com.ieptbto.cra.page.titulo;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class EntradaManualPage extends BasePage<TituloRemessa>{

	/***/
	private static final long serialVersionUID = 1L;
	
	private Form<?> form;
	private List<TituloRemessa> titulosEntradaManual = new ArrayList<TituloRemessa>();
	private TituloRemessa titulo; 
	
	public EntradaManualPage() {
		this.titulo = new TituloRemessa();
	
		form = new Form<TituloRemessa>("form", getModel()){
			
			/***/
			private static final long serialVersionUID = 1L;
			@Override
            protected void onSubmit(){
				
			}
		};
		add(form);
		carregarEntradaManual();
	}
	
	private void carregarEntradaManual(){

		form.add(new Button("adicionarTitulo"){
			/***/
			private static final long serialVersionUID = 7363757971646944546L;
			@SuppressWarnings("unused")
			public void onClick() {
				
				titulosEntradaManual.add(titulo);
            }
		});
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}

}
