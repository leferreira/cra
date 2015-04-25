package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.BatimentoDAO;
import br.com.ieptbto.cra.entidade.Remessa;

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
	
	public void realizarBatimento(List<Remessa> retornos){
		batimentoDao.realizarBatimento(retornos);
	}
}
