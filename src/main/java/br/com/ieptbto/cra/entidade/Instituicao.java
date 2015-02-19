package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "INSTITUICAO")
@org.hibernate.annotations.Table(appliesTo = "INSTITUICAO")
public class Instituicao extends AbstractEntidade<Instituicao> {

	private static final long serialVersionUID = 1L;
	private int id;
	private String instituicao;
	private String cnpj;
	private List<Usuario> listaUsuarios;
	//private TipoInstituicao tipoInstituicao;

	@Id
	@Column(name = "ID_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "INSTITUICAO")
	public String getInstituicao() {
		return instituicao;
	}

	@Column(name = "CNPJ")
	public String getCnpj() {
		return cnpj;
	}

	@OneToMany(mappedBy = "instituicao")
	public List<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setListaUsuarios(List<Usuario> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
	}

	@Override
	public int compareTo(Instituicao entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "Instituicao [instituicao=" + instituicao + "]";
	}
}
