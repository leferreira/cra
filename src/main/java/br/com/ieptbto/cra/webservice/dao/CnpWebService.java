package br.com.ieptbto.cra.webservice.dao;

import br.com.ieptbto.cra.entidade.LoteCnp;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.logger.LoggerCra;
import br.com.ieptbto.cra.mediator.CraMediator;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webservice.vo.DescricaoVO;
import br.com.ieptbto.cra.webservice.vo.DetalhamentoVO;
import br.com.ieptbto.cra.webservice.vo.MensagemVO;
import br.com.ieptbto.cra.webservice.vo.MensagemXmlVO;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * @author Thasso Araújo
 *
 */
public class CnpWebService {

	protected static final Logger logger = Logger.getLogger(CnpWebService.class);
	public static final String CONSTANTE_RELATORIO_XML = "relatorio";
	public static final String CONSTANTE_CNP_XML = "cnp";
	public static final String TIPO_ARQUIVO_CNP = "CENTRAL NACIONAL DE PROTESTO";
	public static final String HORA_INICIO_CONSULTA = "07:59:59";
	public static final String HORA_FIM_CONSULTA = "12:00:01";
	public static final String HORA_INICIO_ENVIO = "00:00:01";
	public static final String HORA_FIM_ENVIO = "23:59:59";

	@Autowired
	protected CraMediator craServiceMediator;
	@Autowired
	protected LoggerCra loggerCra;
	protected LocalTime horaInicioServico;
	protected LocalTime horaFimServico;

	protected String mensagemServicoIndisponivel(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_SERVICO_INDISPONIVEL.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_SERVICO_INDISPONIVEL.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String mensagemDataInvalida(Usuario usuario) {
		logger.error("Data informada para CNP na consulta do movimento é inválida");
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_DATA_MOVIMENTO_INVALIDA.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_DATA_MOVIMENTO_INVALIDA.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String mensagemSemMovimentoNessaData(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_NAO_HA_MOVIMENTO_NESSA_DATA.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_NAO_HA_MOVIMENTO_NESSA_DATA.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String usuarioInvalido() {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();

		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_FALHA_NA_AUTENTICACAO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_FALHA_NA_AUTENTICACAO.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String usuarioNaoPermitidoConsultaArquivoCNP() {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();

		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_USUARIO_NAO_PERMITIDO_CONSULTA.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_USUARIO_NAO_PERMITIDO_CONSULTA.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String dadosArquivoCnpEmBranco(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_DADOS_DE_ENVIO_INVALIDOS.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_DADOS_DE_ENVIO_INVALIDOS.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String servicoNaoDisponivelForaDoHorarioEnvio(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_ENVIO_FORA_DO_HORARIO_LIMITE_DO_SERVICO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_ENVIO_FORA_DO_HORARIO_LIMITE_DO_SERVICO.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String mensagemLoteCnpVazioOuNenhumRegistroValido(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_LOTE_VAZIO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_LOTE_VAZIO.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String servicoNaoDisponivelForaDoHorarioConsulta(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_CONSULTA_FORA_DO_HORARIO_LIMITE_DO_SERVICO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_CONSULTA_FORA_DO_HORARIO_LIMITE_DO_SERVICO.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String naoHaRemessasParaArquivoCnp(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_NAO_HA_ARQUIVOS_DISPONIVEIS.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_NAO_HA_ARQUIVOS_DISPONIVEIS.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String arquivoCnpJaEnviadoHoje(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_ARQUIVO_CNP_JA_ENVIADO_HOJE.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_ARQUIVO_CNP_JA_ENVIADO_HOJE.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String envio5anosSucesso(Usuario usuario) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_SUCESSO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_SUCESSO.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
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
			msg = msg.replace("xsi:type=\"arquivoCnpVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"mensagemXml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			writer.close();
			return msg;

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}

	protected MensagemXmlVO gerarMensagemSucesso(Usuario usuario, LoteCnp loteCnp) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());

		DetalhamentoVO detalhamento = new DetalhamentoVO();
		detalhamento.setMensagem(new ArrayList<MensagemVO>());
		MensagemVO mensagem = new MensagemVO();
		mensagem.setDescricao("Foram enviados " + loteCnp.getRegistrosCnp().size() + " registros para CNP.");
		detalhamento.getMensagem().add(mensagem);

		mensagemXml.setDetalhamento(detalhamento);
		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_SUCESSO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_SUCESSO.getDescricao());
		return mensagemXml;
	}

	protected MensagemXmlVO gerarMensagemErroProcessamento() {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao());
		return mensagemXml;
	}
	
	protected MensagemXmlVO gerarMensagemErroProcessamento(String descricaoFinal, LocalDate dataMovimento) {
		MensagemXmlVO mensagemXml = new MensagemXmlVO();
		DescricaoVO descricao = new DescricaoVO();
		descricao.setDataEnvio(DataUtil.localDateToString(dataMovimento));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);

		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal("9999");
		mensagemXml.setDescricaoFinal(descricaoFinal);
		return mensagemXml;
	}

	public LocalTime getHoraInicioServicoEnvio() {
		return new LocalTime(HORA_INICIO_ENVIO);
	}

	public LocalTime getHoraFimServicoEnvio() {
		return new LocalTime(HORA_FIM_ENVIO);
	}

	public LocalTime getHoraInicioServicoConsulta() {
		return new LocalTime(HORA_INICIO_CONSULTA);
	}

	public LocalTime getHoraFimServicoConsulta() {
		return new LocalTime(HORA_FIM_CONSULTA);
	}
}