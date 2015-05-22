package br.com.ieptbto.cra.webservice.dao;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;

import org.springframework.stereotype.Repository;

import br.com.ieptbto.cra.entidade.vo.ArquivoVO;

/**
 * 
 * @author Lefer
 *
 */
@Repository
@WebService
public interface IRemessaWS {

	@WebMethod
	@GET
	public String arquivo(@WebParam(name = "nomeArquivo") String nomeArquivo, @WebParam(name = "login", header = true) String login,
	        @WebParam(name = "senha", header = true) String senha, @WebParam(name = "remessa") ArquivoVO remessa);

}
