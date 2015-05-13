package br.com.ieptbto.cra.relatorio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.wicket.request.http.WebResponse;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class RelatorioUtils {

	private HashMap<String, Object> parametros = new HashMap<String, Object>();
	
	public void gerarRelatorio(WebResponse response, JasperPrint jasperPrint) throws JRException{
	   response.setContentType("application/pdf");      
	   response.setHeader("Pragma", "no-cache");      
	   try {      
	      OutputStream servletOutputStream = response.getOutputStream();
	      JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
	      servletOutputStream.close();
	   } catch (JRException e) {      
	      e.printStackTrace();      
	   } catch (IOException e) {      
	      e.printStackTrace();      
	   }catch(Exception e){      
	      e.printStackTrace();      
	   }      
	} 
	
	/**
	 * 
	 * */
	public JasperPrint relatorioSinteticoDeRemessa(List<SinteticoJRDataSource> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException{
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRemessa.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	/**
	 * @throws JRException 
	 * 
	 * */
	public JasperPrint relatorioSinteticoDeConfirmacao(List<SinteticoJRDataSource> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoConfirmacao.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}

	/**
	 * 
	 * */
	public JasperPrint relatorioSinteticoDeRetorno(List<SinteticoJRDataSource> beans, Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException, IOException{
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));

		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRetorno.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	
	/**
	 * @throws JRException 
	 * 
	 * */
	public JasperPrint relatorioSinteticoDeRemessaPorMunicipio(List<SinteticoJRDataSource> beans, Municipio pracaProtesto,
			LocalDate dataInicio, LocalDate dataFim) throws JRException {
		parametros.put("MUNICIPIO", pracaProtesto.getNomeMunicipio());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		
		JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(beans);
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRemessaPorMunicipio.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
	}
	

	public byte[] relatorioAnaliticoDeRemessaBanco(Instituicao instituicao,
			Municipio pracaProtesto, LocalDate dataInicio, LocalDate dataFim) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] relatorioAnaliticoDeConfirmacaoBanco(Instituicao instituicao,
			Municipio pracaProtesto, LocalDate dataInicio, LocalDate dataFim) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] relatorioAnaliticoDeRetornoBanco(Instituicao instituicao,
			Municipio pracaProtesto, LocalDate dataInicio, LocalDate dataFim) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] relatorioAnaliticoDeRemessaCartorio(Instituicao instituicao,
			Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] relatorioAnaliticoDeConfirmacaoCartorio(
			Instituicao instituicao, Instituicao bancoPortador,
			LocalDate dataInicio, LocalDate dataFim) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] relatorioAnaliticoDeRetornoCartorio(Instituicao instituicao,
			Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] relatorioArquivoDetalhadoRemessa(Arquivo remessa) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] relatorioArquivoDetalhadoConfirmacao(Arquivo confirmacao) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] relatorioArquivoDetalhadoRetorno(Arquivo retorno) {
		// TODO Auto-generated method stub
		return null;
	}
}
