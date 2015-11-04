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

import br.com.ieptbto.cra.conversor.ConversorArquivoVo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.ConfirmacaoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
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
		List<RemessaVO> remessas = new ArrayList<RemessaVO>();
		ArquivoVO arquivoVO = null;
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		if (getUsuario() == null) {
			return setResposta(arquivoVO, nomeArquivo, CONSTANTE_CONFIRMACAO_XML);
		}

		if (TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA.equals(getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao())) {
			remessas = remessaMediator.buscarArquivos(getNomeArquivo(), getUsuario().getInstituicao());
		}

		if (getNomeArquivo() == null) {
			return setResposta(arquivoVO, nomeArquivo, CONSTANTE_CONFIRMACAO_XML);
		}
		return gerarResposta(remessas, getNomeArquivo(), CONSTANTE_CONFIRMACAO_XML);
	}

	private String gerarResposta(List<RemessaVO> remessas, String nomeArquivo, String constanteConfirmacaoXml) {
		StringBuffer string = new StringBuffer();
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";
		String cabecalho = "<confirmacao xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		for (RemessaVO remessaVO : remessas) {
			String msg = gerarMensagem(remessaVO, CONSTANTE_CONFIRMACAO_XML).replace("</confirmacao>", "").replace(cabecalho, "");
			string.append(msg);
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
			logger.info("Remessa processada com sucesso.");
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

	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
			return setRespostaArquivoEmBranco(nomeArquivo);
		}

		setArquivoConfirmacaoVO(converterStringArquivoVO(dados));

		if (getArquivoConfirmacaoVO() == null || getUsuario() == null) {
			ArquivoVO arquivo = new ArquivoVO();
			return setResposta(arquivo, nomeArquivo, CONSTANTE_CONFIRMACAO_XML);
		}

		setConfirmacaoVO(ConversorArquivoVo.converterParaRemessaVO(getArquivoConfirmacaoVO()));

		return gerarMensagem(confirmacaoMediator.processarXML(getConfirmacaoVO(), getUsuario(), nomeArquivo), "confirmacao");

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
