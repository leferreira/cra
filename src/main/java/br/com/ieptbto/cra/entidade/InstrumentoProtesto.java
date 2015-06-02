package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

/**
 * @author Thasso Araújo
 *
 */
@Entity
@Audited
@Table(name = "TB_INSTRUMENTO_PROTESTO")
@org.hibernate.annotations.Table(appliesTo = "TB_INSTRUMENTO_PROTESTO")
public class InstrumentoProtesto extends AbstractEntidade<InstrumentoProtesto> {

	/***/
	private static final long serialVersionUID = 1L;
	private int id;
	private TituloRemessa titulo;
	private LocalDate dataDeEntrada;
	private LocalDate dataSlip;
	private LocalDate dataEnvio;
	private boolean situacao;
	
	@Id
	@Column(name = "ID_INSTRUMENTO_PROTESTO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name="TITULO_ID")
	public TituloRemessa getTitulo() {
		return titulo;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.titulo = titulo;
	}

	@Column(name = "DATA_ENTRADA")
	public LocalDate getDataDeEntrada() {
		return dataDeEntrada;
	}

	public void setDataDeEntrada(LocalDate dataDeEntrada) {
		this.dataDeEntrada = dataDeEntrada;
	}

	@Column(name = "DATA_SLIP")
	public LocalDate getDataSlip() {
		return dataSlip;
	}

	public void setDataSlip(LocalDate dataSlip) {
		this.dataSlip = dataSlip;
	}

	@Column(name = "DATA_ENVIO")
	public LocalDate getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(LocalDate dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	@Column(name = "SITUACAO")
	public boolean isSituacao() {
		return situacao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	@Override
	public int compareTo(InstrumentoProtesto entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
