package br.com.ieptbto.cra.webservice.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoCnpVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
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
	CentralNacionalProtestoMediator centralNacionalProtestoMediator;

	/**
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processar(Usuario usuario, String dados) {
		ArquivoCnp arquivoCnp = new ArquivoCnp();
		setUsuario(usuario);

		try {
			if (getUsuario() == null) {
				return usuarioInvalido();
			}
			if (StringUtils.isBlank(dados)) {
				return dadosArquivoCnpEmBranco(usuario);
			}
			if (new LocalTime().isBefore(getHoraInicioServicoEnvio()) || new LocalTime().isAfter(getHoraFimServicoEnvio())) {
				return servicoNaoDisponivelForaDoHorarioEnvio(usuario);
			}
			logger.info("Iniciar procesaamento arquivo cnp do cartório");
			arquivoCnp = centralNacionalProtestoMediator.processarArquivoCartorio(getUsuario(), converterStringArquivoCnpVO(dados));
			loggerCra.sucess(getUsuario(), CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Arquivo da CNP enviado por " + getUsuario().getInstituicao().getNomeFantasia() + " processado com sucesso!");
		} catch (InfraException ex) {
			ex.printStackTrace();
			logger.info(ex.getMessage(), ex.getCause());
			loggerCra.error(getUsuario(), CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro no processamento do arquivo da CNP de " + getUsuario().getInstituicao().getNomeFantasia() + "!", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(ex.getMessage()), CONSTANTE_RELATORIO_XML);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.info(ex.getMessage(), ex.getCause());
			loggerCra.error(getUsuario(), CraAcao.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO,
					"Erro no processamento do arquivo da CNP de " + getUsuario().getInstituicao().getNomeFantasia() + "!", ex);
			return gerarMensagem(gerarMensagemErroProcessamento(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(gerarMensagemSucesso(arquivoCnp), CONSTANTE_RELATORIO_XML);
	}

	/**
	 * Converter a String dados para ArquivoCnpVO
	 * 
	 * @param dados
	 * @return
	 */
	private ArquivoCnpVO converterStringArquivoCnpVO(String dados) {
		JAXBContext context;
		ArquivoCnpVO arquivoCnp = null;

		try {
			context = JAXBContext.newInstance(ArquivoCnpVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", StringUtils.EMPTY);
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", StringUtils.EMPTY);
				}
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivoCnp = (ArquivoCnpVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new InfraException(e.getMessage(), e.getCause());
		}
		return arquivoCnp;
	}

	// private boolean isInstituicaoEnviouArquivoCnpHoje(Instituicao
	// instituicao) {
	// return
	// centralNacionalProtestoMediator.isInstituicaoEnviouArquivoCnpHoje(instituicao);
	// }

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
			String mensagem = "N�O CONSTAM PROTESTOS, POR FALTA DE PAGAMENTO, PARA ESTE CPF/CNPJ NOS TABELIONATOS DO ESTADO DO TOCANTINS!";
			xml.append("<protesto>");
			xml.append("	<mensagem>" + mensagem + "</mensagem>");
			xml.append("</protesto>");
		}
		return XmlFormatterUtil.format(xml.toString());
	}
}