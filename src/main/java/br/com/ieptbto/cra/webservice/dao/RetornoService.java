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
import br.com.ieptbto.cra.entidade.vo.ArquivoGenericoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServices;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.ArquivoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.XmlFormatterUtil;
import br.com.ieptbto.cra.webservice.receiver.RetornoReceiver;
import br.com.ieptbto.cra.webservice.vo.AbstractMensagemVO;

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

		ArquivoGenericoVO arquivoVO = null;
		try {
			if (usuario == null) {
				return setResposta(usuario, arquivoVO, nomeArquivo);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario, arquivoVO, nomeArquivo);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.DOWNLOAD_ARQUIVO_RETORNO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}

			List<RemessaVO> remessas = arquivoMediator.buscarArquivos(nomeArquivo, usuario.getInstituicao());
			if (remessas.isEmpty()) {
				return setRespostaArquivoEmProcessamento(usuario, nomeArquivo);
			}
			String resposta = gerarResposta(usuario, remessas, nomeArquivo, CONSTANTE_RETORNO_XML);
			loggerCra.sucess(usuario, getCraAcao(),
					"Arquivo de Retorno " + nomeArquivo + " recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
			return resposta;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(), "Erro interno ao construir o arquivo de Retorno " + nomeArquivo + " recebido por "
					+ usuario.getInstituicao().getNomeFantasia() + ".", e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
	}

	private String gerarResposta(Usuario usuario, List<RemessaVO> remessas, String nomeArquivo, String constanteRetornoXml) {
		StringBuffer conteudo = new StringBuffer();
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";

		conteudo.append("<retorno>\r\n");
		LayoutPadraoXML layoutTranmissao = usuario.getInstituicao().getLayoutPadraoXML();
		if (layoutTranmissao == LayoutPadraoXML.SERPRO) {
			conteudo.append("<nome_arquivo>" + nomeArquivo + "</nome_arquivo>\r\n");
		}
		for (RemessaVO remessaVO : remessas) {
			if (layoutTranmissao == LayoutPadraoXML.SERPRO) {
				conteudo.append("<comarca CodMun=\"" + remessaVO.getCabecalho().getCodigoMunicipio() + "\">\r\n");
				String msg = gerarRespostaArquivo(remessaVO, nomeArquivo, CONSTANTE_RETORNO_XML);
				msg = msg.replace("<retorno xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">", "").replace("</retorno>", "");
				msg = msg.replace(xml, "");
				conteudo.append(msg);
				conteudo.append("</comarca>\r\n");
			} else if (layoutTranmissao == LayoutPadraoXML.CRA_NACIONAL) {
				String msg = gerarRespostaArquivo(remessaVO, nomeArquivo, CONSTANTE_RETORNO_XML);
				msg = msg.replace("<retorno xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">", "").replace("</retorno>", "");
				msg = msg.replace(xml, "");
				conteudo.append(msg);
			}
		}
		conteudo.append("</retorno>");
		return XmlFormatterUtil.format(xml + conteudo.toString());
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

		try {
			if (usuario == null) {
				return setResposta(usuario, new ArquivoGenericoVO(), nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.ENVIO_ARQUIVO_RETORNO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			Arquivo arquivoJaEnviado = arquivoMediator.buscarArquivoPorNomeInstituicaoEnvio(usuario, nomeArquivo);
			if (arquivoJaEnviado != null) {
				return setRespostaArquivoJaEnviadoCartorio(usuario, nomeArquivo, arquivoJaEnviado);
			}
			AbstractMensagemVO mensagemCra = retornoReceiver.receber(usuario, nomeArquivo, dados);
			String resposta = gerarMensagemRelatorio(mensagemCra);
			loggerCra.sucess(usuario, CraAcao.ENVIO_ARQUIVO_RETORNO, resposta,
	                "Arquivo " + nomeArquivo + ", enviado por " + usuario.getInstituicao().getNomeFantasia() + ", recebido com sucesso.");
			return resposta;
			
		} catch (InfraException ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), ex.getMessage(), ex, dados);
			return setRespostaErrosServicosCartorios(usuario, nomeArquivo, ex.getMessage());
		} catch (ArquivoException ex) {
			logger.info(ex.getMessage(), ex);
			if (CodigoErro.CRA_ARQUIVO_ENVIADO_ANTERIORMENTE.equals(ex.getCodigoErro())) {
				loggerCra.error(usuario, getCraAcao(), "Arquivo " + ex.getNomeArquivo() + " já enviado anteriormente em "
						+ DataUtil.localDateToString(ex.getDataEnvio()) + ".", ex);
				return setRespostaArquivoJaEnviadoCartorio(usuario, ex.getNomeArquivo(), ex.getDataEnvio());
			}
			return setRespostaErrosServicosCartorios(usuario, nomeArquivo, ex.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(), e.getMessage(), e, dados);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
	}

	@Override
	protected String gerarMensagemRelatorio(Object object) {
		Writer writer = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(CONSTANTE_RELATORIO_XML), Object.class, object);
			marshaller.marshal(element, writer);
			String msg = writer.toString();
			msg = msg.replace("<retorno xsi:type=\"mensagemXmlVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">", "<relatorio>");
			msg = msg.replace("</retorno>", "</relatorio>");
			msg = msg.replace(" xsi:type=\"mensagemXmlVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
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
	
	@Override
	protected String gerarRespostaArquivo(Object object, String nomeArquivo, String nameNode) {
		Writer writer = new StringWriter();
		
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(nameNode), Object.class, object);
			marshaller.marshal(element, writer);
			writer.close();
			return writer.toString();
			
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}
}