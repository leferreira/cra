package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.page.base.BasePage;

public class HistoricoPage extends BasePage<Titulo> {

	/***/
	private static final long serialVersionUID = 1L;
	private Titulo titulo;

	public HistoricoPage(Titulo titulo){
		this.titulo=titulo;
		add(new HistoricoPanel("tituloHistorio", getModel(), titulo));
	}
	
	@Override
	protected IModel<Titulo> getModel() {
		return new CompoundPropertyModel<Titulo>(titulo);
	}

}
