package br.com.ieptbto.cra.page.cartorio;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.base.BaseTest;

public class IncluirCartorioPageTest extends BaseTest {

	@Before
	public void setUp() {
		super.setUp();
		logar();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testCarregarIncluirCartorioPage() {
		tester.startPage(IncluirCartorioPage.class);
		form = tester.newFormTester("form");
		tester.assertComponent("form:cartorioInputPanel:nomeFantasia", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:razaoSocial", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:cnpj", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:email", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:contato", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:favorecido", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:bancoContaCorrente", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:numContaCorrente", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:agenciaContaCorrente", TextField.class);

		tester.assertComponent("form:cartorioInputPanel:endereco", TextArea.class);
		tester.assertComponent("form:cartorioInputPanel:responsavel", TextField.class);
		tester.assertComponent("form:cartorioInputPanel:status", Component.class);
		tester.assertComponent("form:cartorioInputPanel:comarcaCartorio", Component.class);
		tester.assertNoErrorMessage();
	}

	/**
	 * Teste falho devido o cnpj já existir e ser unicos
	 */
	@Test
	public void testInsertCartorioFail() {
		tester.startPage(IncluirCartorioPage.class);
		form = tester.newFormTester("form");
		form.setValue("cartorioInputPanel:nomeFantasia", "CARTÓRIO TESTE");
		form.setValue("cartorioInputPanel:razaoSocial", "Cartorio Teste");
		form.setValue("cartorioInputPanel:cnpj", "123");
		form.setValue("cartorioInputPanel:email", "cartorio@mail.com");
		form.setValue("cartorioInputPanel:favorecido", "Teste Favorecido");
		form.setValue("cartorioInputPanel:bancoContaCorrente", "Banco Cartório");
		form.setValue("cartorioInputPanel:numContaCorrente", "1234");
		form.setValue("cartorioInputPanel:agenciaContaCorrente", "1234");
		form.setValue("cartorioInputPanel:endereco", "AV. Teste");
		form.setValue("cartorioInputPanel:responsavel", "TESTE");
		form.setValue("cartorioInputPanel:comarcaCartorio", "0");
		form.setValue("cartorioInputPanel:status", "Ativo");
		form.submit();

		tester.assertErrorMessages(new String[] { "Cartório não criado, pois já existe!" });
		tester.assertNoInfoMessage();
	}

	@Test
	public void testInsertCartorioSuccessful() {
		tester.startPage(IncluirCartorioPage.class);
		form = tester.newFormTester("form");
		form.setValue("cartorioInputPanel:nomeFantasia", "CARTÓRIO TESTE");
		form.setValue("cartorioInputPanel:razaoSocial", "Cartorio Teste");
		form.setValue("cartorioInputPanel:cnpj", "123456");
		form.setValue("cartorioInputPanel:email", "cartorio@mail.com");
		form.setValue("cartorioInputPanel:favorecido", "Teste Favorecido");
		form.setValue("cartorioInputPanel:bancoContaCorrente", "Banco Cartório");
		form.setValue("cartorioInputPanel:numContaCorrente", "1234");
		form.setValue("cartorioInputPanel:agenciaContaCorrente", "1234");
		form.setValue("cartorioInputPanel:endereco", "AV. Teste");
		form.setValue("cartorioInputPanel:responsavel", "TESTE");
		form.setValue("cartorioInputPanel:comarcaCartorio", "0");
		form.setValue("cartorioInputPanel:status", "Ativo");
		form.submit();

		tester.assertNoErrorMessage();
		tester.assertInfoMessages(new String[] { "Cartório cadastrado com sucesso!" });
	}
}
