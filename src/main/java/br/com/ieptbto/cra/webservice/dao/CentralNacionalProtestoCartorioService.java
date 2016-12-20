package br.com.ieptbto.cra.webservice.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LoteCnp;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.error.CodigoErro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CentralNacionalProtestoMediator;
import br.com.ieptbto.cra.util.XmlFormatterUtil;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class CentralNacionalProtestoCartorioService extends CnpWebService {

	@Autowired
	private CentralNacionalProtestoMediator centralNacionalProtestoMediator;

	/**
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processarLoteCnp5Anos(Usuario usuario, String dados) {
		LoteCnp lote = null;

		try {
			if (usuario == null) {
				return usuarioInvalido();
			}
			if (StringUtils.isBlank(dados)) {
				return dadosArquivoCnpEmBranco(usuario);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ENVIO_CNP_5_ANOS)) {
				return mensagemServicoIndisponivel(usuario);
			}
			if (centralNacionalProtestoMediator.instituicaoEnviouLote5anos(usuario.getInstituicao())) {
				return envio5anosSucesso(usuario);
			}
			lote = centralNacionalProtestoMediator.processarLote5anos(usuario.getInstituicao(), converterStringArquivoCnpVO(dados));
			if (lote == null) {
				loggerCra.alert(usuario, CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO, CodigoErro.CNP_LOTE_VAZIO.getDescricao());
				return mensagemLoteCnpVazioOuNenhumRegistroValido(usuario);
			}
			loggerCra.sucess(usuario, CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Arquivo da CNP enviado por " + usuario.getInstituicao().getNomeFantasia() + ", com " + lote.getRegistrosCnp().size()
							+ " títulos, processado com sucesso!");
		} catch (InfraException ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro no processamento do arquivo da CNP de " + usuario.getInstituicao().getNomeFantasia() + "!", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(ex.getMessage()), CONSTANTE_RELATORIO_XML);
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro no processamento do arquivo da CNP de " + usuario.getInstituicao().getNomeFantasia() + "!", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(gerarMensagemSucesso(usuario, lote), CONSTANTE_RELATORIO_XML);
	}

	/**
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processarLoteCnpDiario(Usuario usuario, String dados) {
		LoteCnp lote = new LoteCnp();

		try {
			if (usuario == null) {
				return usuarioInvalido();
			}
			if (StringUtils.isBlank(dados)) {
				return dadosArquivoCnpEmBranco(usuario);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO)) {
				return mensagemServicoIndisponivel(usuario);
			}
			lote = centralNacionalProtestoMediator.processarLoteDiario(usuario.getInstituicao(), converterStringArquivoCnpVO(dados));
			if (lote == null) {
				loggerCra.alert(usuario, CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO, CodigoErro.CNP_LOTE_VAZIO.getDescricao());
				return mensagemLoteCnpVazioOuNenhumRegistroValido(usuario);
			}
			loggerCra.sucess(usuario, CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Arquivo da CNP enviado por " + usuario.getInstituicao().getNomeFantasia() + ", com " + lote.getRegistrosCnp().size()
							+ " títulos, processado com sucesso!");
		} catch (InfraException ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro no processamento do arquivo da CNP de " + usuario.getInstituicao().getNomeFantasia() + "!", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(ex.getMessage()), CONSTANTE_RELATORIO_XML);
		} catch (Exception ex) {
			logger.info(ex.getMessage(), ex);
			loggerCra.error(usuario, CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro no processamento do arquivo da CNP de " + usuario.getInstituicao().getNomeFantasia() + "!", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(gerarMensagemSucesso(usuario, lote), CONSTANTE_RELATORIO_XML);
	}

	private ArquivoCnpVO converterStringArquivoCnpVO(String dados) {
		JAXBContext context;
		ArquivoCnpVO arquivoCnp = null;

		try {
			context = JAXBContext.newInstance(ArquivoCnpVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			logger.info("Início da conversão do XML...");
			if (dados.contains("<?xml version=")) {
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", StringUtils.EMPTY);
				dados = dados.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", StringUtils.EMPTY);
			}
			dados = dados.replaceAll("& ", "&amp;");

			InputStream xml = new ByteArrayInputStream(dados.getBytes());
			arquivoCnp = (ArquivoCnpVO) unmarshaller.unmarshal(new InputSource(xml));
			logger.info("Fim da conversão do XML...");

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
		}
		return arquivoCnp;
	}

	/**
	 * Consultar Protesto WS
	 * 
	 * @param dados
	 * @return
	 */
	public String consultarProtesto(String documentoDevedor) {
		if (documentoDevedor.contains(".")) {
			documentoDevedor = documentoDevedor.replace(".", "");
		}
		if (documentoDevedor.contains("/")) {
			documentoDevedor = documentoDevedor.replace("/", "");
		}
		if (documentoDevedor.contains("-")) {
			documentoDevedor = documentoDevedor.replace("-", "");
		}
		List<Instituicao> cartorios = centralNacionalProtestoMediator.consultarProtestosWs(documentoDevedor);

		StringBuffer xml = new StringBuffer();
		if (!cartorios.isEmpty()) {
			xml.append("<protesto>");
			for (Instituicao instituicao : cartorios) {
				xml.append("	<cartorio>");
				xml.append("		<municipio>" + instituicao.getMunicipio().getNomeMunicipio() + "</municipio>");
				xml.append("		<telefone>" + instituicao.getTelefone() + "</telefone>");
				xml.append("		<endereco>" + instituicao.getEndereco() + "</endereco>");
				xml.append("	</cartorio>");
			}
			xml.append("</protesto>");
		} else {
			String mensagem = "NÃO CONSTAM PROTESTOS, POR FALTA DE PAGAMENTO, PARA ESTE CPF/CNPJ NOS TABELIONATOS DO ESTADO DO TOCANTINS!";
			xml.append("<protesto>");
			xml.append("	<mensagem>" + mensagem + "</mensagem>");
			xml.append("</protesto>");
		}
		return XmlFormatterUtil.format(xml.toString());
	}
}