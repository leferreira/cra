package br.com.ieptbto.cra.page.arquivo;

import org.junit.Before;
import org.junit.Test;

import br.com.ieptbto.cra.base.BaseTest;

/**
 * 
 * @author Lefer
 *
 */
public class EnviarArquivoPageTest extends BaseTest {

	@Before
	public void setUp() {
		super.setUp();
		logar();
	}

	@Test
	public void abrirPaginaEnvioArquivo() {
		tester.startPage(EnviarArquivoPage.class);
		form = tester.newFormTester("form");
		// form.setValue("usuarioInputPanel:nome", "Teste");
		tester.assertErrorMessages();
		tester.assertNoInfoMessage();
	}

}
