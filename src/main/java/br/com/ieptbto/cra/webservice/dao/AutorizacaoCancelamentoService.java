package br.com.ieptbto.cra.webservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoGenericoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServices;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.DesistenciaCancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;
import br.com.ieptbto.cra.webservice.VO.MensagemXmlDesistenciaCancelamentoSerpro;
import br.com.ieptbto.cra.webservice.VO.TituloDetalhamentoSerpro;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class AutorizacaoCancelamentoService extends CraWebService {

	@Autowired
	private AutorizacaoCancelamentoMediator autorizacaoCancelamentoMediator;
	@Autowired
	private InstituicaoMediator instituicaoMediator;

	private List<Exception> erros;
	private Object relatorio;

	/**
	 * @param nomeArquivo
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		this.craAcao = CraAcao.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO;
		this.nomeArquivo = nomeArquivo;

		Arquivo arquivo = new Arquivo();
		ArquivoGenericoVO arquivoVO = new ArquivoGenericoVO();
		try {
			if (usuario == null) {
				return setResposta(usuario, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}

			arquivo = autorizacaoCancelamentoMediator.processarAutorizacaoCancelamento(nomeArquivo, dados, getErros(), usuario);
			if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
				return gerarMensagemSerpro(arquivo, CONSTANTE_RELATORIO_XML);
			}
			relatorio = gerarResposta(arquivo, usuario);
			loggerCra.sucess(usuario, getCraAcao(), "O arquivo de Autorização de Cancelamento " + nomeArquivo + ", enviado por "
					+ usuario.getInstituicao().getNomeFantasia() + ", foi processado com sucesso.");

		} catch (InfraException ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), ex.getMessage(), ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(),
					"Erro interno no processamento do arquivo de Autorização de Cancelamento " + nomeArquivo + "." + e.getMessage(), e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return gerarMensagem(relatorio, CONSTANTE_RELATORIO_XML);
	}

	private MensagemXml gerarResposta(Arquivo arquivo, Usuario usuario) {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		MensagemXml mensagemRetorno = new MensagemXml();
		Descricao desc = new Descricao();
		Detalhamento detal = new Detalhamento();
		detal.setMensagem(mensagens);

		mensagemRetorno.setDescricao(desc);
		mensagemRetorno.setDetalhamento(detal);
		mensagemRetorno.setCodigoFinal(CodigoErro.CRA_SUCESSO.getCodigo());
		mensagemRetorno.setDescricaoFinal(CodigoErro.CRA_SUCESSO.getDescricao());

		desc.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		desc.setTipoArquivo(Descricao.XML_UPLOAD_SUSTACAO);
		desc.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		desc.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		desc.setUsuario(usuario.getNome());
		desc.setNomeArquivo(nomeArquivo);

		for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(ac.getCabecalhoCartorio().getCodigoMunicipio());
			mensagem.setDescricao(formatarMensagemRetorno(ac));
			mensagens.add(mensagem);
		}

		for (Exception ex : getErros()) {
			DesistenciaCancelamentoException exception = DesistenciaCancelamentoException.class.cast(ex);
			Mensagem mensagem = new Mensagem();
			mensagem.setDescricao(exception.getDescricao());
			mensagem.setMunicipio(exception.getMunicipio());
			mensagem.setCodigo(exception.getCodigoErro().getCodigo());
			mensagens.add(mensagem);
			loggerCra.alert(usuario, getCraAcao(), "Comarca Rejeitada: " + exception.getMunicipio() + " - " + exception.getMessage());
		}
		return mensagemRetorno;
	}

	private String formatarMensagemRetorno(AutorizacaoCancelamento ac) {
		Instituicao instituicao = instituicaoMediator.getCartorioPorCodigoIBGE(ac.getCabecalhoCartorio().getCodigoMunicipio());
		return instituicao.getNomeFantasia() + " (" + ac.getAutorizacoesCancelamentos().size() + ") ";
	}

	private String gerarMensagemSerpro(Arquivo arquivo, String constanteRelatorioXml) {
		MensagemXmlDesistenciaCancelamentoSerpro mensagemAC = new MensagemXmlDesistenciaCancelamentoSerpro();
		mensagemAC.setNomeArquivo(arquivo.getNomeArquivo());
		mensagemAC.setTitulosDetalhamento(new ArrayList<TituloDetalhamentoSerpro>());

		for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
			for (PedidoAutorizacaoCancelamento pedidoAC : ac.getAutorizacoesCancelamentos()) {
				TituloDetalhamentoSerpro titulo = new TituloDetalhamentoSerpro();
				titulo.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
				titulo.setCodigoCartorio(pedidoAC.getAutorizacaoCancelamento().getCabecalhoCartorio().getCodigoCartorio());
				titulo.setNumeroTitulo(pedidoAC.getNumeroTitulo());
				titulo.setNumeroProtocoloCartorio(pedidoAC.getNumeroProtocolo());
				titulo.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(pedidoAC.getDataProtocolagem()));
				titulo.setCodigo(pedidoAC.getCodigoErroProcessamento().getCodigo());
				titulo.setOcorrencia(pedidoAC.getCodigoErroProcessamento().getDescricao());

				mensagemAC.getTitulosDetalhamento().add(titulo);
			}
		}
		return gerarMensagem(mensagemAC, constanteRelatorioXml);
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}