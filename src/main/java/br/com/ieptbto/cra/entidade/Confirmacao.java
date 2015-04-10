package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name = "TB_CONFIRMACAO")
@org.hibernate.annotations.Table(appliesTo = "TB_CONFIRMACAO")
public class Confirmacao extends Titulo<Confirmacao> {

	private int id;
	private Remessa remessa;
	private String identificacaoTransacaoRemetente;
	private String identificacaoTransacaoDestinatario;
	private String identificacaoTransacaoTipo;
	private TituloRemessa titulo;

	@Id
	@Column(name = "ID_CONFIRMACAO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public int getId() {
		return id;
	}

	@Column(name = "IDENTIFICAO_TRANSACAO_REMENTE", length = 3)
	public String getIdentificacaoTransacaoRemetente() {
		return identificacaoTransacaoRemetente;
	}

	@Column(name = "IDENTIFICAO_TRANSACAO_DESTINATARIO", length = 3)
	public String getIdentificacaoTransacaoDestinatario() {
		return identificacaoTransacaoDestinatario;
	}

	@Column(name = "IDENTIFICAO_TRANSACAO_TIPO", length = 3)
	public String getIdentificacaoTransacaoTipo() {
		return identificacaoTransacaoTipo;
	}

	@ManyToOne
	@JoinColumn(name = "REMESSA_ID")
	public Remessa getRemessa() {
		return remessa;
	}

	@OneToOne
	@JoinColumn(name = "TITULO_ID")
	public TituloRemessa getTitulo() {
		return titulo;
	}

	public void setTitulo(TituloRemessa titulo) {
		this.titulo = titulo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

	public void setIdentificacaoTransacaoDestinatario(String identificacaoTransacaoDestinatario) {
		this.identificacaoTransacaoDestinatario = identificacaoTransacaoDestinatario;
	}

	public void setIdentificacaoTransacaoRemetente(String identificacaoTransacaoRemetente) {
		this.identificacaoTransacaoRemetente = identificacaoTransacaoRemetente;
	}

	public void setIdentificacaoTransacaoTipo(String identificacaoTransacaoTipo) {
		this.identificacaoTransacaoTipo = identificacaoTransacaoTipo;
	}

	@Override
	public int compareTo(Confirmacao entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
