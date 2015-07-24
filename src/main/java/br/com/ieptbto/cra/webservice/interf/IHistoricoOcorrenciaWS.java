package br.com.ieptbto.cra.webservice.interf;

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
			@WebParam(name = "user_ocor") String ocorrencia, @WebParam(name = "user_code_Portador") String codigoPortador,
			@WebParam(name = "user_prot") String protocolo, @WebParam(name = "user_nosso_num") String nossoNumero,
			@WebParam(name = "user_num_titulo") String numeroTitulo);
}
