package br.com.ieptbto.cra.page.base;

import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.AbstractEntidade;

public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HomePage() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}

}