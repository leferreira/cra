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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "INSTITUICAO")
@org.hibernate.annotations.Table(appliesTo = "INSTITUICAO")
public class Instituicao extends AbstractEntidade<Instituicao> {

	private static final long serialVersionUID = 1L;
	private int id;
	private String nomeFantasia;
	private String razaoSocial;
	private String cnpj;
	private String codCompensacao;
	private String email;
	private String contato;
	private Double valorConfirmacao;
	private String endereco;
	private String responsavel;
	private String agenciaCentralizadora;
	private String favorecido;
	private String bancoContaCorrente;
	private String agenciaContaCorrente;
	private String numContaCorrente;
	private String municipioCartorio;
	private Municipio comarcaCartorio;
	private boolean situacao;
	private List<Usuario> listaUsuarios = new ArrayList<Usuario>();
	private List<InstituicaoMunicipio> instituicaoMunicipio = new ArrayList<InstituicaoMunicipio>();
	private TipoInstituicao tipoInstituicao;

	@Id
	@Column(name = "ID_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_FANTASIA", nullable = false, unique = true, length = 50)
	public String getNomeFantasia() {
		return nomeFantasia;
	}

	@Column(name = "CNPJ", nullable = false, unique = true, length = 50)
	public String getCnpj() {
		return cnpj;
	}

	@Column(name = "RAZAO_SOCIAL", length = 50)
	public String getRazaoSocial() {
		return razaoSocial;
	}

	@Column(name = "CODIGO_COMPENSACAO", length = 3)
	public String getCodCompensacao() {
		return codCompensacao;
	}

	@Column(name = "EMAIL", length = 50)
	public String getEmail() {
		return email;
	}

	@Column(name = "CONTATO", length = 20)
	public String getContato() {
		return contato;
	}

	@Column(name = "VALOR_CONFIRMACAO", precision = 2)
	public Double getValorConfirmacao() {
		return valorConfirmacao;
	}

	@Column(name = "ENDERECO", length = 50)
	public String getEndereco() {
		return endereco;
	}

	@Column(name = "RESPOSAVEL", length = 20)
	public String getResponsavel() {
		return responsavel;
	}

	@Column(name = "AGENCIA_CENTRALIZADORA", length = 50)
	public String getAgenciaCentralizadora() {
		return agenciaCentralizadora;
	}

	@Column(name = "SITUACAO")
	public boolean isSituacao() {
		return situacao;
	}

	@Column(name = "FAVORECIDO")
	public String getFavorecido() {
		return favorecido;
	}

	@Column(name = "BANCO_CONTA_CORRENTE")
	public String getBancoContaCorrente() {
		return bancoContaCorrente;
	}

	@Column(name = "AGENCIA_CONTA_CORRENTE")
	public String getAgenciaContaCorrente() {
		return agenciaContaCorrente;
	}

	@Column(name = "NUM_CONTA_CORRENTE")
	public String getNumContaCorrente() {
		return numContaCorrente;
	}

	@Transient
	public Municipio getComarcaCartorio() {
		return comarcaCartorio;
	}

	/**
	 * Instituições do tipo cartório estão em uma comarca
	 * */
	@Column(name="MUNICIPIO_CARTORIO", nullable=true, length=40, unique=true)
	public String getMunicipioCartorio() {
		return municipioCartorio;
	}
	
	@OneToMany(mappedBy = "instituicao", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public List<InstituicaoMunicipio> getInstituicaoMunicipio() {
		return instituicaoMunicipio;
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

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
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

	public void setComarcaCartorio(Municipio municipio) {
		this.comarcaCartorio = municipio;
	}

	@Transient
	public String getStatus() {
		if (isSituacao() == true) {
			return "Ativo";
		} else {
			return "Não Ativo";
		}
	}

	public void setStatus(String status) {
		if (status == "Ativo") {
			setSituacao(true);
		} else {
			setSituacao(false);
		}
	}

	public void setFavorecido(String favorecido) {
		this.favorecido = favorecido;
	}

	public void setBancoContaCorrente(String bancoContaCorrente) {
		this.bancoContaCorrente = bancoContaCorrente;
	}

	public void setAgenciaContaCorrente(String agenciaContaCorrente) {
		this.agenciaContaCorrente = agenciaContaCorrente;
	}

	public void setNumContaCorrente(String numContaCorrente) {
		this.numContaCorrente = numContaCorrente;
	}

	public void setInstituicaoMunicipio(
			List<InstituicaoMunicipio> instituicaoMunicipio) {
		this.instituicaoMunicipio = instituicaoMunicipio;
	}

	public void setMunicipioCartorio(String municipioCartorio) {
		this.municipioCartorio = municipioCartorio;
	}
}
