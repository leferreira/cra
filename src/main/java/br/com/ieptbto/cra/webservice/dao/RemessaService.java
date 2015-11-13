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

import br.com.ieptbto.cra.conversor.ConversorArquivoVo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RemessaMediator;

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
	 * 
	 * @param nomeArquivo
	 * @param usuario2
	 * @param arquivoRecebido
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, ArquivoVO arquivoRecebido) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		setArquivoVO(arquivoRecebido);

		if (getArquivoVO() == null || getUsuario() == null) {
			ArquivoVO arquivo = new ArquivoVO();
			return setResposta(arquivo, nomeArquivo, CONSTANTE_REMESSA_XML);
		}

		setRemessas(ConversorArquivoVo.converterParaRemessaVO(getArquivoVO()));

		return gerarMensagem(remessaMediator.processarArquivoXML(getRemessas(), getUsuario(), nomeArquivo), "relatorio");
	}

	/**
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @param dados
	 * @return
	 */
	public String processar(String nomeArquivo, Usuario usuario, String dados) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
			return setRespostaArquivoEmBranco(nomeArquivo);
		}
		return processar(nomeArquivo, usuario, converterStringArquivoVO(dados));
	}

	/**
	 * 
	 * @param nomeArquivo
	 * @param usuario
	 * @return
	 */
	public String buscarRemessa(String nomeArquivo, Usuario usuario) {
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		RemessaVO remessa = null;

		if (getUsuario() == null) {
			return setResposta(arquivoVO, nomeArquivo, CONSTANTE_REMESSA_XML);
		}

		if (getNomeArquivo() == null || !getNomeArquivo().toUpperCase().startsWith(TipoArquivoEnum.REMESSA.getConstante())) {
			return setResposta(arquivoVO, nomeArquivo, CONSTANTE_REMESSA_XML);
		}

		if (TipoInstituicaoCRA.CARTORIO.equals(getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao())) {
			remessa = remessaMediator.buscarRemessaParaCartorio(getUsuario().getInstituicao(), getNomeArquivo());
		}

		if (remessa == null) {
			return setResposta(arquivoVO, nomeArquivo, CONSTANTE_REMESSA_XML);
		}

		return gerarResposta(remessa, getNomeArquivo(), CONSTANTE_REMESSA_XML);
	}

	private String gerarResposta(RemessaVO remessaVO, String nomeArquivo, String constanteConfirmacaoXml) {
		StringBuffer string = new StringBuffer();
		String msg = gerarMensagem(remessaVO, CONSTANTE_REMESSA_XML);
		string.append(msg);
		return string.toString();
	}

	/**
	 * 
	 * @param dados
	 * @return
	 */
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
