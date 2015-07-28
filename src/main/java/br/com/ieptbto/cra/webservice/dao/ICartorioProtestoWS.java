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
public interface ICartorioProtestoWS {

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @return
	 */
	@WebMethod(operationName = "RemessaCartorio")
	@GET
	public String remessaCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param dados
	 * @param senha
	 * @return
	 */
	@WebMethod(operationName = "ConfirmacaoCartorio")
	@GET
	public String confirmacaoCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
 	 * @param dados
	 * @param senha
	 * @return
	 */
	@WebMethod(operationName = "RetornoCartorio")
	@GET
	public String retornoCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "CancelamentoCartorio")
	@GET
	public String cancelamentoCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "DesistenciaCartorio")
	@GET
	public String desistenciaCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);

	/**
	 * 
	 * @param nomeArquivo
	 * @param login
	 * @param senha
	 * @param dados
	 * @return
	 */
	@WebMethod(operationName = "AutorizacaoCancelamentoCartorio")
	@GET
	public String autorizacaoCancelamentoCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados);
	
	
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
