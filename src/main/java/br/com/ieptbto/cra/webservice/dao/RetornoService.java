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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoRetornoVO;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.entidade.vo.RetornoVO;
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class RetornoService extends CraWebService {

	@Autowired
	private RemessaMediator remessaMediator;
	private ArquivoVO arquivoVO;
	private ArquivoRetornoVO arquivoRetornoVO;
	private RetornoVO retornoVO;

	public String processar(String nomeArquivo, Usuario usuario) {
		List<RemessaVO> remessas = new ArrayList<RemessaVO>();
		ArquivoVO arquivoVO = null;
		setUsuario(usuario);
		setNomeArquivo(nomeArquivo);
		
		if (getUsuario() == null) {
			return setResposta(LayoutPadraoXML.CRA_NACIONAL, arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
		}
		if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
			return setResposta(usuario.getInstituicao().getLayoutPadraoXML(), arquivoVO, nomeArquivo, CONSTANTE_RELATORIO_XML);
		}
		if (!getNomeArquivo().contains(getUsuario().getInstituicao().getCodigoCompensacao())) {
			return setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
		}

		remessas = remessaMediator.buscarArquivos(getNomeArquivo(), getUsuario().getInstituicao());
		if (remessas.isEmpty()) {
			return setRespostaArquivoEmProcessamento(usuario.getInstituicao().getLayoutPadraoXML(), nomeArquivo);
		}
		return gerarResposta(usuario.getInstituicao().getLayoutPadraoXML() ,remessas, getNomeArquivo(), CONSTANTE_RETORNO_XML);
	}

	private String gerarResposta(LayoutPadraoXML layoutPadraoResposta,List<RemessaVO> remessas, String nomeArquivo, String constanteRetornoXml) {
		StringBuffer string = new StringBuffer();
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>";
		String cabecalho = "<retorno>";
		
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			string.append("<nome_arquivo>" + nomeArquivo + "</nome_arquivo>");
		}
		for (RemessaVO remessaVO : remessas) {
			if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
				string.append("<comarca CodMun=\""+ remessaVO.getCabecalho().getCodigoMunicipio() +"\">");
				String msg = gerarMensagem(remessaVO, CONSTANTE_RETORNO_XML).replace("</retorno>", "").replace(cabecalho, "");
				string.append(msg);
				string.append("</comarca>");
			} else {
				String msg = gerarMensagem(remessaVO, CONSTANTE_RETORNO_XML).replace("</retorno>", "").replace(cabecalho, "");
				string.append(msg);
					
			}
		}
		string.append("</retorno>");
		return xml + cabecalho + string.toString();

	}

	protected String gerarMensagem(Object mensagem, String nomeNo) {
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(mensagem.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
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

	public ArquivoVO getArquivoVO() {
		return arquivoVO;
	}

	public void setArquivoVO(ArquivoVO arquivoVO) {
		this.arquivoVO = arquivoVO;
	}

	public ArquivoRetornoVO getArquivoRetornoVO() {
		return arquivoRetornoVO;
	}

	public RetornoVO getRetornoVO() {
		return retornoVO;
	}

	public void setArquivoRetornoVO(ArquivoRetornoVO arquivoRetornoVO) {
		this.arquivoRetornoVO = arquivoRetornoVO;
	}

	public void setRetornoVO(RetornoVO retornoVO) {
		this.retornoVO = retornoVO;
	}
}
