package br.com.ieptbto.cra.entidade;

import java.math.BigDecimal;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "TB_INSTITUICAO")
@org.hibernate.annotations.Table(appliesTo = "TB_INSTITUICAO")
public class Instituicao extends AbstractEntidade<Instituicao> {

	private static final long serialVersionUID = 1L;
	private int id;
	private String nomeFantasia;
	private String razaoSocial;
	private String cnpj;
	private String codigoCompensacao;
	private String email;
	private String contato;
	private BigDecimal valorConfirmacao;
	private String endereco;
	private String responsavel;
	private String agenciaCentralizadora;
	private String favorecido;
	private String bancoContaCorrente;
	private String agenciaContaCorrente;
	private String numeroContaCorrente;
	private boolean situacao;

	private TipoInstituicao tipoInstituicao;
	private List<Arquivo> arquivoEnviados;
	private List<Usuario> listaUsuarios;
	private Municipio municipio;

	@Id
	@Column(name = "ID_INSTITUICAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_FANTASIA", nullable = false, unique = true, length = 100)
	public String getNomeFantasia() {
		return nomeFantasia;
	}

	@Column(name = "CNPJ", nullable = false, unique = true, length = 50)
	public String getCnpj() {
		return cnpj;
	}

	@Column(name = "RAZAO_SOCIAL", length = 100, nullable = false, unique = true)
	public String getRazaoSocial() {
		return razaoSocial;
	}

	@Column(name = "CODIGO_COMPENSACAO", length = 3)
	public String getCodigoCompensacao() {
		return codigoCompensacao;
	}

	@Column(name = "EMAIL", length = 50)
	public String getEmail() {
		return email;
	}

	@Column(name = "CONTATO", length = 40)
	public String getContato() {
		return contato;
	}

	@Column(name = "VALOR_CONFIRMACAO", precision = 2)
	public BigDecimal getValorConfirmacao() {
		return valorConfirmacao;
	}

	@Column(name = "ENDERECO", length = 50)
	public String getEndereco() {
		return endereco;
	}

	@Column(name = "RESPOSAVEL", length = 100)
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

	@Column(name = "NUMERO_CONTA_CORRENTE")
	public String getNumeroContaCorrente() {
		return numeroContaCorrente;
	}

	@OneToMany(mappedBy = "instituicaoEnvio", fetch = FetchType.LAZY)
	public List<Arquivo> getArquivoEnviados() {
		return arquivoEnviados;
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

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "MUNICIPIO_ID", columnDefinition = "integer NULL")
	public Municipio getMunicipio() {
		return municipio;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCodigoCompensacao(String codigoCompensacao) {
		this.codigoCompensacao = codigoCompensacao;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public void setValorConfirmacao(BigDecimal valorConfirmacao) {
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

	public void setFavorecido(String favorecido) {
		this.favorecido = favorecido;
	}

	public void setBancoContaCorrente(String bancoContaCorrente) {
		this.bancoContaCorrente = bancoContaCorrente;
	}

	public void setAgenciaContaCorrente(String agenciaContaCorrente) {
		this.agenciaContaCorrente = agenciaContaCorrente;
	}

	public void setNumeroContaCorrente(String numeroContaCorrente) {
		this.numeroContaCorrente = numeroContaCorrente;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	public void setTipoInstituicao(TipoInstituicao tipoInstituicao) {
		this.tipoInstituicao = tipoInstituicao;
	}

	public void setArquivoEnviados(List<Arquivo> arquivoEnviados) {
		this.arquivoEnviados = arquivoEnviados;
	}

	public void setListaUsuarios(List<Usuario> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
	}

	@Override
	public int compareTo(Instituicao entidade) {
		return 0;
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
}
