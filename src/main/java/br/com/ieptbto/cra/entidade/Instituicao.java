package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private String razaoSocial;
	private String cnpj;
	private String codCompensacao;
	private String email;
	private String contato;
	private Double valorConfirmacao;
	private String endereco;
	private String responsavel;
	private String agenciaCentralizadora;
	private boolean situacao;
	private List<Usuario> listaUsuarios;
	private TipoInstituicao tipoInstituicao;

	@Id
	@Column(name = "ID_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "INSTITUICAO", nullable = false, unique=true, length=50)
	public String getInstituicao() {
		return instituicao;
	}

	@Column(name = "CNPJ", nullable = false, unique=true, length=50)
	public String getCnpj() {
		return cnpj;
	}

	@Column(name = "RAZAO_SOCIAL", length=50)
	public String getRazaoSocial() {
		return razaoSocial;
	}

	@Column(name = "CODIGO_COMPENSACAO", length=3)
	public String getCodCompensacao() {
		return codCompensacao;
	}

	@Column(name = "EMAIL", length=50)
	public String getEmail() {
		return email;
	}

	@Column(name = "CONTATO", length=20)
	public String getContato() {
		return contato;
	}

	@Column(name = "VALOR_CONFIRMACAO", precision=2)
	public Double getValorConfirmacao() {
		return valorConfirmacao;
	}

	@Column(name = "ENDERECO", length=50)
	public String getEndereco() {
		return endereco;
	}

	@Column(name = "RESPOSAVEL", length=20)
	public String getResponsavel() {
		return responsavel;
	}

	@Column(name = "AGENCIA_CENTRALIZADORA", length=50)
	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	@Column(name = "SITUACAO")
	public boolean isSituacao() {
		return situacao;
	}

	@ManyToOne
	@JoinColumn(name = "TIPO_INSTITUICAO_ID")
	public TipoInstituicao getTipoInstituicao() {
		return tipoInstituicao;
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
		return 0;
	}

	@Override
	public String toString() {
		return "Instituicao [instituicao=" + instituicao + "]";
	}

	public void setTipoInstituicao(TipoInstituicao tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public void setCodCompensacao(String codCompensacao) {
		this.codCompensacao = codCompensacao;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public void setValorConfirmacao(Double valorConfirmacao) {
		this.valorConfirmacao = valorConfirmacao;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public void setAgenciaCentralizadora(String agenciaCentralizadora) {
		this.agenciaCentralizadora = agenciaCentralizadora;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}
}
