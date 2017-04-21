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
public class AutorizacaoCancelamentoService extends CraWebService {

	@Autowired
	private AutorizacaoCancelamentoMediator autorizacaoCancelamentoMediator;
	@Autowired
	private InstituicaoMediator instituicaoMediator;
	private List<Exception> erros;

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
		try {
            if (usuario == null) {
                return setResposta(null, nomeArquivo);
            }
            if (nomeArquivo == null || StringUtils.isBlank(nomeArquivo)) {
                return setResposta(usuario, nomeArquivo);
            }
			if (craServiceMediator.verificarServicoIndisponivel(CraServices.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (!nomeArquivo.contains(usuario.getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario, nomeArquivo);
			}
			if (dados == null || StringUtils.isBlank(dados)) {
				return setRespostaArquivoEmBranco(usuario, nomeArquivo);
			}
			arquivo = autorizacaoCancelamentoMediator.processarAutorizacaoCancelamento(nomeArquivo, dados, getErros(), usuario);
			if (usuario.getInstituicao().getLayoutPadraoXML().equals(LayoutPadraoXML.SERPRO)) {
				return gerarMensagemSerpro(arquivo, CONSTANTE_RELATORIO_XML);
			}
			MensagemXmlVO relatorio = gerarResposta(arquivo, usuario);
			return gerarMensagemRelatorio(relatorio);

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

		for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
			MensagemVO mensagem = new MensagemVO();
			mensagem.setCodigo("0000");
			mensagem.setMunicipio(ac.getCabecalhoCartorio().getCodigoMunicipio());
			mensagem.setDescricao(formatarMensagemRetorno(ac));
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

	private String formatarMensagemRetorno(AutorizacaoCancelamento ac) {
		Instituicao instituicao = instituicaoMediator.getCartorioPorCodigoIBGE(ac.getCabecalhoCartorio().getCodigoMunicipio());
		return instituicao.getNomeFantasia() + " (" + ac.getAutorizacoesCancelamentos().size() + ") ";
	}

	private String gerarMensagemSerpro(Arquivo arquivo, String constanteRelatorioXml) {
		MensagemXmlDesistenciaCancelamentoSerproVO mensagemAC = new MensagemXmlDesistenciaCancelamentoSerproVO();
		mensagemAC.setNomeArquivo(arquivo.getNomeArquivo());
		mensagemAC.setTitulosDetalhamento(new ArrayList<TituloDetalhamentoSerproVO>());

		for (AutorizacaoCancelamento ac : arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento()) {
			for (PedidoAutorizacaoCancelamento pedidoAC : ac.getAutorizacoesCancelamentos()) {
				TituloDetalhamentoSerproVO titulo = new TituloDetalhamentoSerproVO();
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
		return gerarMensagemRelatorio(mensagemAC);
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

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}

	@Override
	protected String gerarRespostaArquivo(Object object, String nomeArquivo, String nameNode) {
		return null;
	}
	
	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}
}