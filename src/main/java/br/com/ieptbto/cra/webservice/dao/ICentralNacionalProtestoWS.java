package br.com.ieptbto.cra.webservice.dao;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;

import org.springframework.stereotype.Repository;

/**
 * @author Thasso Araújo
 *
 */
@Repository
@WebService
public interface ICentralNacionalProtestoWS {

	/**
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "cartorio")
	@GET
	public String cartorio(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "user_dados") String dados);

	/**
	 * @param login
	 * @param senha
	 * @return
	 */
	@WebMethod(operationName = "centralNacionalProtesto")
	@GET
	public String centralNacionalProtesto(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha);

}
