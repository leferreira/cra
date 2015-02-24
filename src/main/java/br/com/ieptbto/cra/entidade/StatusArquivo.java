package br.com.ieptbto.cra.entidade;

import org.joda.time.DateTime;

/**
 * 
 * @author Lefer
 *
 */
public class StatusArquivo extends AbstractEntidade<StatusArquivo> {

	/****/
	private static final long serialVersionUID = 853651236L;

	private int id;
	private DateTime data;
	private Arquivo arquivo;

	@Override
	public int getId() {
		return this.id;
	}

	public DateTime getData() {
		return data;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setData(DateTime data) {
		this.data = data;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	@Override
	public int compareTo(StatusArquivo entidade) {
		// TODO Auto-generated method stub
		return 0;
	}

}
