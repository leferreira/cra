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

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_ARQUIVO_DE_PARA")
@org.hibernate.annotations.Table(appliesTo = "TB_ARQUIVO_DE_PARA")
public class ArquivoDePara extends AbstractEntidade<ArquivoDePara> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private Instituicao banco;
	private String codigoAgencia;
	private String nomeAgencia;
	private String cidade;
	private String uf;

	@Id
	@Column(name = "ID_ARQUIVO_DE_PARA", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name="INSTITUICAO_BANCO_ID")
	public Instituicao getBanco() {
		return banco;
	}

	@Column(name="CODIGO_AGENCIA")
	public String getCodigoAgencia() {
		return codigoAgencia;
	}

	@Column(name="NOME_AGENCIA")
	public String getNomeAgencia() {
		return nomeAgencia;
	}

	@Column(name="CIDADE")
	public String getCidade() {
		return cidade;
	}

	@Column(name="UF")
	public String getUf() {
		return uf;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setBanco(Instituicao banco) {
		this.banco = banco;
	}

	public void setCodigoAgencia(String codigoAgencia) {
		this.codigoAgencia = codigoAgencia;
	}

	public void setNomeAgencia(String nomeAgencia) {
		this.nomeAgencia = nomeAgencia;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	@Override
	public int compareTo(ArquivoDePara entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
