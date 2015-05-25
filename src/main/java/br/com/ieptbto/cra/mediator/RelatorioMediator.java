package br.com.ieptbto.cra.mediator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.hibernate.HibernateException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.RelatorioDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.relatorio.RelatorioUtils;
import br.com.ieptbto.cra.relatorio.SinteticoJRDataSource;


/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("unused")
@Service
public class RelatorioMediator{

	@Autowired
	RemessaDAO remessaDao = new RemessaDAO();
	@Autowired
	RelatorioDAO relatorioDao = new RelatorioDAO();
	
	private RelatorioUtils relatorioUtils = new RelatorioUtils();
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Instituicao instituicao;
	private Municipio pracaProtesto;
	private Instituicao bancoPortador;
	
	/**
	 * Método que recebe os parâmetros do relatório de um arquivo detalhado.
	 * 
	 * 	@param 
	 * @return 
	 * @throws IOException 
	 * 	
	 * */
	public JasperPrint novoRelatorioDeArquivoDetalhado(Instituicao instituicao,Arquivo arquivo, List<TituloRemessa> titulos) throws JRException{
		this.instituicao=instituicao;
		return chamarRelatorioArquivoDetalhado(arquivo, titulos);
	}
	
	/**
	 * Método que recebe os parâmetros do relatório sintético para BANCOS.
	 * 
	 * 	@param 
	 * @return 
	 * @throws IOException 
	 * 	
	 * */
	public JasperPrint novoRelatorioSintetico(Instituicao instituicao, String tipoArquivo, LocalDate dataInicio, LocalDate dataFim) throws JRException, HibernateException, SQLException, IOException {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.instituicao = instituicao;
		return chamarRelatorioSinteticoPorTipoArquivo(tipoArquivo);
	}	

	/**
	 * Método que recebe os parâmetros do relatório analítico para Cartórios.
	 * 
	 * @param 
	 * @return 
	 * 	
	 * */
	public JasperPrint novoRelatorioSinteticoPorMunicipio(Municipio municipio,	String tipoArquivo, LocalDate dataInicio, LocalDate dataFim)throws JRException, HibernateException, SQLException, IOException  {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.pracaProtesto = municipio;
		return chamarRelatorioSinteticoPorTipoArquivoDeMunicipios(tipoArquivo);
	}
	/**
	 * Método que recebe os parâmetros do relatório analíticos.
	 * 
	 * @param 
	 * 	
	 * */
	public JasperPrint novoRelatorioAnalitico(Instituicao instituicao, String tipoArquivo, LocalDate dataInicio, LocalDate dataFim, Municipio municipio) {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.instituicao = instituicao;
//		return chamarRelatorioAnaliticoPorTipoArquivo(tipoArquivo);
		return null;
	}
	
	private RelatorioUtils getRelatorioUtils(){
		return this.relatorioUtils;
	}
	
	private JasperPrint chamarRelatorioSinteticoPorTipoArquivo(String stringTipo) throws JRException, IOException{
		
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(stringTipo);
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			List<SinteticoJRDataSource> beans = relatorioDao.relatorioDeRemessaSintetico(instituicao, dataInicio, dataFim);
			return getRelatorioUtils().relatorioSinteticoDeRemessa(beans, instituicao, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			List<SinteticoJRDataSource> beans = relatorioDao.relatorioDeConfirmacaoSintetico(instituicao, dataInicio, dataFim);
			return getRelatorioUtils().relatorioSinteticoDeConfirmacao(beans ,instituicao, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			List<SinteticoJRDataSource> beans = relatorioDao.relatorioDeRetornoSintetico(instituicao, dataInicio, dataFim); 
			return getRelatorioUtils().relatorioSinteticoDeRetorno(beans, instituicao, dataInicio, dataFim);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
	}
	
	private JasperPrint chamarRelatorioSinteticoPorTipoArquivoDeMunicipios(String stringTipo) throws JRException, IOException{
		
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(stringTipo);
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			List<SinteticoJRDataSource> beans = relatorioDao.relatorioDeRemessaSinteticoPorMunicipio(pracaProtesto, dataInicio, dataFim);
			return getRelatorioUtils().relatorioSinteticoDeRemessaPorMunicipio(beans, pracaProtesto, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			List<SinteticoJRDataSource> beans = relatorioDao.relatorioDeConfirmacaoSinteticoPorMunicipio(pracaProtesto, dataInicio, dataFim);
			return getRelatorioUtils().relatorioSinteticoDeConfirmacaoPorMunicipio(beans ,pracaProtesto, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			List<SinteticoJRDataSource> beans = relatorioDao.relatorioDeRetornoSinteticoPorMunicipio(pracaProtesto, dataInicio, dataFim); 
			return getRelatorioUtils().relatorioSinteticoDeRetornoPorMunicipio(beans, pracaProtesto, dataInicio, dataFim);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
	}

	private JasperPrint chamarRelatorioAnaliticoPorTipoArquivo(String tipoArquivo){
		
		TipoArquivoEnum tipo = TipoArquivoEnum.getTipoArquivoEnum(tipoArquivo);
		if (tipo.equals(TipoArquivoEnum.REMESSA)) {
//			return getRelatorioUtils().relatorioAnaliticoDeRemessaBanco(instituicao, pracaProtesto, dataInicio, dataFim);
		} else if (tipo.equals(TipoArquivoEnum.CONFIRMACAO)) {
//			return getRelatorioUtils().relatorioAnaliticoDeConfirmacaoBanco(instituicao, pracaProtesto, dataInicio, dataFim);
		} else if (tipo.equals(TipoArquivoEnum.RETORNO)) {
//			return getRelatorioUtils().relatorioAnaliticoDeRetornoBanco(instituicao, pracaProtesto, dataInicio, dataFim);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
		return null;
	}
	
	private byte[] chamarRelatorioAnaliticoPorTipoArquivoCartorio(String tipoArquivo){
		
		TipoArquivoEnum tipo = TipoArquivoEnum.getTipoArquivoEnum(tipoArquivo);
		if (tipo.equals(TipoArquivoEnum.REMESSA)) {
//			return getRelatorioUtils().relatorioAnaliticoDeRemessaCartorio(instituicao, bancoPortador, dataInicio, dataFim);
		} else if (tipo.equals(TipoArquivoEnum.CONFIRMACAO)) {
//			return getRelatorioUtils().relatorioAnaliticoDeConfirmacaoCartorio(instituicao, bancoPortador, dataInicio, dataFim);
		} else if (tipo.equals(TipoArquivoEnum.RETORNO)) {
//			return getRelatorioUtils().relatorioAnaliticoDeRetornoCartorio(instituicao, bancoPortador, dataInicio, dataFim);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
		return null;
	}
	
	private JasperPrint chamarRelatorioArquivoDetalhado(Arquivo arquivo, List<TituloRemessa> titulos) throws JRException{

		TipoArquivoEnum tipo = TipoArquivoEnum.getTipoArquivoEnum(arquivo.getTipoArquivo().getTipoArquivo().getConstante());
		if (tipo.equals(TipoArquivoEnum.REMESSA)) {
			return getRelatorioUtils().relatorioArquivoDetalhadoRemessa(arquivo, titulos);
		} else if (tipo.equals(TipoArquivoEnum.CONFIRMACAO)) {
//			return getRelatorioUtils().relatorioArquivoDetalhadoConfirmacao(confirmacao);
		} else if (tipo.equals(TipoArquivoEnum.RETORNO)) {
//			return getRelatorioUtils().relatorioArquivoDetalhadoRetorno(retorno);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
		return null;
	}

}
