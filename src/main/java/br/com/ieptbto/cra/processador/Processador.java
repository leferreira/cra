package br.com.ieptbto.cra.processador;

import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

/**
 * 
 * @author Lefer
 *
 */
public abstract class Processador {

	@SpringBean
	protected ConfiguracaoBase configuracao;
}
