package br.com.ieptbto.cra.page.titulo;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.page.base.BasePage;

public class HistoricoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;
	private TituloRemessa titulo;

	public HistoricoPage(TituloRemessa titulo){
		this.titulo=titulo;
		add(new HistoricoPanel("tituloHistorio", getModel(), titulo));
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}

}
