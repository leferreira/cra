package br.com.ieptbto.cra.entidade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "MUNICIPIO")
@org.hibernate.annotations.Table(appliesTo = "MUNICIPIO")
public class Municipio extends AbstractEntidade<Municipio>{

	private int id;
	private String nomeMunicipio;
	private String uf;
	private Integer codIBGE;
	private List<InstituicaoMunicipio> instituicaoMunicipio = new ArrayList<InstituicaoMunicipio>();
	private boolean situacao;

	@Id
	@Column(name = "ID_MUNICIPIO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_MUNICIPIO", nullable = false, unique = true, length = 60)
	public String getNomeMunicipio() {
		return nomeMunicipio;
	}

	@Column(name = "UF", nullable=false, length=2)
	public String getUf() {
		return uf;
	}

	@Column(name = "COD_IBGE", nullable = false, unique = true, length = 7)
	public Integer getCodIBGE() {
		return codIBGE;
	}

	@OneToMany(mappedBy = "municipio", fetch = FetchType.LAZY, cascade= CascadeType.ALL)
	public List<InstituicaoMunicipio> getInstituicaoMunicipio() {
		return instituicaoMunicipio;
	}

	@Column(name = "SITUACAO")
	public boolean isSituacao() {
		return situacao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNomeMunicipio(String nomeMunicipio) {
		this.nomeMunicipio = nomeMunicipio;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public void setCodIBGE(Integer codIBGE) {
		this.codIBGE = codIBGE;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	public void setInstituicaoMunicipio(
			List<InstituicaoMunicipio> instituicaoMunicipio) {
		this.instituicaoMunicipio = instituicaoMunicipio;
	}
	
	@Override
	public int compareTo(Municipio entidade) {
		return 0;
	}
}
