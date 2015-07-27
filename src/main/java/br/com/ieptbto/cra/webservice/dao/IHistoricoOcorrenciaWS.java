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
public interface IHistoricoOcorrenciaWS {

	/**
	 * 
	 * @param login
	 * @param senha
	 * @param ocorrencia
	 * @param codigoPortador
	 * @param protocolo
	 * @param nossoNumero
	 * @param numeroTitulo
	 * @return
	 */
	@WebMethod(operationName = "Ocorrencia")
	@GET
	public String ocorrencia(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha, 
			@WebParam(name = "id_ocorrencia") String ocorrencia, @WebParam(name = "data_ocorrencia") String dataOcorrencia, 
			@WebParam(name = "cod_portador") String codigoPortador,@WebParam(name = "nosso_num") String nossoNumero,
			@WebParam(name = "num_titulo") String numeroTitulo);
}
