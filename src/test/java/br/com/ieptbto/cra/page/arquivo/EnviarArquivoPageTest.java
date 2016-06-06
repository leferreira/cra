package br.com.ieptbto.cra.page.arquivo;

import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
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
		tester.assertComponent("form:file", FileUploadField.class);
		tester.assertComponent("form:enviarArquivo", AjaxButton.class);
		form = tester.newFormTester("form");
		tester.assertErrorMessages();
		tester.assertNoInfoMessage();
	}

	@Test
	public void enviarArquivo() {
		tester.startPage(EnviarArquivoPage.class);
		form = tester.newFormTester("form");

		tester.assertNoInfoMessage();
	}

}
