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
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.enumeration.TipoAcaoLog;
import br.com.ieptbto.cra.exception.InfraException;
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
	private RemessaMediator remessaMediator;
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
		ArquivoVO arquivoVO = new ArquivoVO();
		setTipoAcaoLog(TipoAcaoLog.ENVIO_ARQUIVO_REMESSA);
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);

		try {
			if (getUsuario().getId() == 0) {
				return setResposta(LayoutPadraoXML.CRA_NACIONAL, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
				return setResposta(usuario.getInstituicao().getLayoutPadraoXML(), arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			if (!getNomeArquivo().contains(getUsuario().getInstituicao().getCodigoCompensacao())) {
				return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
			}
			if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
				return setRespostaArquivoEmBranco(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
			}

			setRemessas(ConversorArquivoVO.converterParaRemessaVO(converterStringArquivoVO(dados)));
			setObjectMensagemXml(remessaMediator.processarArquivoXML(getRemessas(), getUsuario(), nomeArquivo));
			loggerCra.sucess(usuario, getTipoAcaoLog(),
					"O arquivo de Remessa " + nomeArquivo + ", enviado por " + usuario.getInstituicao().getNomeFantasia() + ", foi processado com sucesso.");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			loggerCra.error(getUsuario(), getTipoAcaoLog(), "Erro interno no processamento do arquivo de Remessa " + nomeArquivo + ".", ex);
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
		setTipoAcaoLog(TipoAcaoLog.DOWNLOAD_ARQUIVO_REMESSA);
		Remessa remessa = null;
		RemessaVO remessaVO = null;
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);

		try {
			if (getUsuario().getId() == 0) {
				return setResposta(LayoutPadraoXML.CRA_NACIONAL, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
			}
			remessaVO = remessaMediator.buscarRemessaParaCartorio(remessa, usuario.getInstituicao(), nomeArquivo);
			if (remessaVO == null) {
				return setRespostaPadrao(LayoutPadraoXML.CRA_NACIONAL, nomeArquivo, CodigoErro.CARTORIO_ARQUIVO_NAO_EXISTE);
			}

			setMensagem(gerarResposta(remessaVO, getNomeArquivo(), CONSTANTE_REMESSA_XML));
			loggerCra.sucess(getUsuario(), getTipoAcaoLog(),
					"Arquivo de Remessa " + nomeArquivo + " recebido com sucesso por " + getUsuario().getInstituicao().getNomeFantasia() + ".");
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			loggerCra.error(getUsuario(), getTipoAcaoLog(),
					"Erro interno ao construir o arquivo de Remessa " + nomeArquivo + " recebido por " + getUsuario().getInstituicao().getNomeFantasia() + ".",
					e);
			return setRespostaErroInternoNoProcessamento(LayoutPadraoXML.CRA_NACIONAL, nomeArquivo);
		}
		return getMensagem();
	}

	private String gerarResposta(RemessaVO remessaVO, String nomeArquivo, String constanteConfirmacaoXml) {
		StringBuffer string = new StringBuffer();
		String msg = gerarMensagem(remessaVO, CONSTANTE_REMESSA_XML);
		string.append(msg);
		return string.toString();
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
