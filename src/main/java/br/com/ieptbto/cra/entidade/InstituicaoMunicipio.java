package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "INSTITUICAO_MUNICIPIO")
@org.hibernate.annotations.Table(appliesTo = "INSTITUICAO_MUNICIPIO")
public class InstituicaoMunicipio extends
		AbstractEntidade<InstituicaoMunicipio> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private Instituicao instituicao;
	private Municipio municipio;

	@Id
	@Column(name = "ID_INSTITUICAO_MUNICIPIO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "MUNICIPIO_ID")
	public Municipio getMunicipio() {
		return municipio;
	}

	@ManyToOne
	@JoinColumn(name = "INSTITUICAO_ID")
	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	@Override
	public int compareTo(InstituicaoMunicipio entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}
}
