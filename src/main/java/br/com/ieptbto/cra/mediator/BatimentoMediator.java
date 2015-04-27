package br.com.ieptbto.cra.mediator;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class BatimentoMediator {

	@Autowired
	private BatimentoDAO batimentoDao;
	
	public List<Remessa> buscarRetornosParaBatimento(){
		return batimentoDao.buscarRetornosParaBatimento();
	}
	
	public BigDecimal buscarValorDeTitulosPagos(Remessa retorno){
		if (!retorno.getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)){
			throw new InfraException("O arquivo não é um arquivo de RETORNO válido.");
		} else {
			return batimentoDao.buscarValorDeTitulosPagos(retorno);
		}
	}
	
	public void realizarBatimento(List<Remessa> retornos){
		batimentoDao.realizarBatimento(retornos);
	}
}
