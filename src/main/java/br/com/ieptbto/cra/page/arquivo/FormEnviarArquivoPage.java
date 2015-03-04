package br.com.ieptbto.cra.page.arquivo;

import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.page.base.BaseForm;

/**
 * 
 * @author Lefer
 *
 */
public class FormEnviarArquivoPage extends BaseForm<Arquivo> {

	/****/
	private static final long serialVersionUID = 1L;

	public FormEnviarArquivoPage(String id, IModel<Arquivo> model) {
		super(id, model);
	}

}
