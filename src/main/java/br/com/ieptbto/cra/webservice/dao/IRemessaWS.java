package br.com.ieptbto.cra.webservice.dao;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.springframework.stereotype.Repository;

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
	@Path("arquivo/{nomeArquivo}")
	public String gerarXML(@PathParam("nomeArquivo") @WebParam(name = "nomeArquivo") String nomeArquivo);

}
