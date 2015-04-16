package br.com.ieptbto.cra.entidade;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

public class Cartorio extends Instituicao {

	/***/
	private static final long serialVersionUID = 1L;
	
	private Municipio municipio;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional=true)
	@JoinColumn(name = "MUNICIPIO_ID", columnDefinition = "integer NULL", nullable=true)
	public Municipio getMunicipio() {
		return municipio;
	}
	
	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}
}
