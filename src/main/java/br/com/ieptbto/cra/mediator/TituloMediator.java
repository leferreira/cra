package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloMediator {

	@Autowired
	TituloDAO tituloDAO;
	
	public List<TituloRemessa> buscarListaTitulos(TituloRemessa titulo, Usuario user){
		return tituloDAO.buscarListaTitulos(titulo, user);
	}
	
	public List<Historico> getHistoricoTitulo(TituloRemessa titulo){
		return tituloDAO.getHistoricoTitulo(titulo);
	}
}
