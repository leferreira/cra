package br.com.ieptbto.cra.exception;

import br.com.ieptbto.cra.error.CodigoErro;

/**
 * 
 * @author Lefer
 *
 */
public class XmlCraException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String municipio;
	private String codigoIbge;
	private CodigoErro erro;

	public XmlCraException(String message, Throwable cause, String municipio, String codigoIbge, CodigoErro erro) {
		super(message, cause);
		this.municipio = municipio;
		this.codigoIbge = codigoIbge;
		this.erro = erro;
	}

	public XmlCraException(String message, String municipio, String codigoIbge, CodigoErro erro) {
		super(message);
		this.municipio = municipio;
		this.codigoIbge = codigoIbge;
		this.erro = erro;
	}

	public String getMunicipio() {
		return municipio;
	}

	public String getCodigoIbge() {
		return codigoIbge;
	}

	public CodigoErro getErro() {
		return erro;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public void setCodigoIbge(String codigoIbge) {
		this.codigoIbge = codigoIbge;
	}

	public void setErro(CodigoErro erro) {
		this.erro = erro;
	}

}
