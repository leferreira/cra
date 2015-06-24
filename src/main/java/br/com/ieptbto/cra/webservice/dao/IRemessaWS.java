package br.com.ieptbto.cra.webservice.dao;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;

import org.springframework.stereotype.Repository;

/**
 * 
 * @author Lefer
 *
 */
@Repository
@WebService
public interface IRemessaWS {

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "remessa")
	@GET
	public String remessa(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @return
	 */
	@WebMethod(operationName = "confirmacao")
	@GET
	public String confirmacao(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @return
	 */
	@WebMethod(operationName = "retorno")
	@GET
	public String retorno(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "cancelamento")
	@GET
	public String cancelamento(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "desistencia")
	@GET
	public String desistencia(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "autorizacaoCancelamento")
	@GET
	public String autorizacaoCancelamento(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);

}
