package br.com.ieptbto.cra.page.municipio;

import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.base.BaseTest;

public class IncluirMunicipioPageTest extends BaseTest{

	@Before
	public void setUp() {
		super.setUp();
		logar();
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCarregarIncluirMunicipioPage() {
		tester.startPage(IncluirMunicipioPage.class);
		form = tester.newFormTester("form");
		tester.assertComponent("form:municipioInputPanel:nomeMunicipio", TextField.class);
		tester.assertComponent("form:municipioInputPanel:uf", TextField.class);
		tester.assertComponent("form:municipioInputPanel:codIBGE", TextField.class);
		tester.assertNoErrorMessage();
	}
	
	/**
	 * Teste falho devido ao campo IBGE sem obrigatório
	 * */
	@Test
	public void testInsertMunicipioFail() {
		tester.startPage(IncluirMunicipioPage.class);
		form = tester.newFormTester("form");
		form.setValue("municipioInputPanel:nomeMunicipio", "MUNICIPIO TESTE");
		form.setValue("municipioInputPanel:uf", "TO");
		form.setValue("municipioInputPanel:codIBGE", "");
		form.submit();

		tester.assertErrorMessages(new String[] { "Campo 'Código do IBGE' é obrigatório." });
		tester.assertNoInfoMessage();
	}
	
	@Test
	public void testInsertMunicipioSuccessful() {
		tester.startPage(IncluirMunicipioPage.class);
		form = tester.newFormTester("form");
		form.setValue("municipioInputPanel:nomeMunicipio", "MUNICIPIO TESTE");
		form.setValue("municipioInputPanel:uf", "TO");
		form.setValue("municipioInputPanel:codIBGE", "9999999");
		form.submit();

		tester.assertNoErrorMessage();
		tester.assertInfoMessages(new String[] { "Município criado com sucesso" });
	}
	
}
