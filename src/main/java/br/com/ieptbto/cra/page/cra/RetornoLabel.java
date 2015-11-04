package br.com.ieptbto.cra.page.cra;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
public class RetornoLabel extends BasePage<Retorno> {

	/***/
	private static final long serialVersionUID = 1L;
	private Retorno retorno;
	
	public RetornoLabel() {
		info("Os arquivos de retorno foram gerados com sucesso!");
	}
	
	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}

}
