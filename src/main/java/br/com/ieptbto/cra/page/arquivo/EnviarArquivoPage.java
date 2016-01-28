package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang.StringUtils;
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
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.TituloException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

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
	private RelatorioMediator relatorioMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	private Arquivo arquivo;
	private Instituicao cra;
	private Form<Arquivo> form;
	private FileUploadField fileUploadField;

	public EnviarArquivoPage() {
		this.arquivo = new Arquivo();
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
					ArquivoMediator arquivoRetorno = arquivoMediator.salvar(arquivo, uploadedFile, getUser());
					for (Exception exception : arquivoRetorno.getErros()) {
						warn(exception.getMessage());
					}
					arquivoRetorno.getErros().clear();

					if (arquivoRetorno.getArquivo() != null) {
						if (!arquivoRetorno.getArquivo().getRemessas().isEmpty()) {
							info("O arquivo " + arquivo.getNomeArquivo() + " com " + arquivoRetorno.getArquivo().getRemessas().size()+ " Remessa(s), salvo com sucesso.");
						} else if (arquivoRetorno.getArquivo().getRemessaDesistenciaProtesto() != null
		                        && arquivoRetorno.getArquivo().getRemessaDesistenciaProtesto().getDesistenciaProtesto() != null
		                        && !arquivoRetorno.getArquivo().getRemessaDesistenciaProtesto().getDesistenciaProtesto().isEmpty()) {
							info("A desistência de protesto " + arquivo.getNomeArquivo() + " com "+ arquivoRetorno.getArquivo().getRemessaDesistenciaProtesto().getDesistenciaProtesto().size()
		                            + " desistencia(s), foi salvo com sucesso.");
						}
					}
					
					if (arquivoRetorno.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)
		                    && getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {

						JasperReport jasperReport = JasperCompileManager
	                            .compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRetorno.jrxml"));
						JasperPrint jasperPrint = relatorioMediator.relatorioRetorno(jasperReport, arquivoRetorno.getArquivo(),
	                            getUser().getInstituicao());

						File pdf = File.createTempFile("report", ".pdf");
						JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
						IResourceStream resourceStream = new FileResourceStream(pdf);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
	                            "CRA_RELATORIO_" + arquivoRetorno.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));

						getFeedbackPanel().info("O arquivo " + arquivo.getNomeArquivo() + " com "
	                            + arquivoRetorno.getArquivo().getRemessas().size() + " Remessa(s), salvo com sucesso.");
					}
				} catch (TituloException ex) {
					logger.error(ex.getMessage());
					for (Exception erro : ex.getErros()) {
						if (StringUtils.isNotBlank(ex.getMessage())) {
							warn(erro.getMessage());
						}
					}
					ex.getErros().clear();
					getFeedbackPanel().error(ex.getMessage());
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

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

	public Instituicao getCra() {
		return cra;
	}

	public void setCra(Instituicao cra) {
		this.cra = cra;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}
}
