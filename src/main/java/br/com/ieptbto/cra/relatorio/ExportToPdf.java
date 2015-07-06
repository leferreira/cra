package br.com.ieptbto.cra.relatorio;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

public class ExportToPdf {

	public ExportToPdf(HttpServletResponse servletResponse, JasperPrint jasperPrint, String nomeRelatorio) {
		
		try {
			HttpServletResponse response = servletResponse;
			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "attachment; filename=" + nomeRelatorio + ".pdf");
			ServletOutputStream servletOutputStream = response.getOutputStream();
			
			JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
//			JRPdfExporter exporterPdf = new JRPdfExporter();
//			exporterPdf.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//			exporterPdf.setParameter(JRExporterParameter.OUTPUT_STREAM, servletOutputStream);
//			exporterPdf.exportReport();
			
		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
	}
}
