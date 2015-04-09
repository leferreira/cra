package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TituloDAO;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class TituloMediator {

	@Autowired
	private TituloDAO tituloDAO;
	
	public List<Titulo> buscarListaTitulos(Titulo titulo, Usuario user){
		return tituloDAO.buscarListaTitulos(titulo, user);
	}
}
