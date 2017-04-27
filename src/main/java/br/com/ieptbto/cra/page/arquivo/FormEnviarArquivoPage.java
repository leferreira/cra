package br.com.ieptbto.cra.page.arquivo;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.page.base.BaseForm;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;

/**
 * 
 * @author Lefer
 *
 */
public class FormEnviarArquivoPage extends BaseForm<Arquivo> {

	/****/
	private static final long serialVersionUID = 1L;

	public FormEnviarArquivoPage(String id, IModel<Arquivo> model, FileUploadField fileUpload) {
		super(id, model);
	}
}
