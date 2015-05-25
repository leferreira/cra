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
@XmlRootElement(name = "descricao")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "dataEnvio", "tipoArquivo", "nomeArquivo", "dataMovimento", "portador", "usuario" })
public class Descricao {

	public static String XML_UPLOAD_REMESSA = "XML_UPLOAD_REMESSA";

	@XmlElement(name = "dataEnvio")
	private String dataEnvio;

	@XmlElement(name = "tipoArquivo")
	private String tipoArquivo;

	@XmlElement(name = "nomeArquivo")
	private String nomeArquivo;

	@XmlElement(name = "dataMovimento")
	private String dataMovimento;

	@XmlElement(name = "portador")
	private String portador;

	@XmlElement(name = "usuario")
	private String usuario;

	public String getDataEnvio() {
		return dataEnvio;
	}

	public String getTipoArquivo() {
		return tipoArquivo;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}

	public String getPortador() {
		return portador;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setDataEnvio(String dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setTipoArquivo(String tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setPortador(String portador) {
		this.portador = portador;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
