package br.com.ieptbto.cra.validacao.regra;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.processador.ProcessadorArquivo;

/**
 * 
 * @author Lefer
 *
 */
public abstract class Regra {

	protected static final Logger logger = Logger.getLogger(ProcessadorArquivo.class);

	protected abstract void executar();
}
