package br.com.ieptbto.cra.page.instrumentoProtesto;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoDeParaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class ImportarArquivoDeParaPage extends BasePage<AgenciaCAF> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ImportarArquivoDeParaPage.class);

	@SpringBean
	ArquivoDeParaMediator arquivoDeParaMediator;

	private AgenciaCAF arquivoCAF;
	private FileUploadField fileUploadField;

	public ImportarArquivoDeParaPage() {
		this.arquivoCAF = new AgenciaCAF();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioEnvioDePara();
	}

	private void formularioEnvioDePara() {
		Form<AgenciaCAF> form = new Form<AgenciaCAF>("form", getModel()) {

			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				final FileUpload uploadedFile = fileUploadField.getFileUpload();

				try {
					arquivoDeParaMediator.processarArquivo(uploadedFile);
					info("Arquivo " + uploadedFile.getClientFileName() + " foi importado com sucesso na CRA !");
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível importar o arquivo De/Para ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.setMaxSize(Bytes.megabytes(10));

		form.add(campoArquivo());
		form.add(botaoEnviar());
		add(form);
	}

	private FileUploadField campoArquivo() {
		fileUploadField = new FileUploadField("file", new ListModel<FileUpload>());
		fileUploadField.setRequired(true);
		fileUploadField.setLabel(new Model<String>("Anexo de Arquivo"));
		return fileUploadField;
	}

	private AjaxButton botaoEnviar() {
		return new AjaxButton("enviarArquivo") {

			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}

			@Override
			protected void finalize() throws Throwable {
				super.finalize();
			}

		};
	}

	@Override
	protected IModel<AgenciaCAF> getModel() {
		return new CompoundPropertyModel<AgenciaCAF>(arquivoCAF);
	}

}
