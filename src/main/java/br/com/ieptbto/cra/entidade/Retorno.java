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
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_RETORNO")
@org.hibernate.annotations.Table(appliesTo = "TB_RETORNO")
public class Retorno extends Titulo<Retorno> {

	private int id;
	private Remessa remessa;
	private CabecalhoRemessa cabecalho;

	@Id
	@Column(name = "ID_CONFIRMACAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "REMESSA_ID")
	public Remessa getRemessa() {
		return remessa;
	}

	@ManyToOne
	@JoinColumn(name = "CABECALHO_ID")
	public CabecalhoRemessa getCabecalho() {
		return cabecalho;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setCabecalho(CabecalhoRemessa cabecalho) {
		this.cabecalho = cabecalho;
	}

}
