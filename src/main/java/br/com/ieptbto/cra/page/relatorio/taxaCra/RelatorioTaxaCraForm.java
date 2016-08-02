package br.com.ieptbto.cra.page.relatorio.taxaCra;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.TituloTaxaCraBean;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.page.base.BaseForm;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioTaxaCraForm extends BaseForm<RelatorioTaxaCraFormBean> {

	/***/
	private static final long serialVersionUID = 1L;

	private static final String TAXA_CRA_PAGOS = "PAGOS";
	private static final String TAXA_CRA_CANCELADOS = "CANCELADOS";

	@SpringBean
	RelatorioMediator relatorioMediator;

	public RelatorioTaxaCraForm(String id, IModel<RelatorioTaxaCraFormBean> model) {
		super(id, model);
	}

	@Override
	protected void onSubmit() {
		RelatorioTaxaCraFormBean bean = getModelObject();
		LocalDate dataInicio = null;
		LocalDate dataFim = null;
		Instituicao convenio = bean.getConvenio();
		Instituicao cartorio = bean.getCartorio();

		try {
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

			JasperPrint jasperPrint = null;
			if (bean.getSituacaoTituloRelatorio().equals(TAXA_CRA_PAGOS)) {
				List<TituloTaxaCraBean> titulos = relatorioMediator.relatorioTitulosPagosTaxaCra(convenio, cartorio, dataInicio, dataFim);
				jasperPrint = new RelatorioUtil().gerarRelatorioTaxaCraPagos(titulos, convenio, cartorio, dataInicio, dataFim);

			} else if (bean.getSituacaoTituloRelatorio().equals(TAXA_CRA_CANCELADOS)) {
				jasperPrint = new RelatorioUtil().gerarRelatorioTaxaCraCancelados(convenio, cartorio, dataInicio, dataFim);
			}
			if (jasperPrint != null) {
				if (jasperPrint.getPages().isEmpty()) {
					throw new InfraException("O relatório não encontrou resultados!");
				}
			}

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