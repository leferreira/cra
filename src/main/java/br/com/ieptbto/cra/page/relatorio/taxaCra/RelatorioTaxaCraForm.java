package br.com.ieptbto.cra.page.relatorio.taxaCra;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BaseForm;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

public class RelatorioTaxaCraForm extends BaseForm<RelatorioTaxaCraFormBean> {

	/***/
	private static final long serialVersionUID = 1L;

	private LocalDate dataInicio;
	private LocalDate dataFim;

	public RelatorioTaxaCraForm(String id, IModel<RelatorioTaxaCraFormBean> model) {
		super(id, model);
	}

	@Override
	protected void onSubmit() {
		RelatorioTaxaCraFormBean bean = getModelObject();

		try {
			if (bean.getConvenio() == null && bean.getCartorio() == null || bean.getConvenio() != null && bean.getCartorio() != null) {
				throw new InfraException("Por favor, informe o Banco/Convênio ou o Município !");
			}
			if (bean.getDataInicio() != null) {
				if (bean.getDataFim() != null) {
					dataInicio = new LocalDate(bean.getDataInicio());
					dataFim = new LocalDate(bean.getDataFim());
					if (!dataInicio.isBefore(dataFim))
						if (!dataInicio.isEqual(dataFim))
							throw new InfraException("A data de início deve ser antes da data fim.");
				} else
					throw new InfraException("As duas datas devem ser preenchidas.");
			}

			JasperPrint jasperPrint = new RelatorioUtil().gerarRelatorioTaxaCra(bean.getSituacaoTituloRelatorio(), bean.getConvenio(),
					bean.getCartorio(), dataInicio, dataFim);
			File pdf = File.createTempFile("report", ".pdf");
			JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
			IResourceStream resourceStream = new FileResourceStream(pdf);
			getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
					"CRA_RELATORIO_TAXA_CRA_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));

		} catch (InfraException ex) {
			error(ex.getMessage());
		} catch (Exception e) {
			error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
			e.printStackTrace();
		}
	}
}