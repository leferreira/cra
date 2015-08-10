package br.com.ieptbto.cra.page.cra;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
public class BatimentoLabel extends BasePage<Batimento> {

	/***/
	private static final long serialVersionUID = 1L;
	private Batimento batimento;
	
	public BatimentoLabel() {
		info("Batimento realizado com sucesso!");
	}
	
	@Override
	protected IModel<Batimento> getModel() {
		// TODO Auto-generated method stub
		return new CompoundPropertyModel<Batimento>(batimento);
	}

}
