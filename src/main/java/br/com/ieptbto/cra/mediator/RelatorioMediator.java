package br.com.ieptbto.cra.mediator;

import net.sf.jasperreports.engine.JRException;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.relatorio.RelatorioUtils;

@Service
public class RelatorioMediator {

	@Autowired
	ArquivoDAO arquivoDao;
	@Autowired
	RemessaDAO remessaDao;
	@Autowired
	TipoArquivoDAO tipoArquivoDao;
	@Autowired
	TituloDAO tituloDao;
 
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Instituicao instituicao;
	private Municipio pracaProtesto;
	private Instituicao bancoPortador;
	
	public byte[] novoRelatorioDeArquivoDetalhado(Instituicao instituicao,Arquivo arquivo){
		this.instituicao=instituicao;
		return chamarRelatorioArquivoDetalhado(arquivo);
	}
	/**
	 * Método que recebe os parâmetros do relatório sintético para BANCOS.
	 * 
	 * 	@param 
	 * @return 
	 * 	
	 * */
	public byte[] novoRelatorioSintetico(Instituicao instituicao, String tipoArquivo, LocalDate dataInicio, LocalDate dataFim) throws JRException {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.instituicao = instituicao;
		return chamarRelatorioSinteticoPorTipoArquivo(tipoArquivo);
	}	

	/**
	 * Método que recebe os parâmetros do relatório analítico pra BANCOS.
	 * 
	 * @param 
	 * 	
	 * */
	public byte[] novoRelatorioAnaliticoBanco(Instituicao instituicao, String tipoArquivo, LocalDate dataInicio, LocalDate dataFim, Municipio municipio) {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.instituicao = instituicao;
		return chamarRelatorioAnaliticoPorTipoArquivoBanco(tipoArquivo);
	}

	/**
	 * Método que recebe os parâmetros do relatório analítico para Cartórios.
	 * 
	 * @param 
	 * 	
	 * */
	public byte[] novoRelatorioAnaliticoCartorio(Instituicao instituicao, String tipoArquivo, LocalDate dataInicio, LocalDate dataFim, Instituicao portador) {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.instituicao = instituicao;
		return chamarRelatorioAnaliticoPorTipoArquivoCartorio(tipoArquivo);
	}

	private RelatorioUtils getRelatorioUtils(){
		return new RelatorioUtils();
	}
	
	private byte[] chamarRelatorioSinteticoPorTipoArquivo(String stringTipo) throws JRException{
		
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(stringTipo);
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			return getRelatorioUtils().relatorioSinteticoDeRemessa(instituicao, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			return getRelatorioUtils().relatorioSinteticoDeConfirmacao(instituicao, dataInicio, dataFim);
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			return getRelatorioUtils().relatorioSinteticoDeRetorno(instituicao, dataInicio, dataFim);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
	}

	private byte[] chamarRelatorioAnaliticoPorTipoArquivoBanco(String tipoArquivo){
		
		TipoArquivoEnum tipo = TipoArquivoEnum.getTipoArquivoEnum(tipoArquivo);
		if (tipo.equals(TipoArquivoEnum.REMESSA)) {
			return getRelatorioUtils().relatorioAnaliticoDeRemessaBanco(instituicao, pracaProtesto, dataInicio, dataFim);
		} else if (tipo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			return getRelatorioUtils().relatorioAnaliticoDeConfirmacaoBanco(instituicao, pracaProtesto, dataInicio, dataFim);
		} else if (tipo.equals(TipoArquivoEnum.RETORNO)) {
			return getRelatorioUtils().relatorioAnaliticoDeRetornoBanco(instituicao, pracaProtesto, dataInicio, dataFim);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
	}
	
	private byte[] chamarRelatorioAnaliticoPorTipoArquivoCartorio(String tipoArquivo){
		
		TipoArquivoEnum tipo = TipoArquivoEnum.getTipoArquivoEnum(tipoArquivo);
		if (tipo.equals(TipoArquivoEnum.REMESSA)) {
			return getRelatorioUtils().relatorioAnaliticoDeRemessaCartorio(instituicao, bancoPortador, dataInicio, dataFim);
		} else if (tipo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			return getRelatorioUtils().relatorioAnaliticoDeConfirmacaoCartorio(instituicao, bancoPortador, dataInicio, dataFim);
		} else if (tipo.equals(TipoArquivoEnum.RETORNO)) {
			return getRelatorioUtils().relatorioAnaliticoDeRetornoCartorio(instituicao, bancoPortador, dataInicio, dataFim);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
	}
	
	private byte[] chamarRelatorioArquivoDetalhado(Arquivo arquivo){
		TipoArquivoEnum tipo = TipoArquivoEnum.getTipoArquivoEnum(arquivo.getTipoArquivo().getTipoArquivo().constante);
		if (tipo.equals(TipoArquivoEnum.REMESSA)) {
			Arquivo remessa = arquivoDao.buscarArquivoPorNome(arquivo);
			return getRelatorioUtils().relatorioArquivoDetalhadoRemessa(remessa);
		} else if (tipo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			Arquivo confirmacao = arquivoDao.buscarArquivoPorNome(arquivo);
			return getRelatorioUtils().relatorioArquivoDetalhadoConfirmacao(confirmacao);
		} else if (tipo.equals(TipoArquivoEnum.RETORNO)) {
			Arquivo retorno = arquivoDao.buscarArquivoPorNome(arquivo);
			return getRelatorioUtils().relatorioArquivoDetalhadoRetorno(retorno);
		} else {
			throw new InfraException("Não foi possível gerar o relatório. Entre em contato com a CRA!");
		}
	}
}
