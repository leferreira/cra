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
     * ENVIO DE REMESSAS PELOS BANCOS/CONVÊNIOS
     * 
     * @param nomeArquivo
     * @param usuario
     * @param dados
     * @return
     */
    public String processar(String nomeArquivo, Usuario usuario, String dados) {
	ArquivoVO arquivo = new ArquivoVO();
	setUsuario(usuario);
	setNomeArquivo(nomeArquivo);

	if (getUsuario() == null) {
	    return setResposta(LayoutPadraoXML.CRA_NACIONAL, arquivo, nomeArquivo, CONSTANTE_RELATORIO_XML);
	}
	if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
	    return setResposta(usuario.getInstituicao().getLayoutPadraoXML(), arquivo, nomeArquivo, CONSTANTE_RELATORIO_XML);
	}
	if (!getNomeArquivo().contains(getUsuario().getInstituicao().getCodigoCompensacao())) {
	    return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
	}
	if (dados == null || StringUtils.EMPTY.equals(dados.trim())) {
	    return setRespostaArquivoEmBranco(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
	}
	return processarEnvioRemessa(nomeArquivo, usuario, converterStringArquivoVO(dados));
    }

    private String processarEnvioRemessa(String nomeArquivo, Usuario usuario, ArquivoVO arquivoRecebido) {
	setUsuario(usuario);
	setNomeArquivo(nomeArquivo);
	setArquivoVO(arquivoRecebido);

	setRemessas(ConversorArquivoVO.converterParaRemessaVO(getArquivoVO()));
	return gerarMensagem(remessaMediator.processarArquivoXML(getRemessas(), getUsuario(), nomeArquivo), CONSTANTE_RELATORIO_XML);
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
     * CONSULTA DE REMESSAS PELOS CARTÓRIOS
     * 
     * @param nomeArquivo
     * @param usuario
     * @return
     */
    public String buscarRemessa(String nomeArquivo, Usuario usuario) {
	Remessa remessa = null;
	RemessaVO remessaVO = null;
	setUsuario(usuario);
	setNomeArquivo(nomeArquivo);

	try {
	    remessaVO = remessaMediator.buscarRemessaParaCartorio(remessa, usuario.getInstituicao(), nomeArquivo);
	    if (remessaVO == null) {
		return setRespostaPadrao(LayoutPadraoXML.CRA_NACIONAL, nomeArquivo, CodigoErro.CARTORIO_ARQUIVO_NAO_EXISTE);
	    }

	    setMensagem(gerarResposta(remessaVO, getNomeArquivo(), CONSTANTE_REMESSA_XML));
	} catch (Exception e) {
	    e.printStackTrace();
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
