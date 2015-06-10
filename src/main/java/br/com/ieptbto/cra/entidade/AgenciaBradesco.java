package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_AGENCIA_BRADESCO")
@org.hibernate.annotations.Table(appliesTo = "TB_AGENCIA_BRADESCO")
public class AgenciaBradesco extends AbstractEntidade<AgenciaBradesco> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private String nomeCedente;
	private String cnpj;
	private String codigoAgenciaCedente;
	private String agenciaDestino;
	private String orientacao;
	
	@Id
	@Column(name = "ID_AGENCIA_BRADESCO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	@Column(name = "NOME_CEDENTE")
	public String getNomeCedente() {
		return nomeCedente;
	}

	@Column(name = "CNPJ")
	public String getCnpj() {
		return cnpj;
	}

	@Column(name = "CODIGO_AGENCIA_CEDENTE")
	public String getCodigoAgenciaCedente() {
		return codigoAgenciaCedente;
	}

	@Column(name = "ORIENTACAO")
	public String getOrientacao() {
		return orientacao;
	}
	
	@Column(name = "AGENCIA_DESTINO")
	public String getAgenciaDestino() {
		return agenciaDestino;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setNomeCedente(String nomeCedente) {
		this.nomeCedente = nomeCedente;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCodigoAgenciaCedente(String codigoAgenciaCedente) {
		this.codigoAgenciaCedente = codigoAgenciaCedente;
	}

	public void setAgenciaDestino(String agenciaDestino) {
		this.agenciaDestino = agenciaDestino;
	}
	
	@Override
	public int compareTo(AgenciaBradesco entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setOrientacao(String orientacao) {
		this.orientacao = orientacao;
	}
}
