package br.com.ieptbto.cra.webservice.dao;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoCartorioService extends CnpWebService {

	public String processar(Usuario usuario, String dados) {
		setUsuario(usuario);
		return null;
	}
}