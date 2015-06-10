package br.com.ieptbto.cra.page.batimento;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
public class ConfirmacaoLabel extends BasePage<Confirmacao> {

	/***/
	private static final long serialVersionUID = 1L;
	private Confirmacao confirmacao;
	
	public ConfirmacaoLabel() {
		info("Os arquivos de confirmação foram gerados com sucesso!");
	}
	
	@Override
	protected IModel<Confirmacao> getModel() {
		return new CompoundPropertyModel<Confirmacao>(confirmacao);
	}

}
