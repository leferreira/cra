package br.com.ieptbto.cra.webservice.dao;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.RemessaCnp;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;
import br.com.ieptbto.cra.webservice.VO.Descricao;
import br.com.ieptbto.cra.webservice.VO.Detalhamento;
import br.com.ieptbto.cra.webservice.VO.Mensagem;
import br.com.ieptbto.cra.webservice.VO.MensagemXml;


/**
 * @author Thasso Araújo
 *
 */
public class CnpWebService {

	protected static final Logger logger = Logger.getLogger(CnpWebService.class);
	public static final String CONSTANTE_RELATORIO_XML = "relatorio";
	public static final String TIPO_ARQUIVO_CNP= "CENTRAL NACIONAL DE PROTESTO";
	protected Usuario usuario;
	protected LocalTime horaInicioServico;
	protected LocalTime horaFimServico;
	
	protected String usuarioInvalido() {
		MensagemXml mensagemXml = new MensagemXml();
		
		Descricao descricao = new Descricao();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		
		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_FALHA_NA_AUTENTICACAO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_FALHA_NA_AUTENTICACAO.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}
	
	protected String dadosArquivoCnpEmBranco(Usuario usuario) {
		MensagemXml mensagemXml = new MensagemXml();
		Descricao descricao = new Descricao();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());
		
		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_DADOS_DE_ENVIO_INVALIDOS.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_DADOS_DE_ENVIO_INVALIDOS.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}

	protected String servicoNaoDisponivelForaDoHorario(Usuario usuario) {
		MensagemXml mensagemXml = new MensagemXml();
		Descricao descricao = new Descricao();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());
		
		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_ENVIO_FORA_DO_HORARIO_LIMITE_DO_SERVICO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_ENVIO_FORA_DO_HORARIO_LIMITE_DO_SERVICO.getDescricao());
		return gerarMensagem(mensagemXml, CONSTANTE_RELATORIO_XML);
	}
	
	protected String arquivoCnpJaEnviadoHoje(Usuario usuario) {
		MensagemXml mensagemXml = new MensagemXml();
		Descricao descricao = new Descricao();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(usuario.getLogin());
		
		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_ARQUIVO_CNP_JA_ENVIADO_HOJE.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_ARQUIVO_CNP_JA_ENVIADO_HOJE.getDescricao());
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
	
	protected MensagemXml gerarMensagemSucesso(ArquivoCnp arquivoCNP) {
		MensagemXml mensagemXml = new MensagemXml();
		Descricao descricao = new Descricao();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(getUsuario().getLogin());
		
		Detalhamento detalhamento = new Detalhamento();
		detalhamento.setMensagem(new ArrayList<Mensagem>());
		Mensagem mensagem = new Mensagem();
		
		for (RemessaCnp remessaCnp : arquivoCNP.getRemessaCnp()) {
			mensagem.setMunicipio(getUsuario().getInstituicao().getMunicipio().getCodigoIBGE());
			mensagem.setDescricao("Foram enviados " + remessaCnp.getTitulos().size() + " títulos para CNP.");
		}
		mensagemXml.setDetalhamento(detalhamento);
		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CNP_SUCESSO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CNP_SUCESSO.getDescricao());
		return null;
	}
	
	protected MensagemXml gerarMensagemErroProcessamento() {
		MensagemXml mensagemXml = new MensagemXml();
		Descricao descricao = new Descricao();
		descricao.setDataEnvio(DataUtil.localDateToString(new LocalDate()));
		descricao.setTipoArquivo(TIPO_ARQUIVO_CNP);
		descricao.setUsuario(getUsuario().getLogin());
		
		mensagemXml.setDescricao(descricao);
		mensagemXml.setCodigoFinal(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getCodigo());
		mensagemXml.setDescricaoFinal(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao());
		return mensagemXml;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}
	
	public LocalTime getHoraInicioServico() {
		return new LocalTime("14:00");
	}
	
	public LocalTime getHoraFimServico() {
		return new LocalTime("17:00");
	}
}
