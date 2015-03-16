package br.com.ieptbto.cra.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.PermissaoEnvioDAO;
import br.com.ieptbto.cra.entidade.PermissaoEnvio;

@Service
public class PermissaoEnvioMediator {

	@Autowired
	PermissaoEnvioDAO permissaoDAO;
	
	public PermissaoEnvio salvar(PermissaoEnvio permissaoEnvio){
		return permissaoDAO.salvar(permissaoEnvio);
	}
	
	public PermissaoEnvio alterar(PermissaoEnvio permissaoEnvio){
		return permissaoDAO.alterar(permissaoEnvio);
	}
}
