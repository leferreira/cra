package br.com.ieptbto.cra.page.convenio.layout;

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
import org.apache.wicket.util.lang.Bytes;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 */

@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class EnviarArquivoEmpresaPage extends BasePage<Arquivo> {

	private Arquivo arquivo;
	private FileUploadField fileUploadField;
	private FormArquivoEmpresa form;

	public EnviarArquivoEmpresaPage() {
		this.arquivo = new Arquivo();
		addComponentes();
	}

	private void addComponentes() {

		form = new FormArquivoEmpresa("form", getModel(), getUser()) {
			@Override
			protected void onSubmit() {
				setFile(fileUploadField);
				if (getFile().getFileUpload() == null) {
					error("Pelo menos um arquivo deve ser informado.");
				}

				try {
					ArquivoFiliadoMediator arquivoRetorno = arquivoFiliadoMediator.salvarArquivo(getFile(), getUsuario());

					for (Exception exception : arquivoRetorno.getErros()) {
						warn(exception.getMessage());
					}
					arquivoRetorno.getErros().clear();

					if (arquivoRetorno.getArquivo() != null) {
						info("O arquivo " + fileUploadField.getInputName() + " com " + arquivoRetorno.getArquivo().getRemessas().size()
		                        + " Remessa(s), foi salvo com sucesso.");
					}

				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception ex) {
					error("Não foi possível procesar o arquivo enviado. Por favor entre em contato com a CRA.");
					logger.error(ex.getMessage());
				}
			}
		};
		form.setMultiPart(true);
		form.setMaxSize(Bytes.megabytes(10));
		form.add(botaoEnviar());
		form.add(campoArquivo());

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
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

}
