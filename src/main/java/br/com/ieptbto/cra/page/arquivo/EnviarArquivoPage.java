package br.com.ieptbto.cra.page.arquivo;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * 
 * @author Lefer
 *
 */
public class EnviarArquivoPage extends BasePage<Arquivo> {

	/****/
	private static final long serialVersionUID = 852632145;
	private Arquivo arquivo;
	private FormEnviarArquivoPage form;

	public EnviarArquivoPage() {
		form = new FormEnviarArquivoPage("form", getModel());
		add(form);
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

}
