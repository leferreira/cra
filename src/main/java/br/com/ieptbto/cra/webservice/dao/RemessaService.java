package br.com.ieptbto.cra.webservice.dao;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServices;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.webservice.receiver.RemessaReceiver;
import br.com.ieptbto.cra.webservice.vo.AbstractMensagemVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RemessaService extends CraWebService {

	@Autowired
	private ArquivoMediator arquivoMediator;
	@Autowired
	private RemessaReceiver remessaReceiver;

	/**
	 * Envio de remessas pelos bancos/convênios
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		this.craAcao = CraAcao.ENVIO_ARQUIVO_REMESSA;
		this.nomeArquivo = nomeArquivo;

		try {
			if (usuario == null) {
				return setResposta(null, nomeArquivo);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario, nomeArquivo);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.ENVIO_ARQUIVO_REMESSA)) {
				return mensagemServicoIndisponivel(usuario);
			}
			Arquivo arquivoJaEnviado = arquivoMediator.buscarArquivoPorNomeInstituicaoEnvio(usuario, nomeArquivo);
			if (arquivoJaEnviado != null) {
				if (arquivoJaEnviado.getInstituicaoEnvio().getLayoutPadraoXML() != LayoutPadraoXML.SERPRO) {
					return setRespostaArquivoJaEnviadoAnteriormente(usuario, nomeArquivo, arquivoJaEnviado);
				}
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}
			AbstractMensagemVO mensagemCra = remessaReceiver.receber(usuario, nomeArquivo, dados);
			return gerarMensagemRelatorio(mensagemCra);
			 
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), "Erro interno no processamento do arquivo de Remessa " + nomeArquivo + ".", ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
	}
	
	/**
	 * Consulta de remessas pelos cartórios
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @return
	 */
	public String buscarRemessa(String nomeArquivo, Usuario usuario) {
		this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_REMESSA;
		this.nomeArquivo = nomeArquivo;

		try {
			if (usuario == null) {
				return setResposta(null, nomeArquivo);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.DOWNLOAD_ARQUIVO_REMESSA)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (nomeArquivo != null) {
				if (StringUtils.isEmpty(nomeArquivo.trim())) {
					return setRespostaPadrao(usuario, nomeArquivo, CodigoErro.CARTORIO_NOME_ARQUIVO_INVALIDO);
				}
			}
			RemessaVO remessaVO = arquivoMediator.buscarRemessaParaCartorio(usuario, nomeArquivo);
			if (remessaVO == null) {
				return setRespostaPadrao(usuario, nomeArquivo, CodigoErro.CARTORIO_ARQUIVO_NAO_EXISTE);
			}
            loggerCra.sucess(usuario, getCraAcao(), "Arquivo de Remessa " + nomeArquivo +
                    " recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
			return gerarRespostaArquivo(remessaVO, nomeArquivo, CONSTANTE_REMESSA_XML);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), "Erro interno ao construir o arquivo de Remessa " + nomeArquivo + 
					" recebido por " + usuario.getInstituicao().getNomeFantasia() + ".", ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
	}

	@Override
	protected String gerarRespostaArquivo(Object object, String nomeArquivo, String constanteRemessaXml) {
		Writer writer = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(constanteRemessaXml), Object.class, object);
			marshaller.marshal(element, writer);
			String msg = writer.toString();
			msg = msg.replace(" xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"arquivoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			writer.close();
			return msg;

		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
        }
		return null;
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
			msg = msg.replace(" xsi:type=\"mensagemXmlSerproVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"mensagemXmlDesistenciaCancelamentoSerproVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"mensagemXmlVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"arquivoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"instituicaoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			writer.close();
			return msg;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}

	/**
	 * Envio de remessas pelos convênios com layouts personalizados
	 * 
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processarRemessaConvenio(Usuario usuario, String dados) {
		this.craAcao = CraAcao.ENVIO_ARQUIVO_REMESSA;
		this.nomeArquivo = CONSTANTE_CONVENIO;

		try {
			if (usuario == null) {
				return setResposta(usuario, nomeArquivo);
			}
			Arquivo arquivoJaEnviado = arquivoMediator.buscarArquivoEnviadoConvenio(usuario);
			if (arquivoJaEnviado != null) {
				return setRespostaArquivoJaEnviadoAnteriormente(usuario, nomeArquivo, arquivoJaEnviado);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}
			AbstractMensagemVO mensagemCra = remessaReceiver.receberRemessaConvenio(usuario, nomeArquivo, dados);
			return gerarMensagemRelatorio(mensagemCra);
			 
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), "Erro interno no processamento do arquivo de Remessa " + nomeArquivo + ".", ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
	}
}