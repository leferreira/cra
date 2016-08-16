package br.com.ieptbto.cra.page.tipoInstituicao;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;

import br.com.ieptbto.cra.base.BaseTest;

public class IncluirTipoInstituicaoPageTest extends BaseTest {

	@Before
	public void setUp() {
		super.setUp();
		logar();
	}

	// @Test
	// @Transactional
	// @Rollback(true)
	public void testCarregarIncluirTipoInstituicaoPage() {
		tester.startPage(IncluirTipoInstituicaoPage.class);
		form = tester.newFormTester("form");
		tester.assertComponent("form:tipoInputPanel:tipoInstituicao", TextField.class);
		tester.assertComponent("form:tipoInputPanel:arquivosEnvioPermitido", Component.class);
		tester.assertNoErrorMessage();
	}
}
