package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.RemessaDAO;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RemessaMediator {

	@Autowired
	private RemessaDAO remessaDao;
	
	public List<Remessa> buscarRemessa(Instituicao instituicao){
		return remessaDao.listarRemessasPorInstituicao(instituicao);
	}
}
