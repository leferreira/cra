package br.com.ieptbto.cra.webservice.dao;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;

/**
 * @author Thasso Araújo
 *
 */
@WebService(name = "/CentralNacionalProtestoService", endpointInterface = "br.com.ieptbto.cra.webservice.dao.ICentralNacionalProtestoWS")
@Path("/CentralNacionalProtestoService")
public class CentralNacionalProtestoServiceImpl implements ICentralNacionalProtestoWS {

	public static final Logger logger = Logger.getLogger(CentralNacionalProtestoServiceImpl.class);

	@Resource
	private WebServiceContext wsctx;
	private UsuarioMediator usuarioMediator;
	private Usuario usuario;
	private CentralNacionalProtestoCartorioService centralNacionalProtestoCartorioService;
	private CentralNacionalProtestoService centralNacionalProtestoService;
	private ClassPathXmlApplicationContext context;

	@Override
	@WebMethod(operationName = "cartorio")
	@GET
	public String cartorio(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return centralNacionalProtestoCartorioService.processar(usuario, dados);
	}

	@Override
	@WebMethod(operationName = "centralNacionalProtesto")
	@GET
	public String centralNacionalProtesto(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		init(login, senha);
		return centralNacionalProtestoService.processar(usuario);
	}

	@Override
	@WebMethod(operationName = "centralNacionalProtesto")
	@GET
	public String consultaProtesto(@WebParam(name = "documentoDevedor") String documentoDevedor) {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}

		centralNacionalProtestoCartorioService = (CentralNacionalProtestoCartorioService) context.getBean("centralNacionalProtestoCartorioService");
		return centralNacionalProtestoCartorioService.consultarProtesto(documentoDevedor);
	}

	private void init(String login, String senha) {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}
		usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");
		centralNacionalProtestoCartorioService = (CentralNacionalProtestoCartorioService) context.getBean("centralNacionalProtestoCartorioService");
		centralNacionalProtestoService = (CentralNacionalProtestoService) context.getBean("centralNacionalProtestoService");
		setUsuario(login, senha);
	}

	private void setUsuario(String login, String senha) {
		logger.info("Inicio WebService pelo usuario= " + login);
		this.usuario = usuarioMediator.autenticarWS(login, senha);
	}

	public Usuario getUsuario() {
		return usuario;
	}
}
