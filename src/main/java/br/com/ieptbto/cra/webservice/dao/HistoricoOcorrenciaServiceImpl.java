package br.com.ieptbto.cra.webservice.dao;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;

/**
 * @author Thasso Araújo
 *
 */
@WebService(name = "/HistoricoOcorrenciaService", endpointInterface = "br.com.ieptbto.cra.webservice.dao.IHistoricoOcorrenciaWS")
@Path("/HistoricoOcorrenciaService")
public class HistoricoOcorrenciaServiceImpl implements IHistoricoOcorrenciaWS {

	@Resource
	private WebServiceContext wsctx;
	public static final Logger logger = Logger.getLogger(HistoricoOcorrenciaServiceImpl.class);
	private ClassPathXmlApplicationContext context;
	private HistoricoOcorrenciaService historicoService;
	private UsuarioMediator usuarioMediator;
	private Usuario usuario;
	
	@Override
	@WebMethod(operationName = "Remessa")
	@GET
	public String ocorrencia(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha, 
			@WebParam(name = "user_ocor") String ocorrencia, @WebParam(name = "user_code_Portador") String codigoPortador,
			@WebParam(name = "user_prot") String protocolo, @WebParam(name = "user_nosso_num") String nossoNumero,
			@WebParam(name = "user_num_titulo") String numeroTitulo) {
		
		init(login, senha);
		return historicoService.processarOcorrencia();
	}
	
	private void init(String login, String senha) {
		context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		historicoService = (HistoricoOcorrenciaService) context.getBean("historicoOcorrenciaService");
		usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");
		setUsuario(login, senha);
	}

	private void setUsuario(String login, String pass) {
		logger.info("Inicio WebService pelo usuario= " + login);
		this.usuario = usuarioMediator.autenticar(login, pass);
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
}
