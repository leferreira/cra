package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class Cabecalho<T> extends AbstractEntidade<T> {

	private String identificacaoTransacaoRemetente;
	private String identificacaoTransacaoDestinatario;
	private String identificacaoTransacaoTipo;

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
	@Transient
	public abstract int getId();

	@Override
	public abstract int compareTo(T entidade);

}
