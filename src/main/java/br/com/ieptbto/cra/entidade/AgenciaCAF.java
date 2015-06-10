package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_AGENCIA_CAF")
@org.hibernate.annotations.Table(appliesTo = "TB_AGENCIA_CAF")
public class AgenciaCAF extends AbstractEntidade<AgenciaCAF> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String banco;
	private String codigoAgencia;
	private String nomeAgencia;
	private String cidade;
	private String uf;

	@Id
	@Column(name = "ID_AGENCIA_CAF", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name="BANCO")
	public String getBanco() {
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

	public void setBanco(String banco) {
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
	public int compareTo(AgenciaCAF entidade) {
		CompareToBuilder compareTo = new CompareToBuilder();
		compareTo.append(this.getBanco(), entidade.getBanco());
		compareTo.append(this.getCodigoAgencia(), entidade.getCodigoAgencia());
		compareTo.append(this.getUf(), entidade.getUf());
		compareTo.append(this.getCidade(), entidade.getCidade());
		return compareTo.toComparison();
	}

}
