package br.com.ieptbto.cra.page.relatorio;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.beans.ArquivoBean;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.NivelDetalhamentoRelatorio;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BaseForm;
import br.com.ieptbto.cra.report.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioArquivosForm extends BaseForm<ArquivoBean> {

	/***/
	private static final long serialVersionUID = 1L;

	public RelatorioArquivosForm(String id, IModel<ArquivoBean> model) {
		super(id, model);

	}

	@Override
	protected void onSubmit() {
		ArquivoBean formBean = getModelObject();
		TipoArquivoFebraban tipoArquivo = formBean.getTipoArquivo();
		NivelDetalhamentoRelatorio tipoRelatorio = formBean.getTipoRelatorio();
		Instituicao instituicao = formBean.getInstituicao();
		LocalDate dataInicio = null;
		LocalDate dataFim = null;

		try {
			if (formBean.getDataInicio() != null) {
				if (formBean.getDataFim() != null) {
					dataInicio = new LocalDate(formBean.getDataInicio());
					dataFim = new LocalDate(formBean.getDataFim());
					if (!dataInicio.isBefore(dataFim))
						if (!dataInicio.isEqual(dataFim))
							throw new InfraException("A data de início deve ser antes da data fim.");
				} else
					throw new InfraException("As duas datas devem ser preenchidas.");
			}

			JasperPrint jasperPrint = new RelatorioUtil().gerarRelatorioArquivos(tipoArquivo, tipoRelatorio, instituicao, dataInicio, dataFim);
			File pdf = File.createTempFile("report", ".pdf");
			JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
			IResourceStream resourceStream = new FileResourceStream(pdf);
			getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
					"CRA_RELATORIO_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));
		} catch (InfraException ex) {
			error(ex.getMessage());
		} catch (Exception e) {
			error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
			e.printStackTrace();
		}
	}
}