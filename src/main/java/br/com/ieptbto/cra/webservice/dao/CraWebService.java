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
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.TipoAcaoLog;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.logger.LoggerCra;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Erro;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;

/**
 * 
 * @author Lefer
 *
 */
public class CraWebService {

    protected static final Logger logger = Logger.getLogger(CraWebService.class);
    public static final String CONSTANTE_RELATORIO_XML = "relatorio";
    public static final String CONSTANTE_COMARCA_XML = "comarcas";
    public static final String CONSTANTE_REMESSA_XML = "remessa";
    public static final String CONSTANTE_CONFIRMACAO_XML = "confirmacao";
    public static final String CONSTANTE_RETORNO_XML = "retorno";
    public static final String CONSTANTE_DESISTENCIA_XML = "desistencia";
    public static final String CONSTANTE_CANCELAMENTO_XML = "cancelamento";

    @Autowired
    protected LoggerCra loggerCra;
    protected Usuario usuario;
    protected String nomeArquivo;
    protected String mensagem;
    protected TipoAcaoLog tipoAcaoLog;
    protected MensagemXml mensagemXml;
    protected Object objectMensagemXml;

    protected String setResposta(LayoutPadraoXML layoutPadraoResposta, ArquivoVO arquivo, String nomeArquivo, String nomeNode) {
	if (getUsuario().getId() == 0) {
	    return setRespostaUsuarioInvalido(layoutPadraoResposta, nomeArquivo);
	}
	if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
	    return setRespostaNomeArquivoInvalido(layoutPadraoResposta, nomeArquivo);
	}
	return gerarMensagem(arquivo, nomeNode);
    }

    private String setRespostaUsuarioInvalido(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo) {
	logger.error("Erro WS : Dados do usuário inválidos. Falha na autenticação.");
	loggerCra.error(getTipoAcaoLog(), "Dados do usuário inválidos. Falha na autenticação.");
	if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
	    MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.SERPRO_FALHA_NA_AUTENTICACAO);
	    return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
	}
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), CodigoErro.CRA_FALHA_NA_AUTENTICACAO);
	return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
    }

    private String setRespostaNomeArquivoInvalido(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo) {
	logger.error("Erro WS : Nome do arquivo informado é inválido!");
	loggerCra.error(getUsuario(), getTipoAcaoLog(), "Nome do arquivo " + nomeArquivo
		+ " está em branco ou é inválido ao layout FEBRABAN.");
	if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
	    CodigoErro codigoErro = getCodigoErroNomeInvalidoSerpro(nomeArquivo);
	    MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), codigoErro);
	    return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
	}
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO);
	return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
    }

    protected String setRespostaPadrao(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo, CodigoErro codigoErro) {
	logger.error("Erro WS: " + codigoErro.getDescricao());
	loggerCra.error(getUsuario(), getTipoAcaoLog(), codigoErro.getDescricao());
	if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
	    MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), codigoErro);
	    return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
	}
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), codigoErro);
	return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
    }

    protected String setRespostaErrosServicosCartorios(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo, String descricao) {
	logger.error("Erro WS: " + descricao);
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), descricao);
	return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
    }

    protected String setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo) {
	logger.error("Erro WS: Este usuário não pode enviar o arquivo desta instituição");
	loggerCra.error(getUsuario(), getTipoAcaoLog(), "Este usuário não pode enviar o arquivo desta instituição. O Código do Portador informado no arquivo difere da instituição "
		+ getUsuario().getInstituicao().getNomeFantasia() + ".");
	if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
	    MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_USUARIO_INSTITUICAO_DIFERENTE_ARQUIVO);
	    return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
	}
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_USUARIO_INSTITUICAO_DIFERENTE_ARQUIVO);
	return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
    }

    protected String setRespostaArquivoEmBranco(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo) {
	logger.error("Erro WS: Dados do arquivo enviados em branco");
	loggerCra.error(getUsuario(), getTipoAcaoLog(), "Os dados do arquivo " + nomeArquivo + " da instituição "
		+ getUsuario().getInstituicao().getNomeFantasia() + " foram enviados em branco ou estão corrimpidos.");
	if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
	    MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.SERPRO_ARQUIVO_INVALIDO_REMESSA_DESISTENCIA_CANCELAMENTO);
	    return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
	}
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_DADOS_DE_ENVIO_INVALIDOS);
	return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
    }

    protected String setRespostaArquivoEmProcessamento(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo) {
	logger.error("Erro WS: O arquivo ainda não foi gerado, ou ainda está em processamento.");
	loggerCra.alert(getUsuario(), getTipoAcaoLog(), "O arquivo " + nomeArquivo
		+ " não foi gerado, ou ainda está em processamento.");
	if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
	    CodigoErro codigoErro = getCodigoErroEmProcessamentoSerpro(nomeArquivo);
	    MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), codigoErro);
	    return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
	}
	CodigoErro codigoErro = getCodigoErroEmProcessamentoCra(nomeArquivo);
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), codigoErro);
	return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
    }

    protected String setRespostaErroInternoNoProcessamento(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo) {
	logger.error("Erro WS: Erro interno no processamento.");
	if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
	    MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
	    return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
	}
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
	return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
    }

    protected String setRespostaArquivoJaEnviadoAnteriormente(LayoutPadraoXML layoutPadraoResposta, String nomeArquivo, Arquivo arquivoJaEnviado) {
	logger.error("Erro WS: Arquivo já enviado anteriormente.");
	loggerCra.alert(getUsuario(), getTipoAcaoLog(), "Arquivo " + nomeArquivo + " já enviado anteriormente em "
		+ DataUtil.localDateToString(arquivoJaEnviado.getDataEnvio()) + ".");
	if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
	    MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
	    return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
	}
	MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO);
	MensagemXml mensagem = msg.getMensagemErro();

	Detalhamento detalhamento = new Detalhamento();
	List<Erro> erros = new ArrayList<Erro>();
	Erro erro = new Erro();
	erro.setCodigo("2116");
	erro.setDescricao("O arquivo " + nomeArquivo + " já foi enviado em "
		+ DataUtil.localDateToString(arquivoJaEnviado.getDataEnvio()));
	erros.add(erro);
	detalhamento.setErro(erros);
	mensagem.setDetalhamento(detalhamento);
	return gerarMensagem(mensagem, CONSTANTE_RELATORIO_XML);
    }

    private CodigoErro getCodigoErroEmProcessamentoSerpro(String nomeArquivo) {
	TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);

	CodigoErro codigoErro = null;
	if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
	    codigoErro = CodigoErro.SERPRO_AGUARDE_CONFIRMACAO_EM_PROCESSAMENTO;
	} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
	    codigoErro = CodigoErro.SERPRO_NAO_HA_REGISTRO_DE_RETORNO_NESTA_DATA;
	} else {
	    codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
	}
	return codigoErro;
    }

    private CodigoErro getCodigoErroEmProcessamentoCra(String nomeArquivo) {
	TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);

	CodigoErro codigoErro = null;
	if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
	    codigoErro = CodigoErro.CRA_NAO_EXISTE_CONFIRMACAO_NA_DATA_INFORMADA;
	} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
	    codigoErro = CodigoErro.CRA_NAO_EXISTE_RETORNO_NA_DATA_INFORMADA;
	} else {
	    codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
	}
	return codigoErro;
    }

    private CodigoErro getCodigoErroNomeInvalidoSerpro(String nomeArquivo) {
	TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);

	CodigoErro codigoErro = null;
	if (TipoArquivoEnum.REMESSA.equals(tipoArquivo) || TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo)
		|| TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
	    codigoErro = CodigoErro.SERPRO_NOME_ARQUIVO_INVALIDO_REMESSA_DESISTENCIA_CANCELAMENTO;
	} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo) || TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
	    codigoErro = CodigoErro.SERPRO_NOME_ARQUIVO_INVALIDO_CONFIRMACAO_RETORNO;
	} else {
	    codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
	}
	return codigoErro;
    }

    protected String gerarMensagem(Object mensagem, String nomeNo) {
	Writer writer = new StringWriter();
	JAXBContext context;
	try {
	    context = JAXBContext.newInstance(mensagem.getClass());

	    Marshaller marshaller = context.createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
	    JAXBElement<Object> element = new JAXBElement<Object>(new QName(nomeNo), Object.class, mensagem);
	    marshaller.marshal(element, writer);
	    String msg = writer.toString();
	    msg = msg.replace(" xsi:type=\"mensagemXmlSerpro\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
	    msg = msg.replace(" xsi:type=\"mensagemXmlDesistenciaCancelamentoSerpro\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
	    msg = msg.replace(" xsi:type=\"mensagemXml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
	    writer.close();
	    setMensagem(msg);

	} catch (JAXBException e) {
	    logger.error(e.getMessage(), e.getCause());
	    new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
	} catch (IOException e) {
	    logger.error(e.getMessage(), e.getCause());
	    new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
	}
	return getMensagem();
    }

    public Usuario getUsuario() {
	if (usuario == null) {
	    usuario = new Usuario();
	}
	return usuario;
    }

    public String getNomeArquivo() {
	return nomeArquivo;
    }

    public void setMensagem(String mensagem) {
	this.mensagem = mensagem;
    }

    public String getMensagem() {
	if (mensagem == null) {
	    mensagem = StringUtils.EMPTY;
	}
	return mensagem;
    }

    public void setObjectMensagemXml(Object objectMensagemRetorno) {
	this.objectMensagemXml = objectMensagemRetorno;
    }

    public Object getObjectMensagemXml() {
	if (objectMensagemXml == null) {
	    objectMensagemXml = new MensagemXml();
	}
	return objectMensagemXml;
    }

    public void setMensagemXml(MensagemXml mensagemXml) {
	this.mensagemXml = mensagemXml;
    }

    public MensagemXml getMensagemXml() {
	if (mensagemXml == null) {
	    mensagemXml = new MensagemXml();
	}
	return mensagemXml;
    }

    public void setTipoAcaoLog(TipoAcaoLog tipoAcaoLog) {
	this.tipoAcaoLog = tipoAcaoLog;
    }

    public TipoAcaoLog getTipoAcaoLog() {
	if (tipoAcaoLog == null) {
	    tipoAcaoLog = TipoAcaoLog.ACESSO_CRA;
	}
	return tipoAcaoLog;
    }

    public void setUsuario(Usuario usuario) {
	this.usuario = usuario;
    }

    public void setNomeArquivo(String nomeArquivo) {
	this.nomeArquivo = nomeArquivo;
    }
}
