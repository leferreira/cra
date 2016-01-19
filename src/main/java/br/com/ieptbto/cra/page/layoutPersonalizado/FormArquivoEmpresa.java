package br.com.ieptbto.cra.page.layoutPersonalizado;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.ArquivoFiliadoMediator;
import br.com.ieptbto.cra.mediator.ArquivoMediator;

/**
 * 
 * @author Lefer
 *
 */
public class FormArquivoEmpresa extends Form<Arquivo> {

	/** * */
	private static final long serialVersionUID = 1L;

	@SpringBean
	ArquivoMediator arquivoMediator;
	@SpringBean
	ArquivoFiliadoMediator arquivoFiliadoMediator;

	private Usuario usuario;
	private FileUploadField file;

	public FormArquivoEmpresa(String id, IModel<Arquivo> model, Usuario usuario) {
		super(id, model);
		this.usuario = usuario;
	}

	public FileUploadField getFile() {
		return file;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setFile(FileUploadField file) {
		this.file = file;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
