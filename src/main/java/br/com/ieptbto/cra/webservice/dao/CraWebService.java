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
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoGenericoVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.regra.TipoArquivoFebraban;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.logger.LoggerCra;
import br.com.ieptbto.cra.mediator.CraMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.vo.DescricaoVO;
import br.com.ieptbto.cra.webservice.vo.DetalhamentoVO;
import br.com.ieptbto.cra.webservice.vo.ErroVO;
import br.com.ieptbto.cra.webservice.vo.MensagemXmlVO;

/**
 * @author Thasso Araújo
 *
 */
public class CraWebService {

	protected static final Logger logger = Logger.getLogger(CraWebService.class);
	public static final String CONSTANTE_RELATORIO_XML = "relatorio";
	public static final String CONSTANTE_COMARCA_XML = "comarcas";
	public static final String CONSTANTE_APRESENTANTE_XML = "apresentante";
	public static final String CONSTANTE_REMESSA_XML = "remessa";
	public static final String CONSTANTE_CONFIRMACAO_XML = "confirmacao";
	public static final String CONSTANTE_RETORNO_XML = "retorno";
	public static final String CONSTANTE_DESISTENCIA_XML = "desistencia";
	public static final String CONSTANTE_CANCELAMENTO_XML = "cancelamento";

	@Autowired
	protected CraMediator craServiceMediator;
	@Autowired
	protected LoggerCra loggerCra;

	protected CraAcao craAcao;
	protected String nomeArquivo;

	protected String mensagemServicoIndisponivel(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(getCraAcao().getConstante());
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_SERVICO_INDISPONIVEL.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_SERVICO_INDISPONIVEL.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String setResposta(Usuario usuario, ArquivoGenericoVO arquivo, String nomeArquivo, String nomeNode) {
		if (usuario == null) {
			return setRespostaUsuarioInvalido(nomeArquivo);
		}
		if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
			return setRespostaNomeArquivoInvalido(usuario, nomeArquivo);
		}
		return gerarMensagem(arquivo, nomeNode);
	}

	private String setRespostaUsuarioInvalido(String nomeArquivo) {
		logger.error("Erro WS : Dados do usuário inválidos. Falha na autenticação.");
		loggerCra.error(getCraAcao(), "Dados do usuário inválidos. Falha na autenticação.");
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), CodigoErro.CRA_FALHA_NA_AUTENTICACAO);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	private String setRespostaNomeArquivoInvalido(Usuario usuario, String nomeArquivo) {
		logger.error("Erro WS : Nome do arquivo " + nomeArquivo + " informado é inválido!");
		loggerCra.error(usuario, getCraAcao(), "Nome do arquivo " + nomeArquivo + " está em branco ou é inválido ao layout FEBRABAN.");
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			CodigoErro codigoErro = getCodigoErroNomeInvalidoSerpro(nomeArquivo);
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, codigoErro);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaPadrao(Usuario usuario, String nomeArquivo, CodigoErro codigoErro) {
		logger.error("Erro WS: " + nomeArquivo + " ==== " + codigoErro.getDescricao());
		loggerCra.error(usuario, getCraAcao(), codigoErro.getDescricao());
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), codigoErro);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), codigoErro);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaErrosServicosCartorios(Usuario usuario, String nomeArquivo, String descricao) {
		logger.error("Erro WS: " + nomeArquivo + " : " + descricao);
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, descricao);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(Usuario usuario, String nomeArquivo) {
		logger.error("Erro WS: Este usuário não pode enviar o arquivo desta instituição. " + nomeArquivo);
		loggerCra.error(usuario, getCraAcao(),
				"Este usuário não pode enviar o arquivo desta instituição. O Código do Portador informado no arquivo difere da instituição "
						+ usuario.getInstituicao().getNomeFantasia() + ".");
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_USUARIO_INSTITUICAO_DIFERENTE_ARQUIVO);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_USUARIO_INSTITUICAO_DIFERENTE_ARQUIVO);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaArquivoEmBranco(Usuario usuario, String nomeArquivo) {
		logger.error("Erro WS: Dados do arquivo enviados em branco " + nomeArquivo);
		loggerCra.error(usuario, getCraAcao(), "Os dados do arquivo " + nomeArquivo + " da instituição "
				+ usuario.getInstituicao().getNomeFantasia() + " foram enviados em branco ou estão corrimpidos.");
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			MensagemDeErro msg =
					new MensagemDeErro(nomeArquivo, usuario, CodigoErro.SERPRO_ARQUIVO_INVALIDO_REMESSA_DESISTENCIA_CANCELAMENTO);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_DADOS_DE_ENVIO_INVALIDOS);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaArquivoEmProcessamento(Usuario usuario, String nomeArquivo) {
		logger.error("Erro WS: O arquivo ainda não foi gerado, ou ainda está em processamento.");
		loggerCra.alert(usuario, getCraAcao(), "O arquivo " + nomeArquivo + " não foi gerado, ou ainda está em processamento.");
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			CodigoErro codigoErro = getCodigoErroEmProcessamentoSerpro(nomeArquivo);
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, codigoErro);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		CodigoErro codigoErro = getCodigoErroEmProcessamentoCra(nomeArquivo);
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, codigoErro);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaErroInternoNoProcessamento(Usuario usuario, String nomeArquivo) {
		logger.error("Erro WS: Erro interno no processamento.");
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaArquivoJaEnviadoAnteriormente(Usuario usuario, String nomeArquivo, Arquivo arquivoJaEnviado) {
		logger.error("Erro WS: Arquivo já enviado anteriormente.");
		loggerCra.alert(usuario, getCraAcao(), "Arquivo " + nomeArquivo + " já enviado anteriormente em "
				+ DataUtil.localDateToString(arquivoJaEnviado.getDataEnvio()) + ".");
		if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
		MensagemXmlVO mensagem = msg.getMensagemErro();

		DetalhamentoVO detalhamento = new DetalhamentoVO();
		List<ErroVO> erros = new ArrayList<ErroVO>();
		ErroVO erro = new ErroVO();
		erro.setCodigo("2116");
		erro.setDescricao("O arquivo " + nomeArquivo + " já foi enviado em " + DataUtil.localDateToString(arquivoJaEnviado.getDataEnvio()));
		erros.add(erro);
		detalhamento.setErro(erros);
		mensagem.setDetalhamento(detalhamento);
		return gerarMensagem(mensagem, CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaArquivoJaEnviadoCartorio(Usuario usuario, String nomeArquivo, Arquivo arquivoJaEnviado) {
		logger.error("Erro WS: Arquivo já enviado anteriormente.");
		loggerCra.error(usuario, getCraAcao(), "Arquivo " + nomeArquivo + " já enviado anteriormente em "
				+ DataUtil.localDateToString(arquivoJaEnviado.getDataEnvio()) + ".");
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
		MensagemXmlVO mensagem = msg.getMensagemErro();

		DetalhamentoVO detalhamento = new DetalhamentoVO();
		List<ErroVO> erros = new ArrayList<ErroVO>();
		ErroVO erro = new ErroVO();
		erro.setCodigo("9999");
		erro.setDescricao("O arquivo " + nomeArquivo + " já foi enviado em " + DataUtil.localDateToString(arquivoJaEnviado.getDataEnvio()));
		erros.add(erro);
		detalhamento.setErro(erros);
		mensagem.setDetalhamento(detalhamento);
		return gerarMensagem(mensagem, CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaArquivoJaEnviadoCartorio(Usuario usuario, String nomeArquivo, LocalDate dataEnvio) {
		logger.error("Erro WS: Arquivo já enviado anteriormente.");
		loggerCra.error(usuario, getCraAcao(),
				"Arquivo " + nomeArquivo + " já enviado anteriormente em " + DataUtil.localDateToString(dataEnvio) + ".");
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, usuario, CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
		MensagemXmlVO mensagem = msg.getMensagemErro();

		DetalhamentoVO detalhamento = new DetalhamentoVO();
		List<ErroVO> erros = new ArrayList<ErroVO>();
		ErroVO erro = new ErroVO();
		erro.setCodigo("9999");
		erro.setDescricao("O arquivo " + nomeArquivo + " já foi enviado em " + DataUtil.localDateToString(dataEnvio));
		erros.add(erro);
		detalhamento.setErro(erros);
		mensagem.setDetalhamento(detalhamento);
		return gerarMensagem(mensagem, CONSTANTE_RELATORIO_XML);
	}

	private CodigoErro getCodigoErroEmProcessamentoSerpro(String nomeArquivo) {
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(nomeArquivo);

		CodigoErro codigoErro = null;
		if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_AGUARDE_CONFIRMACAO_EM_PROCESSAMENTO;
		} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_NAO_HA_REGISTRO_DE_RETORNO_NESTA_DATA;
		} else {
			codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
		}
		return codigoErro;
	}

	private CodigoErro getCodigoErroEmProcessamentoCra(String nomeArquivo) {
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(nomeArquivo);

		CodigoErro codigoErro = null;
		if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.CRA_NAO_EXISTE_CONFIRMACAO_NA_DATA_INFORMADA;
		} else if (TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.CRA_NAO_EXISTE_RETORNO_NA_DATA_INFORMADA;
		} else {
			codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
		}
		return codigoErro;
	}

	private CodigoErro getCodigoErroNomeInvalidoSerpro(String nomeArquivo) {
		TipoArquivoFebraban tipoArquivo = TipoArquivoFebraban.getTipoArquivoFebraban(nomeArquivo);

		CodigoErro codigoErro = null;
		if (TipoArquivoFebraban.REMESSA.equals(tipoArquivo) || TipoArquivoFebraban.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)
				|| TipoArquivoFebraban.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_NOME_ARQUIVO_INVALIDO_REMESSA_DESISTENCIA_CANCELAMENTO;
		} else if (TipoArquivoFebraban.CONFIRMACAO.equals(tipoArquivo) || TipoArquivoFebraban.RETORNO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_NOME_ARQUIVO_INVALIDO_CONFIRMACAO_RETORNO;
		} else {
			codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
		}
		return codigoErro;
	}

	protected String gerarMensagem(Object object, String nomeNo) {
		String msg = "";
		Writer writer = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(nomeNo), Object.class, object);
			marshaller.marshal(element, writer);
			msg = writer.toString();
			msg = msg.replace(" xsi:type=\"mensagemXmlSerproVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"mensagemXmlDesistenciaCancelamentoSerproVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"mensagemXmlVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace("<confirmacao xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">", "");
			msg = msg.replace("<retorno xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">", "");
			msg = msg.replace(" xsi:type=\"instituicaoVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			writer.close();

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return msg;
	}

	public String getNomeArquivo() {
		if (nomeArquivo == null) {
			nomeArquivo = StringUtils.EMPTY;
		}
		return nomeArquivo;
	}

	public CraAcao getCraAcao() {
		if (craAcao == null) {
			craAcao = CraAcao.ACESSO_CRA;
		}
		return craAcao;
	}
}