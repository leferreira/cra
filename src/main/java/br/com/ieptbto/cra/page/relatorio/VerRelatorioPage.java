package br.com.ieptbto.cra.page.relatorio;

import java.io.OutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Response;

/**
 * @author Thasso Araújo
 *
 */
public class VerRelatorioPage extends WebPage {

	/***/
	private static final long serialVersionUID = 1L;
	private JasperPrint jasperPrint;
	
	public VerRelatorioPage(JasperPrint jasperPrint) throws JRException {
		this.jasperPrint = jasperPrint;
		gerarRelatorio();
	}
	
	/**
	 * Método que gera mostra o relatório no navegador
	 * 
	 * @param HttpServletResponse
	 * 			JasperPrint
	 */
	private void gerarRelatorio() throws JRException{
	   
		try {    
			Response response = getRequestCycle().getResponse();
			OutputStream servletOutputStream = response.getOutputStream();
			
	      JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
	      JasperExportManager.exportReportToPdf(jasperPrint);  
	      
//	      HttpServletResponse response = (HttpServletResponse)getResponse().getContainerResponse();
//	      response.setContentType("application/pdf");
//	      response.setHeader("Content-disposition", "attachment: filename=RelatorioCRA.pdf");
//	      ServletOutputStream servletOutputStream = response.getOutputStream();
//	      JRPdfExporter exporter = new JRPdfExporter();
//	      exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, jasperPrint);
//	      exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
//	      exporter.exportReport();
	      
		} catch (JRException e) {      
	      e.printStackTrace();      
	   } catch(Exception e){      
	      e.printStackTrace();      
	   }      
	} 
	
	public JasperPrint getJasperPrint() {
		return jasperPrint;
	}

	public void setJasperPrint(JasperPrint jasperPrint) {
		this.jasperPrint = jasperPrint;
	}
	
}
