package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
	private char[] uf;
	private Integer codIBGE;
	private List<Instituicao> listInstituicao;
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

	@Column(name = "UF", nullable=false)
	public char[] getUf() {
		return uf;
	}

	@Column(name = "COD_IBGE", nullable = false, unique = true, length = 7)
	public Integer getCodIBGE() {
		return codIBGE;
	}

	@ManyToMany(mappedBy = "listaMunicipios")
	public List<Instituicao> getListInstituicao() {
		return listInstituicao;
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

	public void setUf(char[] uf) {
		this.uf = uf;
	}

	public void setCodIBGE(Integer codIBGE) {
		this.codIBGE = codIBGE;
	}

	public void setListInstituicao(List<Instituicao> listInstituicao) {
		this.listInstituicao = listInstituicao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	@Override
	public int compareTo(Municipio entidade) {
		// TODO Auto-generated method stub
		return 0;
	}
}
