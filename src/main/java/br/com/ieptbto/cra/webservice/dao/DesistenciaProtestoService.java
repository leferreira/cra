package br.com.ieptbto.cra.webservice.dao;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

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
import br.com.ieptbto.cra.entidade.vo.ArquivoGenericoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaDesistenciaProtestoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServices;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.DesistenciaCancelamentoException;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DesistenciaCancelamentoMediator;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.XmlFormatterUtil;
import br.com.ieptbto.cra.webservice.vo.DescricaoVO;
import br.com.ieptbto.cra.webservice.vo.DetalhamentoVO;
import br.com.ieptbto.cra.webservice.vo.ErroVO;
import br.com.ieptbto.cra.webservice.vo.MensagemVO;
import br.com.ieptbto.cra.webservice.vo.MensagemXmlDesistenciaCancelamentoSerproVO;
import br.com.ieptbto.cra.webservice.vo.MensagemXmlVO;
import br.com.ieptbto.cra.webservice.vo.TituloDetalhamentoSerproVO;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class DesistenciaProtestoService extends CraWebService {

	@Autowired
	private DesistenciaProtestoMediator desistenciaMediator;
	@Autowired
	private DesistenciaCancelamentoMediator desistenciaCancelamentoMediator;
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	private List<Exception> erros;	
	

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

		try {
            if (usuario == null) {
                return setResposta(null, nomeArquivo);
            }
            if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
                return setResposta(usuario, nomeArquivo);
            }
            if (craServiceMediator.verificarServicoIndisponivel(CraServices.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO)) {
                return mensagemServicoIndisponivel(usuario);
            }
            if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
                return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
            }
            if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
                return setRespostaArquivoEmBranco(usuario, nomeArquivo);
            }
            Arquivo arquivoJaEnviado = desistenciaMediator.verificarDesistenciaJaEnviadaAnteriormente(nomeArquivo, usuario.getInstituicao());
            if (arquivoJaEnviado != null) {
                return setRespostaArquivoJaEnviadoAnteriormente(usuario, nomeArquivo, arquivoJaEnviado);
            }
            Arquivo arquivo = desistenciaMediator.processarDesistencia(nomeArquivo, dados, getErros(), usuario);
            if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
                return gerarMensagemSerpro(arquivo, CONSTANTE_RELATORIO_XML);
            }
            MensagemXmlVO mensagemCra = gerarResposta(arquivo, usuario);
			return gerarMensagemRelatorio(mensagemCra);

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
	}

	private MensagemXmlVO gerarResposta(Arquivo arquivo, Usuario usuario) {
		List<MensagemVO> mensagens = new ArrayList<MensagemVO>();
		List<ErroVO> errosDesistencia = new ArrayList<ErroVO>();
		MensagemXmlVO mensagemRetorno = new MensagemXmlVO();
		DescricaoVO desc = new DescricaoVO();
		DetalhamentoVO detal = new DetalhamentoVO();
		detal.setMensagem(mensagens);
		detal.setErro(errosDesistencia);

		mensagemRetorno.setDescricao(desc);
		mensagemRetorno.setDetalhamento(detal);
		mensagemRetorno.setCodigoFinal("0000");
		mensagemRetorno.setDescricaoFinal("Arquivo processado com sucesso.");

		desc.setDataEnvio(LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG));
		desc.setTipoArquivo(DescricaoVO.XML_UPLOAD_SUSTACAO);
		desc.setDataMovimento(arquivo.getDataEnvio().toString(DataUtil.PADRAO_FORMATACAO_DATA));
		desc.setPortador(arquivo.getInstituicaoEnvio().getCodigoCompensacao());
		desc.setUsuario(usuario.getNome());
		desc.setNomeArquivo(nomeArquivo);

		for (DesistenciaProtesto desistenciaProtesto : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
			MensagemVO mensagem = new MensagemVO();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(desistenciaProtesto.getCabecalhoCartorio().getCodigoMunicipio());
			mensagem.setDescricao(formatarMensagemRetorno(desistenciaProtesto));
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
		getErros().clear();
		return mensagemRetorno;
	}

	private String formatarMensagemRetorno(DesistenciaProtesto desistenciaProtesto) {
		Instituicao instituicao = instituicaoMediator.getCartorioPorCodigoIBGE(desistenciaProtesto.getCabecalhoCartorio().getCodigoMunicipio());
		String titulos = "titulo";
		if (desistenciaProtesto.getDesistencias().size() > 1) {
			titulos = "titulos";
		}
		return instituicao.getNomeFantasia() + " (" + desistenciaProtesto.getDesistencias().size() + " " + titulos.toString() + ").";
	}

	/**
	 * @param arquivo
	 * @param constanteRelatorioXml
	 * @return
	 */
	private String gerarMensagemSerpro(Arquivo arquivo, String constanteRelatorioXml) {
		MensagemXmlDesistenciaCancelamentoSerproVO mensagemDesistencia = new MensagemXmlDesistenciaCancelamentoSerproVO();
		mensagemDesistencia.setNomeArquivo(arquivo.getNomeArquivo());
		mensagemDesistencia.setTitulosDetalhamento(new ArrayList<TituloDetalhamentoSerproVO>());

		for (DesistenciaProtesto dp : arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto()) {
			for (PedidoDesistencia pedidoDesistencia : dp.getDesistencias()) {
				TituloDetalhamentoSerproVO titulo = new TituloDetalhamentoSerproVO();
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
		return gerarMensagemRelatorio(mensagemDesistencia);
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

		try {
            if (usuario == null) {
                return setResposta(null, nomeArquivo);
            }
            if (nomeArquivo.contains(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.getConstante())) {
                this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO;
            } else if (nomeArquivo.contains(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.getConstante())) {
                this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO;
            } else if (nomeArquivo.contains(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
                this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO;
            }
            if (craServiceMediator.verificarServicoIndisponivel(CraServices.DOWNLOAD_ARQUIVO_DESISTENCIA_CANCELAMENTO)) {
                return mensagemServicoIndisponivel(usuario);
            }
            RemessaDesistenciaProtestoVO remessaVO = desistenciaCancelamentoMediator.buscarDesistenciaCancelamentoCartorio(usuario.getInstituicao(), nomeArquivo);
			if (remessaVO == null) {
				return setRespostaPadrao(usuario, nomeArquivo, CodigoErro.CARTORIO_ARQUIVO_NAO_EXISTE);
			}
			return gerarResposta(remessaVO, nomeArquivo);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loggerCra.error(usuario, getCraAcao(), "Erro interno ao construir o arquivo " + nomeArquivo + "." + e.getMessage(), e);
			return setRespostaErroInternoNoProcessamento(usuario, nomeArquivo);
		}
	}

	private String gerarResposta(RemessaDesistenciaProtestoVO remessaVO, String nomeArquivo) {
		String msg = gerarRespostaArquivo(remessaVO, nomeArquivo, CONSTANTE_REMESSA_XML);
		if (nomeArquivo.contains(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.getConstante())) {
			msg = msg.replace("<remessa xsi:type=\"remessaDesistenciaProtestoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
					"<sustacao>");
			msg = msg.replace("</remessa>", "</sustacao>");
		} else if (nomeArquivo.contains(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.getConstante())) {
			msg = msg.replace("<remessa xsi:type=\"remessaDesistenciaProtestoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
					"<cancelamento>");
			msg = msg.replace("</remessa>", "</cancelamento>");
		} else if (nomeArquivo.contains(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
			msg = msg.replace("<remessa xsi:type=\"remessaDesistenciaProtestoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
					"<autoriza_cancelamento>");
			msg = msg.replace("</remessa>", "</autoriza_cancelamento>");
		}
		msg = msg.replace("<registros>", "");
		msg = msg.replace("</registros>", "");
		return XmlFormatterUtil.format(msg);
	}

	/**
	 * @param nomeArquivo
	 * @param usuario
	 * @return
	 */
	public String confirmarRecebimentoDesistenciaCancelamento(String nomeArquivo, Usuario usuario) {
		this.nomeArquivo = nomeArquivo;

		try {
			if (usuario == null) {
				return setResposta(null, nomeArquivo);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario, nomeArquivo);
			}

			if (nomeArquivo.contains(TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_DESISTENCIA_PROTESTO;
			} else if (nomeArquivo.contains(TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_CANCELAMENTO_PROTESTO;
			} else if (nomeArquivo.contains(TipoArquivoFebraban.AUTORIZACAO_DE_CANCELAMENTO.getConstante())) {
				this.craAcao = CraAcao.DOWNLOAD_ARQUIVO_AUTORIZACAO_CANCELAMENTO;
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			desistenciaCancelamentoMediator.confirmarRecebimentoDesistenciaCancelamento(usuario.getInstituicao(), nomeArquivo);
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
		xml = xml.concat("<relatorio>");
		xml = xml.concat("	<descricao>");
		xml = xml.concat("		<dataEnvio>" + LocalDateTime.now().toString(DataUtil.PADRAO_FORMATACAO_DATAHORASEG) + "</dataEnvio>");
		xml = xml.concat("		<tipoArquivo>XML_DOWNLOAD_DESISTENCIA_CANCELAMENTO</tipoArquivo>");
		xml = xml.concat("		<nomeArquivo>" + nomeArquivo + "</nomeArquivo>");
		xml = xml.concat("		<dataMovimento>" + DataUtil.localDateToString(new LocalDate()) + "</dataMovimento>");
		xml = xml.concat("		<usuario>" + user.getLogin() + "</usuario>");
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

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}
	
	
	private List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}