package br.com.ieptbto.cra.webservice.dao;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.MensagemRetornoXml;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ComarcasHomologadasService extends CraWebService {
	
	@Autowired
	private MunicipioMediator municipioMediator;
	
	public String verificarComarcasHomologadas(Usuario usuario, String codigoApresentante) {
		setUsuario(usuario);

		if (getUsuario() == null) {
			return getMensagemFalhaAutenticao();
		}
		
		List<Municipio> municipiosHomologados = municipioMediator.buscarMunicipiosAtivos();
		return gerarRetorno(codigoApresentante, municipiosHomologados);
	}
	
	protected String getMensagemFalhaAutenticao() {
		MensagemRetornoXml msgRetorno = new MensagemRetornoXml();
		
		msgRetorno.setDescricao(new Descricao());
		msgRetorno.setDetalhamento(new Detalhamento());
		
		msgRetorno.setCodigoFinal(CodigoErro.FALHA_NA_AUTENTICACAO.getCodigo());
		msgRetorno.setDescricaoFinal(CodigoErro.FALHA_NA_AUTENTICACAO.getDescricao());
		return gerarMensagem(msgRetorno, CONSTANTE_COMARCA_XML);
	}
	
	protected String gerarMensagem(Object mensagem, String nomeNo) {
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(mensagem.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(nomeNo), Object.class, mensagem);
			marshaller.marshal(element, writer);
			String msg = writer.toString();
			writer.close();
			return msg;

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}
	
	protected String gerarRetorno(String codigoApresentante, List<Municipio> municipiosHomologados){
		String xml = StringUtils.EMPTY;
		
		xml = xml.concat("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\" ?>");
		xml = xml.concat("<" + CONSTANTE_COMARCA_XML + ">");
		
		for (Municipio municipio : municipiosHomologados) {
			xml = xml.concat("<comarca>");
			xml = xml.concat("<codigo_comarca>"+ municipio.getCodigoIBGE() +"</codigo_comarca>");
			xml = xml.concat("<municipio codigo_municipio=\""+ municipio.getCodigoIBGE() +"\" nome=\""+ municipio.getNomeMunicipio() +"\" />");
			xml = xml.concat("</comarca>");
		}
		
		xml = xml.concat("</" + CONSTANTE_COMARCA_XML + ">");
		return xml;
	}
}
