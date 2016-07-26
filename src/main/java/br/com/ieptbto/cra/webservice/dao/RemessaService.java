package br.com.ieptbto.cra.webservice.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RemessaService extends CraWebService {

	@Autowired
	RemessaMediator remessaMediator;
	@Autowired
	ArquivoMediator arquivoMediator;

	private Object relatorio;
	private String resposta;

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

		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			if (usuario == null) {
				return setResposta(usuario, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ENVIO_ARQUIVO_REMESSA)) {
				return mensagemServicoIndisponivel(usuario);
			}
			Arquivo arquivoJaEnviado = arquivoMediator.buscarArquivoEnviado(usuario, nomeArquivo);
			if (arquivoJaEnviado != null) {
				if (!arquivoJaEnviado.getInstituicaoEnvio().getCodigoCompensacao().trim().equals("582")) {
					return setRespostaArquivoJaEnviadoAnteriormente(usuario, nomeArquivo, arquivoJaEnviado);
				}
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}

			List<RemessaVO> remessasVO = ConversorArquivoVO.converterParaRemessaVO(converterStringArquivoVO(dados));
			relatorio = remessaMediator.processarArquivoXML(remessasVO, usuario, nomeArquivo);

			loggerCra.sucess(usuario, getCraAcao(), "O arquivo de Remessa " + nomeArquivo + ", enviado por "
					+ usuario.getInstituicao().getNomeFantasia() + ", foi processado com sucesso.");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), "Erro interno no processamento do arquivo de Remessa " + nomeArquivo + ".", ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return gerarMensagem(relatorio, CONSTANTE_RELATORIO_XML);
	}

	private ArquivoVO converterStringArquivoVO(String dados) {
		JAXBContext context;

		ArquivoVO arquivo = null;
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", StringUtils.EMPTY);
				}
				if (xmlRecebido.contains("<comarca CodMun")) {
					xmlRecebido = xmlRecebido.replaceAll("<comarca CodMun=.[0-9]+..", StringUtils.EMPTY);
				}
				if (xmlRecebido.contains("</comarca>")) {
					xmlRecebido = xmlRecebido.replaceAll("</comarca>", StringUtils.EMPTY);
				}
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivo = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return arquivo;
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

		Remessa remessa = null;
		RemessaVO remessaVO = null;
		try {
			if (usuario == null) {
				return setResposta(usuario, new ArquivoVO(), nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.DOWNLOAD_ARQUIVO_REMESSA)) {
				return mensagemServicoIndisponivel(usuario);
			}
			remessaVO = remessaMediator.buscarRemessaParaCartorio(remessa, usuario.getInstituicao(), nomeArquivo);
			if (remessaVO == null) {
				return setRespostaPadrao(usuario, nomeArquivo, CodigoErro.CARTORIO_ARQUIVO_NAO_EXISTE);
			}
			resposta = gerarResposta(remessaVO, nomeArquivo, CONSTANTE_REMESSA_XML);
			loggerCra.sucess(usuario, getCraAcao(),
					"Arquivo de Remessa " + nomeArquivo + " recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), "Erro interno ao construir o arquivo de Remessa " + nomeArquivo + " recebido por "
					+ usuario.getInstituicao().getNomeFantasia() + ".", ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return resposta;
	}

	private String gerarResposta(RemessaVO remessaVO, String nomeArquivo, String constanteConfirmacaoXml) {
		String msg = gerarMensagem(remessaVO, CONSTANTE_REMESSA_XML);
		msg = msg.replace(" xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
		return msg;
	}
}