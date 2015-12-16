package br.com.ieptbto.cra.webservice.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.ConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaCnp;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfirmacaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ConfirmacaoService extends CraWebService {

	@Autowired
	private RemessaMediator remessaMediator;
	@Autowired
	private ConfirmacaoMediator confirmacaoMediator;
	private ArquivoVO arquivoVO;
	private ArquivoConfirmacaoVO arquivoConfirmacaoVO;
	private ConfirmacaoVO confirmacaoVO;

	public String processar(String nomeArquivo, Usuario usuario) {
		List<RemessaCnp> remessas = new ArrayList<RemessaCnp>();
		ArquivoVO arquivoVO = null;
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		
		if (getUsuario() == null) {
			return setResposta(LayoutPadraoXML.CRA_NACIONAL, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
		}
		if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
			return setResposta(usuario.getInstituicao().getLayoutPadraoXML(), arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
		}
		if (!getNomeArquivo().contains(getUsuario().getInstituicao().getCodigoCompensacao())) {
			return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
		}

		remessas = remessaMediator.buscarArquivos(getNomeArquivo(), getUsuario().getInstituicao());
		if (remessas.isEmpty()) {
			return setRespostaArquivoEmProcessamento(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
		}
		return gerarResposta(usuario.getInstituicao().getLayoutPadraoXML() ,remessas, getNomeArquivo(), CONSTANTE_CONFIRMACAO_XML);
	}

	private String gerarResposta(LayoutPadraoXML layoutPadraoResposta,List<RemessaCnp> remessas, String nomeArquivo, String constanteConfirmacaoXml) {
		StringBuffer string = new StringBuffer();
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>";
		String cabecalho = "<confirmacao>";

		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			string.append("<nome_arquivo>" + nomeArquivo + "</nome_arquivo>");
		}
		for (RemessaCnp remessaVO : remessas) {
			if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
				string.append("<comarca CodMun=\""+ remessaVO.getCabecalho().getCodigoMunicipio() +"\">");
				String msg = gerarMensagem(remessaVO, CONSTANTE_CONFIRMACAO_XML).replace("</confirmacao>", "").replace(cabecalho, "");
				string.append(msg);
				string.append("</comarca>");
			} else {
				String msg = gerarMensagem(remessaVO, CONSTANTE_CONFIRMACAO_XML).replace("</confirmacao>", "").replace(cabecalho, "");
				string.append(msg);
					
			}
		}
		string.append("</confirmacao>");
		return xml + cabecalho + string.toString();

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
			msg = msg.replace("<confirmacao xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">", "");
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

	public String enviarConfirmacao(String nomeArquivo, Usuario usuario, String dados) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		
		if (getUsuario() == null){
			return setResposta(LayoutPadraoXML.CRA_NACIONAL, new ArquivoVO(), nomeArquivo, CONSTANTE_RELATORIO_XML);
		}
		
		if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
			return setRespostaArquivoEmBranco(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
		}

		setArquivoConfirmacaoVO(converterStringArquivoVO(dados));

		if (getArquivoConfirmacaoVO() == null || getUsuario() == null) {
			ArquivoVO arquivo = new ArquivoVO();
			return setResposta(usuario.getInstituicao().getLayoutPadraoXML(), arquivo, nomeArquivo, CONSTANTE_CONFIRMACAO_XML);
		}

		setConfirmacaoVO(ConversorArquivoVO.converterParaRemessaVO(getArquivoConfirmacaoVO()));

		return gerarMensagem(confirmacaoMediator.processarXML(getConfirmacaoVO(), getUsuario(), nomeArquivo), CONSTANTE_CONFIRMACAO_XML);

	}

	private ArquivoConfirmacaoVO converterStringArquivoVO(String dados) {
		JAXBContext context;
		ArquivoConfirmacaoVO arquivo = null;

		try {
			context = JAXBContext.newInstance(ArquivoConfirmacaoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
				}
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivo = (ArquivoConfirmacaoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return arquivo;
	}

	public ArquivoVO getArquivoVO() {
		return arquivoVO;
	}

	public void setArquivoVO(ArquivoVO arquivoVO) {
		this.arquivoVO = arquivoVO;
	}

	public ConfirmacaoVO getConfirmacaoVO() {
		return confirmacaoVO;
	}

	public void setConfirmacaoVO(ConfirmacaoVO confirmacaoVO) {
		this.confirmacaoVO = confirmacaoVO;
	}

	public ArquivoConfirmacaoVO getArquivoConfirmacaoVO() {
		return arquivoConfirmacaoVO;
	}

	public void setArquivoConfirmacaoVO(ArquivoConfirmacaoVO arquivoConfirmacaoVO) {
		this.arquivoConfirmacaoVO = arquivoConfirmacaoVO;
	}

}
