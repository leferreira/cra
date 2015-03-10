package br.com.ieptbto.cra.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.InstituicaoMunicipioDAO;
import br.com.ieptbto.cra.entidade.InstituicaoMunicipio;

@Service
public class InstituicaoMunicipioMediator {

	@Autowired
	InstituicaoMunicipioDAO daoIM;
	
	public InstituicaoMunicipio salvar(InstituicaoMunicipio im){
		return daoIM.salvar(im);
	}
}
