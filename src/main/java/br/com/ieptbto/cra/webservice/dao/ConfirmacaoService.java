package br.com.ieptbto.cra.webservice.dao;

import java.util.ArrayList;
import java.util.List;

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
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.util.XmlFormatterUtil;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;
import br.com.ieptbto.cra.webservice.receiver.ConfirmacaoReceiver;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ConfirmacaoService extends CraWebService {

	@Autowired
	RemessaMediator remessaMediator;
	@Autowired
	ArquivoMediator arquivoMediator;
	@Autowired
	ConfirmacaoReceiver confirmacaoReceiver;

	private MensagemCra mensagemCra;
	private String resposta;

	/**
	 * Métodos de consulta de confirmação pelos bancos/convênios
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario) {
		List<RemessaVO> remessas = new ArrayList<RemessaVO>();
		this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_CONFIRMACAO;
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
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.DOWNLOAD_ARQUIVO_CONFIRMACAO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}
			remessas = remessaMediator.buscarArquivos(nomeArquivo, usuario.getInstituicao());
			if (remessas.isEmpty()) {
				return setRespostaArquivoEmProcessamento(usuario, nomeArquivo);
			}

			resposta = gerarResposta(usuario, remessas, nomeArquivo, CONSTANTE_CONFIRMACAO_XML);
			loggerCra.sucess(usuario, getCraAcao(),
					"Arquivo de Confirmação " + nomeArquivo + " recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(), "Erro interno ao construir o arquivo de Confirmação " + nomeArquivo + " recebido por "
					+ usuario.getInstituicao().getNomeFantasia() + ".", e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return resposta;
	}

	private String gerarResposta(Usuario usuario, List<RemessaVO> remessas, String nomeArquivo, String constanteConfirmacaoXml) {
		StringBuffer string = new StringBuffer();
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>";
		String cabecalho = "<confirmacao>";

		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			string.append("<nome_arquivo>" + nomeArquivo + "</nome_arquivo>");
		}
		for (RemessaVO remessaVO : remessas) {
			if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
				string.append("<comarca CodMun=\"" + remessaVO.getCabecalho().getCodigoMunicipio() + "\">");
				String msg = gerarMensagem(remessaVO, CONSTANTE_CONFIRMACAO_XML).replace("</confirmacao>", "").replace(cabecalho, "");
				string.append(msg);
				string.append("</comarca>");
			} else {
				String msg = gerarMensagem(remessaVO, CONSTANTE_CONFIRMACAO_XML).replace("</confirmacao>", "").replace(cabecalho, "");
				string.append(msg);

			}
		}
		string.append("</confirmacao>");
		return XmlFormatterUtil.format(xml + cabecalho + string.toString());
	}

	/**
	 * Métodos de envio de confirmação pelo cartório
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String enviarConfirmacao(String nomeArquivo, Usuario usuario, String dados) {
		this.craAcao = CraAcao.ENVIO_ARQUIVO_CONFIRMACAO;
		this.nomeArquivo = nomeArquivo;
		this.mensagemCra = null;

		try {
			if (usuario == null) {
				return setResposta(usuario, new ArquivoVO(), nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ENVIO_ARQUIVO_CONFIRMACAO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}
			Arquivo arquivoJaEnviado = arquivoMediator.buscarArquivoEnviado(usuario, nomeArquivo);
			if (arquivoJaEnviado != null) {
				return setRespostaArquivoJaEnviadoAnteriormente(usuario, nomeArquivo, arquivoJaEnviado);
			}

			mensagemCra = confirmacaoReceiver.receber(usuario, nomeArquivo, dados);
			loggerCra.sucess(usuario, getCraAcao(), "O arquivo de Confirmação " + nomeArquivo + ", enviado por "
					+ usuario.getInstituicao().getNomeFantasia() + ", foi processado com sucesso.");
		} catch (InfraException ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), ex.getMessage(), ex);
			return setRespostaErrosServicosCartorios(usuario, nomeArquivo, ex.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(), e.getMessage(), e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return gerarMensagem(mensagemCra, CONSTANTE_CONFIRMACAO_XML);
	}
}