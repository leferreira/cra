package br.com.ieptbto.cra.webservice.dao;

import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.UsuarioMediator;

/**
 * 
 * @author Lefer
 *
 */
// @WebServlet(urlPatterns = { "/RemessaService" })
@WebService(name = "/RemessaService", endpointInterface = "br.com.ieptbto.cra.webservice.dao.IRemessaWS")
@Path("/RemessaService")
public class RemessaServiceImpl implements IRemessaWS {// extends HttpServlet {

	RemessaMediator remessaMediator;
	UsuarioMediator usuarioMediator;
	Usuario usuario;
	@Resource
	WebServiceContext wsctx;
	ClassPathXmlApplicationContext context;

	@WebMethod(operationName = "gerarXML")
	@GET
	@Path("arquivo/{nomeArquivo}")
	public String gerarXML(@PathParam("nomeArquivo") @WebParam(name = "nomeArquivo") String nomeArquivo) {
		context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

		remessaMediator = (RemessaMediator) context.getBean("remessaMediator");
		ArquivoVO arquivo = getArquivo(nomeArquivo);
		return setResposta(arquivo);
	}

	private String setResposta(ArquivoVO arquivo) {
		if (arquivo == null) {
			return setRespostaArquivoInexistente(arquivo);
		}
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<ArquivoVO> element = new JAXBElement<ArquivoVO>(new QName("remessa"), ArquivoVO.class, arquivo);
			marshaller.marshal(element, writer);

			return writer.toString();

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String setRespostaArquivoInexistente(ArquivoVO arquivo) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArquivoVO getArquivo(String nome) {
		return remessaMediator.buscarArquivos(nome);
	}
}
