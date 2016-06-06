package br.com.ieptbto.cra.page.instituicao;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.base.BaseTest;

public class IncluirInstituicaoPageTest extends BaseTest{

	@Before
	public void setUp() {
		super.setUp();
		logar();
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCarregarIncluirInstituicaoPage() {
		tester.startPage(IncluirInstituicaoPage.class);
		form = tester.newFormTester("form");
		tester.assertComponent("form:instituicaoInputPanel:nomeFantasia", TextField.class);
		tester.assertComponent("form:instituicaoInputPanel:razaoSocial", TextField.class);
		tester.assertComponent("form:instituicaoInputPanel:cnpj", TextField.class);
		tester.assertComponent("form:instituicaoInputPanel:codCompensacao", TextField.class);
		tester.assertComponent("form:instituicaoInputPanel:email", TextField.class);
		tester.assertComponent("form:instituicaoInputPanel:contato", TextField.class);
		tester.assertComponent("form:instituicaoInputPanel:endereco", TextArea.class);
		tester.assertComponent("form:instituicaoInputPanel:responsavel", TextField.class);
		tester.assertComponent("form:instituicaoInputPanel:status", Component.class);
		tester.assertComponent("form:instituicaoInputPanel:tipoInstituicao", Component.class);
		tester.assertNoErrorMessage();
	}
	
	/**
	 * Teste falho devido a instituicao e o cnpj já existir e serem unicos
	 * */
	@Test
	public void testInsertInstituicaoFail() {
		tester.startPage(IncluirInstituicaoPage.class);
		form = tester.newFormTester("form");
		form.setValue("instituicaoInputPanel:nomeFantasia", "CRA");
		form.setValue("instituicaoInputPanel:cnpj", "123");
		form.setValue("instituicaoInputPanel:email", "e@mail.com");
		form.setValue("instituicaoInputPanel:contato", "1234");
		form.setValue("instituicaoInputPanel:status", "Ativo");
		form.setValue("instituicaoInputPanel:tipoInstituicao", "0");
		form.submit();

		tester.assertErrorMessages(new String[] { "Instituição não criada, pois já existe!" });
		tester.assertNoInfoMessage();
	}
	
	@Test
	public void testInsertInstituicaoSuccessful() {
		tester.startPage(IncluirInstituicaoPage.class);
		form = tester.newFormTester("form");
		form.setValue("instituicaoInputPanel:nomeFantasia", "INSTITUICAO TESTE");
		form.setValue("instituicaoInputPanel:razaoSocial", "Instituicao Teste");
		form.setValue("instituicaoInputPanel:cnpj", "99.999.999/9999-99");
		form.setValue("instituicaoInputPanel:codCompensacao", "000");
		form.setValue("instituicaoInputPanel:email", "teste@mail.com");
		form.setValue("instituicaoInputPanel:contato", "1234");
		form.setValue("instituicaoInputPanel:endereco", "AV. Teste");
		form.setValue("instituicaoInputPanel:responsavel", "TESTE");
		form.setValue("instituicaoInputPanel:status", "Ativo");
		form.setValue("instituicaoInputPanel:tipoInstituicao", "0");
		form.submit();

		tester.assertNoErrorMessage();
		tester.assertInfoMessages(new String[] { "Instituição criada com sucesso!" });
	}
}
