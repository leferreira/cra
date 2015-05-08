package br.com.ieptbto.cra.relatorio;

import java.util.HashMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

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
	
	private byte[] gerarRelatorio(JasperPrint jasperPrint) throws JRException{
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}
	
	public byte[] relatorioSinteticoDeRetorno(Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException{
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		parametros.put("INSTITUICAO_DESTINO_ID", bancoPortador.getId());

		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRetorno.jrxml"));
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);
		return gerarRelatorio(jasperPrint);
	}
	
	public byte[] relatorioSinteticoDeRemessa(Instituicao bancoPortador, LocalDate dataInicio, LocalDate dataFim) throws JRException{
		parametros.put("BANCO", bancoPortador.getNomeFantasia());
		parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
		parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
		parametros.put("INSTITUICAO_DESTINO_ID", bancoPortador.getId());

		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioSinteticoRemessa.jrxml"));
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);
		return gerarRelatorio(jasperPrint);
	}
	
	public byte[] relatorioSinteticoDeConfirmacao(Instituicao instituicao,
			LocalDate dataInicio, LocalDate dataFim) {
		// TODO Auto-generated method stub
		return null;
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
