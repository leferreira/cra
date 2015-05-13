package br.com.ieptbto.cra.webservice.VO;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Leandro
 * 
 */
@XmlRootElement(name = "detalhamento")
@XmlAccessorType(XmlAccessType.FIELD)
public class Detalhamento {

	@XmlElement(name = "mensagem")
	private List<Mensagem> mensagem;

	@XmlElement(name = "erro")
	private List<Erro> erro;

	public List<Mensagem> getMensagem() {
		return mensagem;
	}

	public List<Erro> getErro() {
		return erro;
	}

	public void setMensagem(List<Mensagem> mensagem) {
		this.mensagem = mensagem;
	}

	public void setErro(List<Erro> erro) {
		this.erro = erro;
	}

}
