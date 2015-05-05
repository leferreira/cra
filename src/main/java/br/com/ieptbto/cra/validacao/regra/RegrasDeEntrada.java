package br.com.ieptbto.cra.validacao.regra;

import java.io.File;
import java.util.List;

import br.com.ieptbto.cra.entidade.Usuario;

/**
 * 
 * @author Lefer
 *
 */
public abstract class RegrasDeEntrada extends Regra {

	protected Usuario usuario;
	protected File arquivo;

	protected abstract void validar(File arquivo, Usuario usuario, List<Exception> erros);

}
