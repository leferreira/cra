package br.com.ieptbto.cra.page.batimento;

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

import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.enumeration.LayoutArquivo;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DepositoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ImportarExtratoPage extends BasePage<Batimento> {

	@SpringBean
	private DepositoMediator depositoMediator;
	
	private static final long serialVersionUID = 1L;
	private Batimento batimento;
	private FileUploadField fileUploadField;

	public ImportarExtratoPage() {
		this.batimento = new Batimento();
		adicionarComponentes();
	}

	public ImportarExtratoPage(String message) {
		this.batimento = new Batimento();
		success(message);
		adicionarComponentes();
	}
	
	@Override
	protected void adicionarComponentes() {
		add(formularioImportarExtrato());
	}

	private Form<Batimento> formularioImportarExtrato() {
		Form<Batimento> form = new Form<Batimento>("form", getModel()) {

			/***/
			private static final long serialVersionUID = -1L;

			@Override
			protected void onSubmit() {
				final FileUpload uploadedFile = fileUploadField.getFileUpload();

				try {
					if (!uploadedFile.getClientFileName().toLowerCase().endsWith(LayoutArquivo.CSV.getExtensao())) {
						throw new InfraException("Extensão do arquivo não permitida! Verifique se o arquivo tem extensão \".CSV\"...");
					}
					DepositoMediator depositosExtrato = depositoMediator.processarDepositosExtrato(getUser(), uploadedFile);
					if (!depositosExtrato.getDepositosConflitados().isEmpty() && !depositosExtrato.getRetornosConflitados().isEmpty()) {
						setResponsePage(new ConflitoDepositosArquivoRetornoPage(depositosExtrato.getDepositosConflitados(), depositosExtrato.getRetornosConflitados()));
					} else {
						success("O arquivo de extrato processado foi importado com sucesso!");
					}
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					getFeedbackPanel().error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo de extrato ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.setMultiPart(true);
		form.setMaxSize(Bytes.megabytes(10));
		form.add(botaoEnviar());
		form.add(campoArquivo());
		return form;
	}

	private FileUploadField campoArquivo() {
		fileUploadField = new FileUploadField("file", new ListModel<FileUpload>());
		fileUploadField.setRequired(true);
		fileUploadField.setLabel(new Model<String>("Anexo de Extrato"));
		return fileUploadField;
	}

	private AjaxButton botaoEnviar() {
		return new AjaxButton("enviarExtrato") {
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
	protected IModel<Batimento> getModel() {
		return new CompoundPropertyModel<>(batimento);
	}
}
