package br.com.ieptbto.cra.webservice.dao;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.util.XmlFormatterUtil;
import br.com.ieptbto.cra.webservice.vo.DescricaoVO;
import br.com.ieptbto.cra.webservice.vo.DetalhamentoVO;
import br.com.ieptbto.cra.webservice.vo.MensagemXmlVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class UsuariosComarcasHomologadasService extends CraWebService {

	@Autowired
	private MunicipioMediator municipioMediator;

	public String verificarComarcasHomologadas(Usuario usuario, String codigoApresentante) {
		if (usuario == null) {
			return getMensagemFalhaAutenticao();
		}

		List<Municipio> municipiosHomologados = municipioMediator.buscarMunicipiosAtivos();
		return gerarRetorno(codigoApresentante, municipiosHomologados);
	}

	protected String getMensagemFalhaAutenticao() {
		MensagemXmlVO msgRetorno = new MensagemXmlVO();
		msgRetorno.setDescricao(new DescricaoVO());
		msgRetorno.setDetalhamento(new DetalhamentoVO());

		msgRetorno.setCodigoFinal(CodigoErro.SERPRO_FALHA_NA_AUTENTICACAO.getCodigo());
		msgRetorno.setDescricaoFinal(CodigoErro.SERPRO_FALHA_NA_AUTENTICACAO.getDescricao());
		return gerarMensagemRelatorio(msgRetorno);
	}

	@Override
	protected String gerarMensagemRelatorio(Object object) {
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(object.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(CONSTANTE_COMARCA_XML), Object.class, object);
			marshaller.marshal(element, writer);
			String msg = writer.toString();
			writer.close();
			return msg;

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}

	protected String gerarRetorno(String codigoApresentante, List<Municipio> municipiosHomologados) {
		String xml = StringUtils.EMPTY;

		xml = xml.concat("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\" ?>");
		xml = xml.concat("<" + CONSTANTE_COMARCA_XML + ">");

		for (Municipio municipio : municipiosHomologados) {
			xml = xml.concat("<comarca>");
			xml = xml.concat("<codigo_comarca>" + municipio.getCodigoIBGE() + "</codigo_comarca>");
			xml = xml.concat("<municipio codigo_municipio=\"" + municipio.getCodigoIBGE() + "\" nome=\"" + municipio.getNomeMunicipio() + "\" />");
			xml = xml.concat("</comarca>");
		}
		xml = xml.concat("</" + CONSTANTE_COMARCA_XML + ">");
		return XmlFormatterUtil.format(xml);
	}

	public String verificarAcessoUsuario(Usuario usuario) {
		String xml = StringUtils.EMPTY;

		xml = xml.concat("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\" ?>");
		if (usuario == null) {
			xml = xml.concat("<usuario credenciaisCorretas=\"false\"/>");
			return XmlFormatterUtil.format(xml);
		}
		xml = xml.concat("<usuario credenciaisCorretas=\"true\"/>");
		if (usuario != null) {
			loggerCra.alert(usuario, CraAcao.VERIFICACAO_CREDENCIAIS_ACESSO_SUCESSO,
					"Acesso à CRA via WebServices liberado com sucesso para o usuário " + usuario.getNome() + ".");
		}
		return XmlFormatterUtil.format(xml);
	}

	@Override
	protected String gerarRespostaArquivo(Object object, String nomeArquivo, String nameNode) {
		// TODO Auto-generated method stub
		return null;
	}
}