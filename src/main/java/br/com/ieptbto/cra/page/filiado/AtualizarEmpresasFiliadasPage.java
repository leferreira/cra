package br.com.ieptbto.cra.page.filiado;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class AtualizarEmpresasFiliadasPage extends BasePage<Filiado> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	FiliadoMediator filiadoMediator;
	private Filiado filiado;
	private FileUploadField fileUploadField;
	private DropDownChoice<Instituicao> dropdownConvenio;

	public AtualizarEmpresasFiliadasPage() {
		this.filiado = new Filiado();
		adicionarComponentes();
	}
	
	public AtualizarEmpresasFiliadasPage(String message) {
		this.filiado = new Filiado();
		success(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formularioArquivoConvenio());
	}

	private Form<Filiado> formularioArquivoConvenio() {
		Form<Filiado> form = new Form<Filiado>("form", getModel()) {

			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				FileUpload uploadedFile = fileUploadField.getFileUpload();
				Instituicao convenio = dropdownConvenio.getModelObject();

				try {
					filiadoMediator.processarArquivoAtualizacoesEmpresas(convenio, uploadedFile);
					success("Arquivo de atualizações da empresas filiadas dos convênios foi importado com sucesso na CRA!");
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível importar o arquivo atualizações. Favor entrar em contato com a CRA...");
				}
			}
		};
		form.setMaxSize(Bytes.megabytes(100));
		form.add(comboConvenio());
		form.add(campoArquivo());
		form.add(botaoEnviar());
		return form;
	}

	private DropDownChoice<Instituicao> comboConvenio() {
		dropdownConvenio = 	new DropDownChoice<Instituicao>("convenio", new Model<Instituicao>(),
				instituicaoMediator.getConvenios(),	new ChoiceRenderer<Instituicao>("nomeFantasia"));
		dropdownConvenio.setLabel(new Model<String>("Convênio"));
		dropdownConvenio.setRequired(true);
		return dropdownConvenio;
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
	protected IModel<Filiado> getModel() {
		return new CompoundPropertyModel<Filiado>(filiado);
	}
}
