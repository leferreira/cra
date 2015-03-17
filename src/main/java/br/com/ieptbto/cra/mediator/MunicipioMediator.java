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
	
	public Municipio adicionarMunicipio(Municipio municipio){
		return municipioDao.salvar(municipio);
	}
	
	public Municipio alterarMunicipio(Municipio municipio){
		return municipioDao.alterar(municipio);
	}
	
	public boolean isMunicipioNaoExiste(Municipio municipio){
		return true;
	}
	
	public List<Municipio> listarTodos(){
		return municipioDao.listarTodos();
	}
}
