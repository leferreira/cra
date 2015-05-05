package br.com.ieptbto.cra.validacao.regra;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.processador.ProcessadorArquivo;

/**
 * 
 * @author Lefer
 *
 */
public abstract class Regra {

	protected static final Logger logger = Logger.getLogger(ProcessadorArquivo.class);

	private List<Exception> erros;

	protected abstract void executar();

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return this.erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

	public void addErro(Exception erro) {
		getErros().add(erro);
	}
}
