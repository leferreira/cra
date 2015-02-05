package br.com.ieptbto.cra.util;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.mediator.UsuarioMediator;

/**
 * 
 * 
 */
public class CargaInicialPage extends WebPage {

	private static final long serialVersionUID = 432141L;

	@SpringBean
	UsuarioMediator usuarioMediator;

	public CargaInicialPage() {
		usuarioMediator.cargaInicial();
	}

}
