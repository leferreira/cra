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

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.util.XmlFormatterUtil;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;
import br.com.ieptbto.cra.webservice.receiver.RetornoReceiver;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RetornoService extends CraWebService {

	@Autowired
	private ArquivoMediator arquivoMediator;
	@Autowired
	private RetornoReceiver retornoReceiver;

	private MensagemCra mensagemCra;
	private String resposta;

	/**
	 * Consulta de Retorno pelos bancos/convênios
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario) {
		this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_RETORNO;
		this.nomeArquivo = nomeArquivo;
		this.resposta = null;

		ArquivoVO arquivoVO = null;
		try {
			if (usuario == null) {
				return setResposta(usuario, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.DOWNLOAD_ARQUIVO_RETORNO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}

			List<RemessaVO> remessas = arquivoMediator.buscarArquivos(nomeArquivo, usuario.getInstituicao());
			if (remessas.isEmpty()) {
				return setRespostaArquivoEmProcessamento(usuario, nomeArquivo);
			}
			resposta = gerarResposta(usuario, remessas, nomeArquivo, CONSTANTE_RETORNO_XML);
			loggerCra.sucess(usuario, getCraAcao(),
					"Arquivo de Retorno " + nomeArquivo + " recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(),
					"Erro interno ao construir o arquivo de Retorno " + nomeArquivo + " recebido por " + usuario.getInstituicao().getNomeFantasia() + ".", e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return resposta;
	}

	private String gerarResposta(Usuario usuario, List<RemessaVO> remessas, String nomeArquivo, String constanteRetornoXml) {
		StringBuffer conteudo = new StringBuffer();
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";

		conteudo.append("<retorno>\r\n");
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			conteudo.append("<nome_arquivo>" + nomeArquivo + "</nome_arquivo>\r\n");
		}
		for (RemessaVO remessaVO : remessas) {
			if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
				conteudo.append("<comarca CodMun=\"" + remessaVO.getCabecalho().getCodigoMunicipio() + "\">\r\n");
				String msg = gerarMensagem(remessaVO, CONSTANTE_RETORNO_XML).replace("<retorno>", "").replace("</retorno>", "");
				msg = msg.replace(xml, "");
				conteudo.append(msg);
				conteudo.append("</comarca>\r\n");
			} else {
				String msg = gerarMensagem(remessaVO, CONSTANTE_RETORNO_XML).replace("<retorno>", "").replace("</retorno>", "");
				msg = msg.replace(xml, "");
				conteudo.append(msg);
			}
		}
		conteudo.append("</retorno>");
		return XmlFormatterUtil.format(conteudo.toString());

	}

	/**
	 * Envio de retorno pelo cartório
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String enviarRetorno(String nomeArquivo, Usuario usuario, String dados) {
		this.craAcao = CraAcao.ENVIO_ARQUIVO_RETORNO;
		this.nomeArquivo = nomeArquivo;
		this.mensagemCra = null;

		try {
			if (usuario == null) {
				return setResposta(usuario, new ArquivoVO(), nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ENVIO_ARQUIVO_RETORNO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			Arquivo arquivoJaEnviado = arquivoMediator.buscarArquivoEnviado(usuario, nomeArquivo);
			if (arquivoJaEnviado != null) {
				return setRespostaArquivoJaEnviadoAnteriormente(usuario, nomeArquivo, arquivoJaEnviado);
			}
			mensagemCra = retornoReceiver.receber(usuario, nomeArquivo, dados);

		} catch (InfraException ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), ex.getMessage(), ex);
			return setRespostaErrosServicosCartorios(usuario, nomeArquivo, ex.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(), e.getMessage(), e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return gerarMensagemEnvioRetorno(mensagemCra, CONSTANTE_CONFIRMACAO_XML);
	}

	private String gerarMensagemEnvioRetorno(Object object, String nomeNo) {
		String msg = "";
		Writer writer = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(nomeNo), Object.class, object);
			marshaller.marshal(element, writer);
			msg = writer.toString();
			msg = msg.replace("<confirmacao xsi:type=\"mensagemXml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">", "<relatorio>");
			msg = msg.replace("</confirmacao>", "</relatorio>");
			writer.close();

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return msg;
	}
}