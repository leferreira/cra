package br.com.ieptbto.cra.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.MunicipioDAO;
import br.com.ieptbto.cra.entidade.Municipio;

@Service
public class MunicipioMediator {

	@Autowired
	MunicipioDAO municipioDao;
	
	public List<Municipio> listarTodos(){
		return municipioDao.listarTodos();
	}
}
