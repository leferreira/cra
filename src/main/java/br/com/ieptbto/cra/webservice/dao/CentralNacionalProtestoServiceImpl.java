package br.com.ieptbto.cra.webservice.dao;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.XmlFormatterUtil;
import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.ws.WebServiceContext;

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
	@WebMethod(operationName = "cartorioProtesto5anos")
	@GET
	public String cartorioProtesto5anos(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return centralNacionalProtestoCartorioService.processarLoteCnp5Anos(usuario, dados);
	}

	@Override
	@WebMethod(operationName = "cartorioDiario")
	@GET
	public String cartorioDiario(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return centralNacionalProtestoCartorioService.processarLoteCnpDiario(usuario, dados);
	}

	@Override
	@WebMethod(operationName = "centralNacionalProtesto")
	@GET
	public String centralNacionalProtesto(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "data") String data) {
		init(login, senha);
		return centralNacionalProtestoService.processar(usuario, data);
	}

	@Override
	@WebMethod(operationName = "cartoriosDisponiveis")
	@GET
	public String cartoriosDisponiveis(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		init(login, senha);
		return centralNacionalProtestoService.consultar(usuario);
	}

	@Override
	@WebMethod(operationName = "consultaProtesto")
	@GET
	public String consultaProtesto(@WebParam(name = "documentoDevedor") String documentoDevedor) {
		if (documentoDevedor == null) {
			StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\" ?>");
			xml.append("<protesto>");
			xml.append("<mensagem>CPF/CNPJ não informado</mensagem>");
			xml.append("</protesto>");
			return XmlFormatterUtil.format(xml.toString());
		}

		if (context == null) {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}

		centralNacionalProtestoCartorioService =
				(CentralNacionalProtestoCartorioService) context.getBean("centralNacionalProtestoCartorioService");

		logger.info("O CPF/CNPJ " + documentoDevedor + " foi consultado na Central Nacional de Protesto. ");

		return centralNacionalProtestoCartorioService.consultarProtesto(documentoDevedor);
	}

	private void init(String login, String senha) {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}
		usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");
		centralNacionalProtestoCartorioService =
				(CentralNacionalProtestoCartorioService) context.getBean("centralNacionalProtestoCartorioService");
		centralNacionalProtestoService = (CentralNacionalProtestoService) context.getBean("centralNacionalProtestoService");
		setUsuario(login, senha);
	}

	private void setUsuario(String login, String senha) {
		logger.info("[ " + DataUtil.localDateToString(new LocalDate()) + " " + DataUtil.localTimeToString("HH:mm:ss", new LocalTime())
				+ " ] ======= WebService ====== Autenticando : " + login + "... ");
		this.usuario = usuarioMediator.autenticarWS(login, senha);
		if (usuario != null) {
			logger.info("[ " + DataUtil.localDateToString(new LocalDate()) + " " + DataUtil.localTimeToString("HH:mm:ss", new LocalTime())
					+ " ] ======= WebService ====== Usuario : " + login + " ===== Serviço: Central Nacional de Protesto =======");
		} else {
			logger.info("[ " + DataUtil.localDateToString(new LocalDate()) + " " + DataUtil.localTimeToString("HH:mm:ss", new LocalTime())
					+ " ] ======= WebService ====== Falha na autenticação do usuário " + login + " !");
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}
}
