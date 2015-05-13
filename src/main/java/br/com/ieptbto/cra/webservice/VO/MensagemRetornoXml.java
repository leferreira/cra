package br.com.ieptbto.cra.webservice.VO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Leandro
 * 
 */
@XmlRootElement(name = "relatorio")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "descricao", "detalhamento", "codigoFinal", "descricaoFinal" })
public class MensagemRetornoXml {

	@XmlElement(name = "descricao")
	private Descricao descricao;

	@XmlElement(name = "final")
	private String codigoFinal;

	@XmlElement(name = "descricao_final")
	private String descricaoFinal;

	@XmlElement(name = "detalhamento")
	private Detalhamento detalhamento;

	public Descricao getDescricao() {
		return descricao;
	}

	public String getCodigoFinal() {
		return codigoFinal;
	}

	public String getDescricaoFinal() {
		return descricaoFinal;
	}

	public Detalhamento getDetalhamento() {
		return detalhamento;
	}

	public void setDescricao(Descricao descricao) {
		this.descricao = descricao;
	}

	public void setCodigoFinal(String codigoFinal) {
		this.codigoFinal = codigoFinal;
	}

	public void setDescricaoFinal(String descricaoFinal) {
		this.descricaoFinal = descricaoFinal;
	}

	public void setDetalhamento(Detalhamento detalhamento) {
		this.detalhamento = detalhamento;
	}

}
