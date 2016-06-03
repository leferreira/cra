package br.com.ieptbto.cra.page.relatorio;

import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.page.base.BaseForm;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioArquivosForm extends BaseForm<RelatorioArquivosFormBean> {

	/***/
	private static final long serialVersionUID = 1L;

	public RelatorioArquivosForm(String id, IModel<RelatorioArquivosFormBean> model) {
		super(id, model);

	}

	@Override
	protected void onSubmit() {
		// TipoArquivoEnum tipoArquivo = radioTipoArquivo.getModelObject();
		// TipoRelatorio tipoRelatorio = radioTipoRelatorio.getModelObject();
		// Instituicao instituicao = comboInstituicao.getModelObject();
		// LocalDate dataInicio = null;
		// LocalDate dataFim = null;
		//
		// try {
		// if (dataEnvioInicio.getDefaultModelObject() != null) {
		// if (dataEnvioFinal.getDefaultModelObject() != null) {
		// dataInicio =
		// DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
		// dataFim =
		// DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
		// if (!dataInicio.isBefore(dataFim))
		// if (!dataInicio.isEqual(dataFim))
		// throw new InfraException("A data de início deve ser antes da data
		// fim.");
		// } else
		// throw new InfraException("As duas datas devem ser preenchidas.");
		// }
		//
		// JasperPrint jasperPrint =
		// new RelatorioUtil().gerarRelatorioArquivos(tipoArquivo,
		// tipoRelatorio, instituicao, dataInicio, dataFim);
		// File pdf = File.createTempFile("report", ".pdf");
		// JasperExportManager.exportReportToPdfStream(jasperPrint, new
		// FileOutputStream(pdf));
		// IResourceStream resourceStream = new FileResourceStream(pdf);
		// getRequestCycle().scheduleRequestHandlerAfterCurrent(new
		// ResourceStreamRequestHandler(resourceStream,
		// "CRA_RELATORIO_" + DataUtil.localDateToString(new
		// LocalDate()).replaceAll("/", "_") + ".pdf"));
		// } catch (InfraException ex) {
		// error(ex.getMessage());
		// } catch (Exception e) {
		// error("Não foi possível gerar o relatório ! Entre em contato com a
		// CRA !");
		// e.printStackTrace();
		// }
	}
}
