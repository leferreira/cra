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
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoGenericoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServices;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.DesistenciaCancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.vo.DescricaoVO;
import br.com.ieptbto.cra.webservice.vo.DetalhamentoVO;
import br.com.ieptbto.cra.webservice.vo.MensagemVO;
import br.com.ieptbto.cra.webservice.vo.MensagemXmlDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.webservice.vo.MensagemXmlVO;
import br.com.ieptbto.cra.webservice.vo.TituloDetalhamentoSerproVO;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CancelamentoProtestoService extends CraWebService {

	@Autowired
	CancelamentoProtestoMediator cancelamentoProtestoMediator;
	@Autowired
	InstituicaoMediator instituicaoMediator;
	private List<Exception> erros;
	private Object relatorio;

	/**
	 * @param nomeArquivo
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		this.craAcao = CraAcao.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO;
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
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}
			arquivo = cancelamentoProtestoMediator.processarCancelamento(nomeArquivo, dados, getErros(), usuario);
			if (usuario.equals(LayoutPadraoXML.SERPRO)) {
				return gerarMensagemSerpro(arquivo, CONSTANTE_RELATORIO_XML);
			}
			relatorio = gerarResposta(arquivo, usuario);

		} catch (InfraException ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), ex.getMessage(), ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), "Erro interno no processamento do arquivo de Cancelamento de Protesto " + nomeArquivo + "." + ex.getMessage(), ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return gerarMensagem(relatorio, CONSTANTE_RELATORIO_XML);
	}

	private MensagemXmlVO gerarResposta(Arquivo arquivo, Usuario usuario) {
		List<MensagemVO> mensagens = new ArrayList<MensagemVO>();
		MensagemXmlVO mensagemRetorno = new MensagemXmlVO();
		DescricaoVO desc = new DescricaoVO();
		DetalhamentoVO detal = new DetalhamentoVO();
		detal.setMensagem(mensagens);

		mensagemRetorno.setDescricao(desc);
		mensagemRetorno.setDetalhamento(detal);
		mensagemRetorno.setCodigoFinal(CodigoErro.CRA_SUCESSO.getCodigo());
		mensagemRetorno.setDescricaoFinal(CodigoErro.CRA_SUCESSO.getDescricao());

		desc.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		desc.setTipoArquivo(DescricaoVO.XML_UPLOAD_SUSTACAO);
		desc.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		desc.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		desc.setUsuario(usuario.getNome());
		desc.setNomeArquivo(nomeArquivo);

		for (CancelamentoProtesto cp : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {
			MensagemVO mensagem = new MensagemVO();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(cp.getCabecalhoCartorio().getCodigoMunicipio());
			mensagem.setDescricao(formatarMensagemRetorno(cp));
			mensagens.add(mensagem);
		}

		for (Exception ex : getErros()) {
			DesistenciaCancelamentoException exception = DesistenciaCancelamentoException.class.cast(ex);
			MensagemVO mensagem = new MensagemVO();
			mensagem.setDescricao(exception.getDescricao());
			mensagem.setMunicipio(exception.getMunicipio());
			mensagem.setCodigo(exception.getCodigoErro().getCodigo());
			mensagens.add(mensagem);
			loggerCra.alert(usuario, getCraAcao(), "Comarca Rejeitada: " + exception.getMunicipio() + " - " + exception.getMessage());
		}
		return mensagemRetorno;
	}

	private String formatarMensagemRetorno(CancelamentoProtesto cancelamentoProtesto) {
		Instituicao instituicao = instituicaoMediator.getCartorioPorCodigoIBGE(cancelamentoProtesto.getCabecalhoCartorio().getCodigoMunicipio());
		return instituicao.getNomeFantasia() + " (" + cancelamentoProtesto.getCancelamentos().size() + ") ";
	}

	private String gerarMensagemSerpro(Arquivo arquivo, String constanteRelatorioXml) {
		MensagemXmlDesistenciaCancelamentoSerproVO mensagemCancelamento = new MensagemXmlDesistenciaCancelamentoSerproVO();
		mensagemCancelamento.setNomeArquivo(arquivo.getNomeArquivo());
		mensagemCancelamento.setTitulosDetalhamento(new ArrayList<TituloDetalhamentoSerproVO>());

		for (CancelamentoProtesto cp : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {
			for (PedidoCancelamento pedidoCancelamento : cp.getCancelamentos()) {
				TituloDetalhamentoSerproVO titulo = new TituloDetalhamentoSerproVO();
				titulo.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
				titulo.setCodigoCartorio(pedidoCancelamento.getCancelamentoProtesto().getCabecalhoCartorio().getCodigoCartorio());
				titulo.setNumeroTitulo(pedidoCancelamento.getNumeroTitulo());
				titulo.setNumeroProtocoloCartorio(pedidoCancelamento.getNumeroProtocolo());
				titulo.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(pedidoCancelamento.getDataProtocolagem()));
				titulo.setCodigo(pedidoCancelamento.getCodigoErroProcessamento().getCodigo());
				titulo.setOcorrencia(pedidoCancelamento.getCodigoErroProcessamento().getDescricao());

				mensagemCancelamento.getTitulosDetalhamento().add(titulo);
			}
		}
		return gerarMensagem(mensagemCancelamento, constanteRelatorioXml);
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}