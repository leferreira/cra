package br.com.ieptbto.cra.webservice.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoGenericoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;
import br.com.ieptbto.cra.webservice.receiver.RemessaReceiver;

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

	private MensagemCra mensagemCra;
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
		this.mensagemCra = null;

		ArquivoGenericoVO arquivoVO = new ArquivoGenericoVO();
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
			mensagemCra = remessaReceiver.receber(usuario, nomeArquivo, dados);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), "Erro interno no processamento do arquivo de Remessa " + nomeArquivo + ".", ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return gerarMensagem(mensagemCra, CONSTANTE_RELATORIO_XML);
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
		this.resposta = null;

		try {
			if (usuario == null) {
				return setResposta(usuario, new ArquivoGenericoVO(), nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.DOWNLOAD_ARQUIVO_REMESSA)) {
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
			resposta = gerarResposta(remessaVO, nomeArquivo, CONSTANTE_REMESSA_XML);
			loggerCra.sucess(usuario, getCraAcao(),
					"Arquivo de Remessa " + nomeArquivo + " recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(),
					"Erro interno ao construir o arquivo de Remessa " + nomeArquivo + " recebido por " + usuario.getInstituicao().getNomeFantasia() + ".", ex);
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