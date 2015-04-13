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

	private CabecalhoRemessa cabecalho;
	private int id;
	private TituloRemessa titulo;

	@Id
	@Column(name = "ID_RETORNO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public int getId() {
		return this.id;
	}

	@ManyToOne
	@JoinColumn(name = "CABECALHO_ID")
	public CabecalhoRemessa getCabecalho() {
		return cabecalho;
	}

	@ManyToOne
	@JoinColumn(name = "TITULO_ID")
	public TituloRemessa getTitulo() {
		return titulo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCabecalho(CabecalhoRemessa cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.titulo = titulo;
	}

	@Override
	public int compareTo(Retorno entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
