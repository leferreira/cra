package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.util.Arrays;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;
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

	private static final long serialVersionUID = 1L;

	@SpringBean
	ArquivoDeParaMediator arquivoDeParaMediator;

	private FileUploadField fileUploadField;
	private CheckBox checkboxLimparBase;
	private DropDownChoice<PadraoArquivoDePara> dropDownDePara;

	public ImportarArquivoDeParaPage() {
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formularioEnvioDePara());
	}

	private Form<AgenciaCAF> formularioEnvioDePara() {
		Form<AgenciaCAF> form = new Form<AgenciaCAF>("form", getModel()) {

			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				final FileUpload uploadedFile = fileUploadField.getFileUpload();
				final PadraoArquivoDePara padraoArquivo = dropDownDePara.getModelObject();
				final Boolean limparBase = checkboxLimparBase.getModelObject();

				try {
					arquivoDeParaMediator.processarArquivo(uploadedFile, padraoArquivo, limparBase);
					success("O arquivo " + padraoArquivo.getModelo() + " foi importado com sucesso na CRA!");
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível importar o arquivo! Favor entrar em contato com a CRA...");
				}
			}
		};
		form.setMaxSize(Bytes.megabytes(100));
		form.add(dropDownTipoArquivoEnviado());
		form.add(checkboxLimparBase());
		form.add(campoArquivo());
		form.add(botaoEnviar());
		return form;
	}

	private DropDownChoice<PadraoArquivoDePara> dropDownTipoArquivoEnviado() {
		dropDownDePara = new DropDownChoice<PadraoArquivoDePara>("tipoDePara", new Model<PadraoArquivoDePara>(), Arrays.asList(PadraoArquivoDePara.values()),
				new ChoiceRenderer<PadraoArquivoDePara>("modelo"));
		dropDownDePara.setRequired(true);
		dropDownDePara.setLabel(new Model<String>("Tipo Arquivo De/Para"));
		return dropDownDePara;
	}

	private CheckBox checkboxLimparBase() {
		checkboxLimparBase = new CheckBox("limparBase", new Model<Boolean>());
		checkboxLimparBase.setLabel(new Model<String>("Limpar a Base"));
		return checkboxLimparBase;
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
		return null;
	}
}
