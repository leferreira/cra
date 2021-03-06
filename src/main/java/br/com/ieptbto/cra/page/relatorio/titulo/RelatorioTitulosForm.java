package br.com.ieptbto.cra.page.relatorio.titulo;

import br.com.ieptbto.cra.beans.RelatorioBean;
import br.com.ieptbto.cra.enumeration.TipoExportacaoRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BaseForm;
import br.com.ieptbto.cra.report.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileOutputStream;

public class RelatorioTitulosForm extends BaseForm<RelatorioBean> {

	private static final long serialVersionUID = 1L;
	private FileUploadField fileUploadField;

	public RelatorioTitulosForm(String id, IModel<RelatorioBean> model, FileUploadField fileUploadField) {
		super(id, model);
		this.fileUploadField = fileUploadField;
	}

	@Override
	protected void onSubmit() {
		RelatorioBean bean = getModelObject();

		try {
            DataUtil.validarPeriodoDataInicialFinal(bean.getDataInicio(), bean.getDataFim());
            if (bean.getBancoConvenio() == null && bean.getCartorio() == null && fileUploadField.getFileUpload() == null && bean.getDataInicio() == null) {
                throw new InfraException("Por favor, informe o Banco/Convênio, o Município ou anexe uma planilha de pendências !");
            }

			if (bean.getTipoExportacao().equals(TipoExportacaoRelatorio.PDF.toString())) {
				JasperPrint jasperPrint = new RelatorioUtil().gerarRelatorioTitulosPorSituacao(bean.getSituacaoTituloRelatorio(), bean.getBancoConvenio(),
                        new LocalDate(bean.getDataInicio()), new LocalDate(bean.getDataFim()));
				if (jasperPrint.getPages().isEmpty()) {
				    throw new InfraException("O relatório não encontrou registros no período selecionado!");
                }
				File pdf = File.createTempFile("report", ".pdf");
				JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
				IResourceStream resourceStream = new FileResourceStream(pdf);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
						"CRA_RELATORIO_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));

			} else if (bean.getTipoExportacao().equals(TipoExportacaoRelatorio.CSV.toString())) {
				if (fileUploadField.getFileUpload() != null) {
					if (!fileUploadField.getFileUpload().getClientFileName().toLowerCase().contains(".xlsx")) {
						throw new InfraException("Formato de planilha não permitido! Por favor envie somente com a extenção XLSX...");
					}
					setResponsePage(new RelatorioTitulosCsvPage(fileUploadField.getFileUpload()));
				} else {
					setResponsePage(new RelatorioTitulosCsvPage(bean.getSituacaoTituloRelatorio(), bean.getTipoInstituicao(), bean.getBancoConvenio(),
							bean.getCartorio(), new LocalDate(bean.getDataInicio()), new LocalDate(bean.getDataFim())));
				}
			}

		} catch (InfraException ex) {
			error(ex.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
		}
	}
}