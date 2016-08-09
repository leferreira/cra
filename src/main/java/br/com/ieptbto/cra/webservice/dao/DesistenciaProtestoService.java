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
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaDesistenciaProtestoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.DesistenciaCancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.XmlFormatterUtil;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Erro;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemCra;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;
import br.com.ieptbto.cra.webservice.VO.MensagemXmlDesistenciaCancelamentoSerpro;
import br.com.ieptbto.cra.webservice.VO.TituloDetalhamentoSerpro;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class DesistenciaProtestoService extends CraWebService {

	@Autowired
	private DesistenciaProtestoMediator desistenciaProtestoMediator;
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	private List<Exception> erros;
	private MensagemCra mensagemCra;
	private String resposta;

	/**
	 * Recebimento de arquivos de desistência das instituições
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		this.craAcao = CraAcao.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO;
		this.nomeArquivo = nomeArquivo;
		this.mensagemCra = null;

		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			if (usuario == null) {
				return setResposta(usuario, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}
			Arquivo arquivoJaEnviado = desistenciaProtestoMediator.verificarDesistenciaJaEnviadaAnteriormente(nomeArquivo, usuario.getInstituicao());
			if (arquivoJaEnviado != null) {
				return setRespostaArquivoJaEnviadoAnteriormente(usuario, nomeArquivo, arquivoJaEnviado);
			}

			Arquivo arquivo = desistenciaProtestoMediator.processarDesistencia(nomeArquivo, dados, getErros(), usuario);
			if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
				return gerarMensagemSerpro(arquivo, CONSTANTE_RELATORIO_XML);
			}
			mensagemCra = gerarResposta(arquivo, usuario);
			loggerCra.sucess(usuario, getCraAcao(), "O arquivo de Desistência de Protesto " + nomeArquivo + ", enviado por "
					+ usuario.getInstituicao().getNomeFantasia() + ", foi processado com sucesso.");

		} catch (InfraException ex) {
			logger.error(ex.getMessage(), ex);
			loggerCra.error(usuario, getCraAcao(), ex.getMessage(), ex);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(),
					"Erro interno no processamento do arquivo de Desistência de Protesto " + nomeArquivo + "." + e.getMessage(), e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return gerarMensagem(mensagemCra, CONSTANTE_RELATORIO_XML);
	}

	private MensagemXml gerarResposta(Arquivo arquivo, Usuario usuario) {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		List<Erro> errosDesistencia = new ArrayList<Erro>();
		MensagemXml mensagemRetorno = new MensagemXml();
		Descricao desc = new Descricao();
		Detalhamento detal = new Detalhamento();
		detal.setMensagem(mensagens);
		detal.setErro(errosDesistencia);

		mensagemRetorno.setDescricao(desc);
		mensagemRetorno.setDetalhamento(detal);
		mensagemRetorno.setCodigoFinal("0000");
		mensagemRetorno.setDescricaoFinal("Arquivo processado com sucesso.");

		desc.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		desc.setTipoArquivo(Descricao.XML_UPLOAD_SUSTACAO);
		desc.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		desc.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		desc.setUsuario(usuario.getNome());
		desc.setNomeArquivo(nomeArquivo);

		for (DesistenciaProtesto desistenciaProtesto : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
			Mensagem mensagem = new Mensagem();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(getMunicipio(desistenciaProtesto));
			mensagem.setDescricao(formatarMensagemRetorno(desistenciaProtesto));
			mensagens.add(mensagem);
		}

		for (Exception ex : getErros()) {
			DesistenciaCancelamentoException exception = DesistenciaCancelamentoException.class.cast(ex);
			Mensagem erro = new Mensagem();
			erro.setDescricao(exception.getDescricao());
			erro.setMunicipio(exception.getCodigoMunicipio());
			erro.setCodigo(exception.getCodigoErro().getCodigo());
			mensagens.add(erro);
			loggerCra.alert(usuario, getCraAcao(), "Comarca Rejeitada: " + exception.getCodigoMunicipio() + " - " + exception.getDescricao());
		}
		getErros().clear();
		return mensagemRetorno;
	}

	private String getMunicipio(DesistenciaProtesto desistenciaProtesto) {
		return desistenciaProtesto.getCabecalhoCartorio().getCodigoMunicipio();
	}

	private String formatarMensagemRetorno(DesistenciaProtesto desistenciaProtesto) {
		Instituicao instituicao = instituicaoMediator.getCartorioPorCodigoIBGE(desistenciaProtesto.getCabecalhoCartorio().getCodigoMunicipio());
		String titulos = "titulo";
		if (desistenciaProtesto.getDesistencias().size() > 1) {
			titulos = "titulos";
		}
		return instituicao.getNomeFantasia() + " (" + desistenciaProtesto.getDesistencias().size() + " " + titulos.toString() + ").";
	}

	private String gerarMensagemSerpro(Arquivo arquivo, String constanteRelatorioXml) {
		MensagemXmlDesistenciaCancelamentoSerpro mensagemDesistencia = new MensagemXmlDesistenciaCancelamentoSerpro();
		mensagemDesistencia.setNomeArquivo(arquivo.getNomeArquivo());
		mensagemDesistencia.setTitulosDetalhamento(new ArrayList<TituloDetalhamentoSerpro>());

		for (DesistenciaProtesto dp : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
			for (PedidoDesistencia pedidoDesistencia : dp.getDesistencias()) {
				TituloDetalhamentoSerpro titulo = new TituloDetalhamentoSerpro();
				titulo.setDataHora(DataUtil.localDateToStringddMMyyyy(new LocalDate()) + DataUtil.localTimeToStringMMmm(new LocalTime()));
				titulo.setCodigoCartorio(pedidoDesistencia.getDesistenciaProtesto().getCabecalhoCartorio().getCodigoCartorio());
				titulo.setNumeroTitulo(pedidoDesistencia.getNumeroTitulo());
				titulo.setNumeroProtocoloCartorio(pedidoDesistencia.getNumeroProtocolo());
				titulo.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(pedidoDesistencia.getDataProtocolagem()));
				titulo.setCodigo(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO.getCodigo());
				titulo.setOcorrencia(CodigoErro.SERPRO_SUCESSO_DESISTENCIA_CANCELAMENTO.getDescricao());

				mensagemDesistencia.getTitulosDetalhamento().add(titulo);
			}
		}
		return gerarMensagem(mensagemDesistencia, constanteRelatorioXml);
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}

	/**
	 * Consulta de desistências, cancelamentos e ac pelos cartórios
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @return
	 */
	public String buscarDesistenciaCancelamento(String nomeArquivo, Usuario usuario) {
		this.nomeArquivo = nomeArquivo;
		this.resposta = null;

		RemessaDesistenciaProtestoVO remessaVO = null;
		try {
			if (nomeArquivo.contains(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO;
			} else if (nomeArquivo.contains(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO;
			} else if (nomeArquivo.contains(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO;
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.DOWNLOAD_ARQUIVO_DESISTENCIA_CANCELAMENTO)) {
				return mensagemServicoIndisponivel(usuario);
			}

			remessaVO = desistenciaProtestoMediator.buscarDesistenciaCancelamentoCartorio(usuario.getInstituicao(), nomeArquivo);
			if (remessaVO == null) {
				return setRespostaPadrao(usuario, nomeArquivo, CodigoErro.CARTORIO_ARQUIVO_NAO_EXISTE);
			}
			resposta = gerarResposta(remessaVO, nomeArquivo);
			loggerCra.sucess(usuario, getCraAcao(),
					"Arquivo " + nomeArquivo + ", recebido com sucesso por " + usuario.getInstituicao().getNomeFantasia() + ".");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(), "Erro interno ao construir o arquivo " + nomeArquivo + "." + e.getMessage(), e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return resposta;
	}

	private String gerarResposta(RemessaDesistenciaProtestoVO remessaVO, String nomeArquivo) {
		String msg = gerarMensagem(remessaVO, CONSTANTE_REMESSA_XML);

		if (nomeArquivo.contains(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.getConstante())) {
			msg = msg.replace("<remessa xsi:type=\"remessaDesistenciaProtestoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
					"<sustacao>");
			msg = msg.replace("</remessa>", "</sustacao>");
		} else if (nomeArquivo.contains(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.getConstante())) {
			msg = msg.replace("<remessa xsi:type=\"remessaDesistenciaProtestoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
					"<cancelamento>");
			msg = msg.replace("</remessa>", "</cancelamento>");
		} else if (nomeArquivo.contains(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
			msg = msg.replace("<remessa xsi:type=\"remessaDesistenciaProtestoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
					"<autoriza_cancelamento>");
			msg = msg.replace("</remessa>", "</autoriza_cancelamento>");
		}
		msg = msg.replace("<registros>", "");
		msg = msg.replace("</registros>", "");
		return XmlFormatterUtil.format(msg);
	}

	public String confirmarRecebimentoDesistenciaCancelamento(String nomeArquivo, Usuario usuario) {
		this.nomeArquivo = nomeArquivo;

		try {
			if (usuario == null) {
				return setResposta(usuario, new ArquivoVO(), nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario, new ArquivoVO(), nomeArquivo, CONSTANTE_RELATORIO_XML);
			}

			if (nomeArquivo.contains(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO;
			} else if (nomeArquivo.contains(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO;
			} else if (nomeArquivo.contains(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO;
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			desistenciaProtestoMediator.confirmarRecebimentoDesistenciaCancelamento(usuario.getInstituicao(), nomeArquivo);
			loggerCra.sucess(usuario, getCraAcao(),
					"Arquivo " + nomeArquivo + " foi confirmado o recebimento pelo " + usuario.getInstituicao().getNomeFantasia() + ".");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(), "Erro ao confirmar o recebimento do arquivo " + nomeArquivo + "." + e.getMessage(), e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
		return gerarMensagemSucessoProcessamentoCartorio(nomeArquivo, usuario);
	}

	private String gerarMensagemSucessoProcessamentoCartorio(String nomeArquivo, Usuario user) {
		String xml = StringUtils.EMPTY;

		xml = xml.concat("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\" ?>");
		xml = xml.concat("<relatorio xsi:type=\"mensagemXml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		xml = xml.concat("	<descricao>");
		xml = xml.concat("		<dataEnvio>" + LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG) + "</dataEnvio>");
		xml = xml.concat("		<tipoArquivo>XML_DOWNLOAD_DESISTENCIA_CANCELAMENTO</tipoArquivo>");
		xml = xml.concat("		<nomeArquivo>" + nomeArquivo + "</nomeArquivo>");
		xml = xml.concat("		<dataMovimento>" + DataUtil.localDateToString(new LocalDate()) + "</dataMovimento>");
		xml = xml.concat("		<usuario>" + user.getNome() + "</usuario>");
		xml = xml.concat("	</descricao>");
		xml = xml.concat("	<detalhamento>");
		xml = xml.concat("		<mensagem codigo=\"" + CodigoErro.CARTORIO_RECEBIMENTO_DESISTENCIA_CANCELAMENTO_COM_SUCESSO.getCodigo()
				+ "\" descricao=\"" + CodigoErro.CARTORIO_RECEBIMENTO_DESISTENCIA_CANCELAMENTO_COM_SUCESSO.getDescricao() + "\"/>");
		xml = xml.concat("	</detalhamento>");
		xml = xml.concat("	<final>0000</final>");
		xml = xml.concat("	<descricao_final>Arquivo de Desistência/Cancelamento confirmado o recebimento com sucesso</descricao_final>");
		xml = xml.concat("</relatorio>");
		return XmlFormatterUtil.format(xml);
	}
}