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
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.exception.CancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;
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
public class CancelamentoProtestoService extends CraWebService {

	@Autowired
	private CancelamentoProtestoMediator cancelamentoProtestoMediator;
	@Autowired 
	private InstituicaoMediator instituicaoMediator;
	private List<Exception> erros;

	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		Arquivo arquivo = new Arquivo();
		ArquivoVO arquivoVO = new ArquivoVO();
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);

		try {
			if (getUsuario() == null) {
				return setResposta(LayoutPadraoXML.CRA_NACIONAL, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario.getInstituicao().getLayoutPadraoXML(), arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (!getNomeArquivo().contains(getUsuario().getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
			}
	
			arquivo = gerarArquivoCancelamento(getUsuario().getInstituicao().getLayoutPadraoXML(), arquivo, dados);
			if (getUsuario().getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
				return gerarMensagemSerpro(arquivo, CONSTANTE_RELATORIO_XML);
			}
			return gerarMensagem(gerarResposta(arquivo, getUsuario()), CONSTANTE_RELATORIO_XML);
			
		} catch (CancelamentoException ex) {
			return gerarMensagemErroCancelamento(getUsuario().getInstituicao().getLayoutPadraoXML(), ex.getPedidosCancelamento());
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private Arquivo gerarArquivoCancelamento(LayoutPadraoXML layoutPadraoXML, Arquivo arquivo, String dados) {
		logger.info("Processando arquivo de cancelamento " + getNomeArquivo());
		return cancelamentoProtestoMediator.processarCancelamento(getNomeArquivo(), layoutPadraoXML , dados, getErros(), getUsuario());
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
		desc.setNomeArquivo(getNomeArquivo());

		for (CancelamentoProtesto cp : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(cp.getCabecalhoCartorio().getCodigoMunicipio());
			mensagem.setDescricao(formatarMensagemRetorno(cp));
			mensagens.add(mensagem);
		}

		for (Exception ex : getErros()) {
			XmlCraException exception = XmlCraException.class.cast(ex);
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo(exception.getErro().getCodigo());
			mensagem.setMunicipio(exception.getCodigoIbge());
			mensagem.setDescricao("Município: " + exception.getCodigoIbge() + " - " + exception.getMunicipio() + " - " + exception.getErro().getDescricao());
			mensagens.add(mensagem);
		}
		return mensagemRetorno;
	}

	private String formatarMensagemRetorno(CancelamentoProtesto cancelamentoProtesto) {
		Instituicao instituicao = instituicaoMediator.getCartorioPorCodigoIBGE(cancelamentoProtesto.getCabecalhoCartorio().getCodigoMunicipio());
		return instituicao.getNomeFantasia() + " (" + cancelamentoProtesto.getCancelamentos().size() + ") ";
	}

	private String gerarMensagemSerpro(Arquivo arquivo, String constanteRelatorioXml) {
		MensagemXmlDesistenciaCancelamentoSerpro mensagemCancelamento = new MensagemXmlDesistenciaCancelamentoSerpro();
		mensagemCancelamento.setNomeArquivo(arquivo.getNomeArquivo());
		mensagemCancelamento.setTitulosDetalhamento(new ArrayList<TituloDetalhamentoSerpro>());
		
		for (CancelamentoProtesto cp : arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto()) {
			for (PedidoCancelamento pedidoCancelamento : cp.getCancelamentos()) {
				TituloDetalhamentoSerpro titulo = new TituloDetalhamentoSerpro();
				titulo.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
				titulo.setCodigoCartorio(pedidoCancelamento.getCancelamentoProtesto().getCabecalhoCartorio().getCodigoCartorio());
				titulo.setNumeroTitulo(pedidoCancelamento.getNumeroTitulo());
				titulo.setNumeroProtocoloCartorio(pedidoCancelamento.getNumeroProtocolo());
				titulo.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(pedidoCancelamento.getDataProtocolagem()));
				titulo.setCodigo(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO.getCodigo());
				titulo.setOcorrencia(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO.getDescricao());
				
				mensagemCancelamento.getTitulosDetalhamento().add(titulo);
			}
		}
		return gerarMensagem(mensagemCancelamento, constanteRelatorioXml);
	}
	
	private String gerarMensagemErroCancelamento(LayoutPadraoXML layoutPadraoXML, List<PedidoCancelamento> pedidosCancelamento) {
		if (layoutPadraoXML.equals(LayoutPadraoXML.SERPRO)) {
			return gerarMensagemErroCancelamentoSerpro(pedidosCancelamento, CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagemErroCancelamento(pedidosCancelamento, CONSTANTE_RELATORIO_XML);
	}
	
	private String gerarMensagemErroCancelamento(List<PedidoCancelamento> pedidosCancelamento, String constanteRelatorioXml) {
		return null;
	}

	private String gerarMensagemErroCancelamentoSerpro(List<PedidoCancelamento> pedidosCancelamento, String constanteRelatorioXml) {
		MensagemXmlDesistenciaCancelamentoSerpro mensagemErroCancelamento = new MensagemXmlDesistenciaCancelamentoSerpro();
		mensagemErroCancelamento.setNomeArquivo(getNomeArquivo());
		mensagemErroCancelamento.setTitulosDetalhamento(new ArrayList<TituloDetalhamentoSerpro>());
		
		for (PedidoCancelamento pedidoCancelamento : pedidosCancelamento) {
			TituloDetalhamentoSerpro titulo = new TituloDetalhamentoSerpro();
			titulo.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
			titulo.setCodigoCartorio(pedidoCancelamento.getCancelamentoProtesto().getCabecalhoCartorio().getCodigoCartorio());
			titulo.setNumeroTitulo(pedidoCancelamento.getNumeroTitulo());
			titulo.setNumeroProtocoloCartorio(pedidoCancelamento.getNumeroProtocolo());
			titulo.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(pedidoCancelamento.getDataProtocolagem()));
			titulo.setCodigo(CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO.getCodigo());
			titulo.setOcorrencia(CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO.getDescricao());
			
			mensagemErroCancelamento.getTitulosDetalhamento().add(titulo);
		}
		return gerarMensagem(mensagemErroCancelamento, constanteRelatorioXml);
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}
