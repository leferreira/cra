package br.com.ieptbto.cra.page.arquivo;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;

/**
 * 
 * @author Lefer
 *
 */
@AuthorizeInstantiation(value = "USER")
public class EnviarArquivoPage extends BasePage<Arquivo> {

	/****/
	private static final long serialVersionUID = 852632145;

	@SpringBean
	private ArquivoMediator arquivoMediator;
	private static final Logger logger = Logger.getLogger(ProcessadorArquivo.class);

	private Arquivo arquivo;
	private Form<Arquivo> form;
	private FileUploadField fileUploadField;

	public EnviarArquivoPage() {
		arquivo = new Arquivo();
		arquivo.setInstituicaoEnvio(getUser().getInstituicao());
		arquivo.setUsuarioEnvio(getUser());
		arquivo.setDataEnvio(new Date());

		form = new Form<Arquivo>("form", getModel()) {
			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				final FileUpload uploadedFile = fileUploadField.getFileUpload();
				arquivo.setNomeArquivo(uploadedFile.getClientFileName());

				try {
					arquivoMediator.salvar(arquivo, uploadedFile);
					info("Arquivo enviado com sucesso.");

				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}

			}
		};
		form.setMultiPart(true);
		form.setMaxSize(Bytes.megabytes(10));

		form.add(campoArquivo());
		form.add(botaoEnviar());
		add(form);
	}

	private FileUploadField campoArquivo() {
		fileUploadField = new FileUploadField("file", new ListModel<FileUpload>());
		fileUploadField.setRequired(true);
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
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

}
