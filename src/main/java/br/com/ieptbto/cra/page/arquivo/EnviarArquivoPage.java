package br.com.ieptbto.cra.page.arquivo;

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
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.DesistenciaException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.cra.RelatorioRetornoPage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class EnviarArquivoPage extends BasePage<Arquivo> {

    /****/
    private static final long serialVersionUID = 852632145;

    @SpringBean
    private ArquivoMediator arquivoMediator;
    @SpringBean
    private InstituicaoMediator instituicaoMediator;
    private Usuario usuario;
    private Arquivo arquivo;
    private Form<Arquivo> form;
    private FileUploadField fileUploadField;

    public EnviarArquivoPage() {
	this.arquivo = new Arquivo();
	this.usuario = getUser();
	this.arquivo.setInstituicaoRecebe(instituicaoMediator.buscarInstituicaoIncial(TipoInstituicaoCRA.CRA.toString()));

	form = new Form<Arquivo>("form", getModel()) {

	    /****/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit() {
		final FileUpload uploadedFile = fileUploadField.getFileUpload();
		arquivo.setNomeArquivo(uploadedFile.getClientFileName());
		arquivo.setDataRecebimento(new LocalDate().toDate());
		getFeedbackMessages().clear();

		try {
		    ArquivoMediator arquivoRetorno = arquivoMediator.salvar(arquivo, uploadedFile, getUsuario());
		    setArquivo(arquivoRetorno.getArquivo());

		    if (arquivo != null) {
			if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)) {
			    info("O arquivo de Remessa " + arquivo.getNomeArquivo()
				    + " enviado, foi processado com sucesso !");
			} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO)) {
			    info("O arquivo de Confirmação " + arquivo.getNomeArquivo()
				    + " enviado, foi processado com sucesso !");
			} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
			    setResponsePage(new RelatorioRetornoPage("O arquivo de Retorno " + arquivo.getNomeArquivo()
				    + " enviado, foi processado com sucesso !", getArquivo(), "ENVIAR ARQUIVO"));
			} else if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			    info("O arquivo de Desistência de Protesto " + arquivo.getNomeArquivo()
				    + " enviado, foi processado com sucesso !");
			}
		    }
		    for (Exception exception : arquivoRetorno.getErros()) {
			if (arquivo.getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			    warn(DesistenciaException.class.cast(exception).toString());
			} else {
			    warn(exception.getMessage());
			}
		    }
		    arquivoRetorno.getErros().clear();

		} catch (InfraException ex) {
		    logger.error(ex.getMessage());
		    getFeedbackPanel().error(ex.getMessage());
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

    public Usuario getUsuario() {
	return usuario;
    }

    public Arquivo getArquivo() {
	return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
	this.arquivo = arquivo;
    }

    @Override
    protected IModel<Arquivo> getModel() {
	return new CompoundPropertyModel<Arquivo>(arquivo);
    }
}
