package br.com.ieptbto.cra.webservice.VO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Leandro
 * 
 */
@XmlRootElement(name = "mensagem")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mensagem {

	@XmlAttribute(name = "municipio")
	private String municipio;

	@XmlAttribute(name = "codigo")
	private String codigo;

	@XmlAttribute(name = "descricao")
	private String descricao;

	public String getMunicipio() {
		return municipio;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
