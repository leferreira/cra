package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoDAO;
import br.com.ieptbto.cra.dao.RetornoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RetornoMediator {

	private static final int NUMERO_SEQUENCIAL_RETORNO = 1;
	
	@Autowired
	InstituicaoDAO instituicaoDAO;
	@Autowired
	TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	RetornoDAO retornoDao;
	
	private Instituicao cra;
	private TipoArquivo tipoArquivo;
	private Arquivo arquivo;
	
	public List<Remessa> buscarRetornosParaBatimento(){
		return retornoDao.buscarRetornosParaBatimento();
	}
	
	public List<Remessa> buscarRetornosConfirmados(){
		return retornoDao.buscarRetornosConfirmados();
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		
		if (!retorno.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)){
			throw new InfraException("O arquivo não é um arquivo de RETORNO válido.");
		} else 
			return retornoDao.buscarValorDeTitulosPagos(retorno);
	}
	
	public void confirmarBatimentos(List<Remessa> retornos){
		retornoDao.confirmarBatimento(retornos);
	}
	
	public void removerConfirmado(Remessa retorno){
		retornoDao.removerConfirmado(retorno);
	}
	
	public void gerarRetornos(Usuario usuarioAcao, List<Remessa> retornos){
		List<Arquivo> arquivosDeRetorno = new ArrayList<Arquivo>();
		
		cra = instituicaoDAO.buscarInstituicaoInicial("CRA");
		tipoArquivo = tipoArquivoDAO.buscarPorTipoArquivo(TipoArquivoEnum.RETORNO);
		
		Instituicao instituicaoDestino = new Instituicao();
		for (Remessa retorno: retornos){
			
			if (arquivo == null || !instituicaoDestino.equals(retorno.getInstituicaoDestino())){
				instituicaoDestino = retorno.getInstituicaoDestino();
				criarNovoArquivoDeRetorno(instituicaoDestino, retorno);
				
				List<Remessa> retornosDaInstituicao = retornoDao.buscarRetornosConfirmadosPorInstituicao(instituicaoDestino);
				arquivo.setRemessas(retornosDaInstituicao);
				
				if (!arquivosDeRetorno.contains(arquivo) && arquivo != null)
					arquivosDeRetorno.add(arquivo);
			} 
		}
		
		retornoDao.gerarRetornos(usuarioAcao ,arquivosDeRetorno);
	}
	
	private void criarNovoArquivoDeRetorno(Instituicao destino, Remessa retorno) {
		arquivo = new Arquivo();
		arquivo.setTipoArquivo(tipoArquivo);
		arquivo.setNomeArquivo(gerarNomeArquivoRetorno(retorno));
		arquivo.setInstituicaoRecebe(destino);
		arquivo.setInstituicaoEnvio(cra);
		arquivo.setDataEnvio(new LocalDate());
	}
	
	private String gerarNomeArquivoRetorno(Remessa retorno) {
		
		return TipoArquivoEnum.RETORNO.getConstante() 
			+ retorno.getCabecalho().getNumeroCodigoPortador()
			+ gerarDataArquivo()
			+ NUMERO_SEQUENCIAL_RETORNO;
	}

	private String gerarDataArquivo(){
		SimpleDateFormat dataPadraArquivo = new SimpleDateFormat("ddMM.yy");
		return dataPadraArquivo.format(new Date()).toString();
	}
}
