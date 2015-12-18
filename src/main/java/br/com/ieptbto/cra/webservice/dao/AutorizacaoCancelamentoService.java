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
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.exception.AutorizacaoCancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.XmlCraException;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
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
public class AutorizacaoCancelamentoService extends CraWebService {

	@Autowired
	private AutorizacaoCancelamentoMediator autorizacaoCancelamentoMediator;
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
			
			arquivo = gerarArquivoAutorizacaoCancelamento(getUsuario().getInstituicao().getLayoutPadraoXML(), dados);
			if (getUsuario().getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
				return gerarMensagemSerpro(arquivo, CONSTANTE_RELATORIO_XML);
			}
			return gerarMensagem(gerarResposta(arquivo, getUsuario()), CONSTANTE_RELATORIO_XML);
			
		} catch (AutorizacaoCancelamentoException ex) {
			return gerarMensagemErroAutorizacaoCancelamento(getUsuario().getInstituicao().getLayoutPadraoXML() , ex.getPedidosAutorizacaoCancelamento());
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private Arquivo gerarArquivoAutorizacaoCancelamento(LayoutPadraoXML layoutPadraoXML, String dados) {
		logger.info("Processando arquivo de autorizacao de cancelamento " + getNomeArquivo());
		return autorizacaoCancelamentoMediator.processarAutorizacaoCancelamento(getNomeArquivo(), layoutPadraoXML , dados, getErros(), getUsuario());
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

		for (AutorizacaoCancelamento ac: arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(ac.getCabecalhoCartorio().getCodigoMunicipio());
			mensagem.setDescricao(formatarMensagemRetorno(ac));
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
				titulo.setCodigo(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO.getCodigo());
				titulo.setOcorrencia(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO.getDescricao());
				
				mensagemAC.getTitulosDetalhamento().add(titulo);
			}
		}
		return gerarMensagem(mensagemAC, constanteRelatorioXml);
	}
	
	private String gerarMensagemErroAutorizacaoCancelamento(LayoutPadraoXML layoutPadraoXML, List<PedidoAutorizacaoCancelamento> pedidoAC) {
		if (layoutPadraoXML.equals(LayoutPadraoXML.SERPRO)) {
			return gerarMensagemErroAutorizacaoCancelamentoSerpro(pedidoAC, CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagemErroAutorizacaoCancelamento(pedidoAC, CONSTANTE_RELATORIO_XML);
	}
	
	private String gerarMensagemErroAutorizacaoCancelamento(List<PedidoAutorizacaoCancelamento> pedidosDesistenciaCancelamento, String constanteRelatorioXml) {
		return null;
	}

	private String gerarMensagemErroAutorizacaoCancelamentoSerpro(List<PedidoAutorizacaoCancelamento> pedidosAC, String constanteRelatorioXml) {
		MensagemXmlDesistenciaCancelamentoSerpro mensagemErroDesistencia = new MensagemXmlDesistenciaCancelamentoSerpro();
		mensagemErroDesistencia.setNomeArquivo(getNomeArquivo());
		mensagemErroDesistencia.setTitulosDetalhamento(new ArrayList<TituloDetalhamentoSerpro>());
		
		for (PedidoAutorizacaoCancelamento pedidoAC : pedidosAC) {
			TituloDetalhamentoSerpro titulo = new TituloDetalhamentoSerpro();
			titulo.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
			titulo.setCodigoCartorio(pedidoAC.getAutorizacaoCancelamento().getCabecalhoCartorio().getCodigoCartorio());
			titulo.setNumeroTitulo(pedidoAC.getNumeroTitulo());
			titulo.setNumeroProtocoloCartorio(pedidoAC.getNumeroProtocolo());
			titulo.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(pedidoAC.getDataProtocolagem()));
			titulo.setCodigo(CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO.getCodigo());
			titulo.setOcorrencia(CodigoErro.SERPRO_NUMERO_PROTOCOLO_INVALIDO.getDescricao());
			
			mensagemErroDesistencia.getTitulosDetalhamento().add(titulo);
		}
		return gerarMensagem(mensagemErroDesistencia, constanteRelatorioXml);
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}
