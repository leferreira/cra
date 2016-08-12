package br.com.ieptbto.cra.page.cnp;

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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.RegistroCnp;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class Importar5AnosCartorioPage extends BasePage<RegistroCnp> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	CentralNacionalProtestoMediator centralNacionalProtestoMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private FileUploadField fileUploadField;
	private DropDownChoice<Instituicao> dropDownCartorio;

	public Importar5AnosCartorioPage() {
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		Form<Void> form = new Form<Void>("form") {

			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				try {
					final FileUpload uploadedFile = fileUploadField.getFileUpload();
					final Instituicao instituicao = dropDownCartorio.getModelObject();
					centralNacionalProtestoMediator.importarBase5anosCSV(instituicao, uploadedFile);
					success("O arquivo Central Nacional de Protesto ,do cartório de " + instituicao.getNomeFantasia()
							+ ", foi importado com sucesso!");

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.setMultiPart(true);
		form.setMaxSize(Bytes.megabytes(20));

		form.add(campoArquivo());
		form.add(dropDownCartorio());
		form.add(botaoEnviar());
		add(form);
	}

	private FileUploadField campoArquivo() {
		fileUploadField = new FileUploadField("file", new ListModel<FileUpload>());
		fileUploadField.setRequired(true);
		fileUploadField.setLabel(new Model<String>("Anexo de Arquivo"));
		return fileUploadField;
	}

	private DropDownChoice<Instituicao> dropDownCartorio() {
		dropDownCartorio = new DropDownChoice<Instituicao>("cartorio", new Model<Instituicao>(), instituicaoMediator.getCartorios(),
				new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio"));
		dropDownCartorio.setOutputMarkupId(true);
		return dropDownCartorio;
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
	protected IModel<RegistroCnp> getModel() {
		return null;
	}
}
