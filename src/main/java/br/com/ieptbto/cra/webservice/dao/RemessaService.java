package br.com.ieptbto.cra.webservice.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import br.com.ieptbto.cra.conversor.ConversorArquivoVO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.CraAcao;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RemessaService extends CraWebService {

	@Autowired
	RemessaMediator remessaMediator;
	@Autowired
	ArquivoMediator arquivoMediator;

	private String arquivoRecebido;
	private List<RemessaVO> remessas;
	private ArquivoVO arquivoVO;

	/**
	 * Envio de remessas pelos bancos/convênios
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		setCraAcao(CraAcao.ENVIO_ARQUIVO_REMESSA);
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);

		ArquivoVO arquivoVO = new ArquivoVO();
		try {
			if (getUsuario().getId() == 0) {
				return setResposta(LayoutPadraoXML.CRA_NACIONAL, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario.getInstituicao().getLayoutPadraoXML(), arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.ENVIO_ARQUIVO_REMESSA)) {
				return mensagemServicoIndisponivel(usuario);
			}

			Arquivo arquivoJaEnviado = arquivoMediator.buscarArquivoEnviado(usuario, nomeArquivo);
			if (arquivoJaEnviado != null) {
				if (!arquivoJaEnviado.getInstituicaoEnvio().getCodigoCompensacao().trim().equals("582")) {
					return setRespostaArquivoJaEnviadoAnteriormente(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo, arquivoJaEnviado);
				}
			}
			if (!getNomeArquivo().contains(getUsuario().getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
			}

			setRemessas(ConversorArquivoVO.converterParaRemessaVO(converterStringArquivoVO(dados)));
			setObjectMensagemXml(remessaMediator.processarArquivoXML(getRemessas(), getUsuario(), nomeArquivo));
			loggerCra.sucess(usuario, getCraAcao(), "O arquivo de Remessa " + nomeArquivo + ", enviado por "
					+ usuario.getInstituicao().getNomeFantasia() + ", foi processado com sucesso.");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			loggerCra.error(getUsuario(), getCraAcao(), "Erro interno no processamento do arquivo de Remessa " + nomeArquivo + ".", ex);
			return setRespostaErroInternoNoProcessamento(LayoutPadraoXML.CRA_NACIONAL, nomeArquivo);
		}
		return gerarMensagem(getObjectMensagemXml(), CONSTANTE_RELATORIO_XML);
	}

	private ArquivoVO converterStringArquivoVO(String dados) {
		JAXBContext context;

		ArquivoVO arquivo = null;
		try {
			context = JAXBContext.newInstance(ArquivoVO.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			String xmlRecebido = "";

			Scanner scanner = new Scanner(new ByteArrayInputStream(new String(dados).getBytes()));
			while (scanner.hasNext()) {
				xmlRecebido = xmlRecebido + scanner.nextLine().replaceAll("& ", "&amp;");
				if (xmlRecebido.contains("<?xml version=")) {
					xmlRecebido = xmlRecebido.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", StringUtils.EMPTY);
				}
				if (xmlRecebido.contains("<comarca CodMun")) {
					xmlRecebido = xmlRecebido.replaceAll("<comarca CodMun=.[0-9]+..", StringUtils.EMPTY);
				}
				if (xmlRecebido.contains("</comarca>")) {
					xmlRecebido = xmlRecebido.replaceAll("</comarca>", StringUtils.EMPTY);
				}
			}
			scanner.close();

			InputStream xml = new ByteArrayInputStream(xmlRecebido.getBytes());
			arquivo = (ArquivoVO) unmarshaller.unmarshal(new InputSource(xml));

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(e.getMessage(), e.getCause());
		}
		return arquivo;
	}

	/**
	 * Consulta de remessas pelos cartórios
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @return
	 */
	public String buscarRemessa(String nomeArquivo, Usuario usuario) {
		setCraAcao(CraAcao.DOWNLOAD_ARQUIVO_REMESSA);
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);

		Remessa remessa = null;
		RemessaVO remessaVO = null;
		try {
			if (getUsuario().getId() == 0) {
				return setResposta(LayoutPadraoXML.CRA_NACIONAL, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (craServiceMediator.verificarServicoIndisponivel(CraServiceEnum.DOWNLOAD_ARQUIVO_REMESSA)) {
				return mensagemServicoIndisponivel(usuario);
			}
			remessaVO = remessaMediator.buscarRemessaParaCartorio(remessa, usuario.getInstituicao(), nomeArquivo);
			if (remessaVO == null) {
				return setRespostaPadrao(LayoutPadraoXML.CRA_NACIONAL, nomeArquivo, CodigoErro.CARTORIO_ARQUIVO_NAO_EXISTE);
			}

			setMensagem(gerarResposta(remessaVO, getNomeArquivo(), CONSTANTE_REMESSA_XML));
			loggerCra.sucess(getUsuario(), getCraAcao(),
					"Arquivo de Remessa " + nomeArquivo + " recebido com sucesso por " + getUsuario().getInstituicao().getNomeFantasia() + ".");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			loggerCra.error(getUsuario(), getCraAcao(), "Erro interno ao construir o arquivo de Remessa " + nomeArquivo + " recebido por "
					+ getUsuario().getInstituicao().getNomeFantasia() + ".", ex);
			return setRespostaErroInternoNoProcessamento(LayoutPadraoXML.CRA_NACIONAL, nomeArquivo);
		}
		return getMensagem();
	}

	private String gerarResposta(RemessaVO remessaVO, String nomeArquivo, String constanteConfirmacaoXml) {
		String msg = gerarMensagem(remessaVO, CONSTANTE_REMESSA_XML);
		msg = msg.replace(" xsi:type=\"remessaVO\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
		return msg;
	}

	public String getArquivoRecebido() {
		return arquivoRecebido;
	}

	public void setArquivoRecebido(String arquivoRecebido) {
		this.arquivoRecebido = arquivoRecebido;
	}

	public List<RemessaVO> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<RemessaVO> remessas) {
		this.remessas = remessas;
	}

	public ArquivoVO getArquivoVO() {
		return arquivoVO;
	}

	public void setArquivoVO(ArquivoVO arquivoVO) {
		this.arquivoVO = arquivoVO;
	}

}
