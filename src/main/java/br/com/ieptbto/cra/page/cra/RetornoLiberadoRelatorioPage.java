package br.com.ieptbto.cra.page.cra;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Thasso Araújo
 *
 */
public class RetornoLiberadoRelatorioPage extends BasePage<Retorno> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private RelatorioMediator relatorioMediator;
	private Retorno retorno;

	public RetornoLiberadoRelatorioPage() {
		this.retorno = new Retorno();
		carregarComponentes();
	}

	private void carregarComponentes() {
		info(getMensagem());
		 add(linkDownloadRelatorioRetornoLiberado());
	}

	private Link<Retorno> linkDownloadRelatorioRetornoLiberado() {
		return new Link<Retorno>("relatorioRetornoLiberado") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				Connection connection = null;
				JasperPrint jasperPrint = null;
				HashMap<String, Object> parametros = new HashMap<String, Object>();

				try {
					Class.forName("org.postgresql.Driver");
					connection = DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra", "postgres", "@dminB3g1n");
					
					parametros.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_PATH);
					parametros.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_PATH + "ieptb.gif")));
					parametros.put("DATA_GERACAO", new LocalDate().toDate());
					 
					String urlJasper = "../../relatorio/RelatorioRetornoLiberado.jrxml";
					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(urlJasper));
					jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
		                    "CRA_RELATORIO_RETORNO_GERADO_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
	}

	public String getMensagem() {
		return "Os arquivos de retorno foram gerados com sucesso!";
	}

	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}
}
