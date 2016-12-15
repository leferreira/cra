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
	@WebMethod(operationName = "cartorioProtesto5anos")
	@GET
	public String cartorioProtesto5anos(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "user_dados") String dados);

	/**
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "cartorioDiario")
	@GET
	public String cartorioDiario(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "user_dados") String dados);

	/**
	 * @param login
	 * @param senha
	 * @return
	 */
	@WebMethod(operationName = "centralNacionalProtesto")
	@GET
	public String centralNacionalProtesto(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha);

	/**
	 * Consulta se há uma movimento na data informada.
	 * 
	 * @param login
	 * @param senha
	 * @param dataProcessamento
	 *            (formato yyyy-mm-dd)
	 * 
	 * @return o movimento da data informada se houver.
	 */
	@WebMethod(operationName = "consultaMovimentoPorData")
	@GET
	public String consultaMovimentoPorData(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "data") String data);

	/**
	 * @param login
	 * @param senha
	 * @return
	 */
	@WebMethod(operationName = "cartoriosDisponiveis")
	@GET
	public String cartoriosDisponiveis(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha);

	/**
	 * @param documentoDevedor
	 * @return
	 */
	@WebMethod(operationName = "consultaProtesto")
	@GET
	public String consultaProtesto(@WebParam(name = "documentoDevedor") String documentoDevedor);

}