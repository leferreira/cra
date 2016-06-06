package br.com.ieptbto.cra.page.usuario;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.base.BaseTest;

public class IncluirUsuarioPageTest extends BaseTest {

	@Before
	public void setUp() {
		super.setUp();
		logar();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testCarregarIncluirUsuarioPage() {
		tester.startPage(IncluirUsuarioPage.class);
		form = tester.newFormTester("formUsuario");
		tester.assertComponent("formUsuario:usuarioInputPanel:nome", TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:login", TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:senha", TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:confirmarSenha", TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:email", TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:contato", TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:instituicao", Component.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:situacao", Component.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:grupoUsuario", Component.class);
		tester.assertNoErrorMessage();
	}

	/**
	 * Teste falho devido ao nome de usuário já existir e as senhas não coinsidirem
	 * */
	@Test
	public void testInsertUserFail() {
		tester.startPage(IncluirUsuarioPage.class);
		form = tester.newFormTester("formUsuario");
		form.setValue("usuarioInputPanel:nome", "Teste1");
		form.setValue("usuarioInputPanel:login", "teste");
		form.setValue("usuarioInputPanel:senha", "123");
		form.setValue("usuarioInputPanel:confirmarSenha", "1234"); // Senha diferente
		form.setValue("usuarioInputPanel:email", "e@mail.com");
		form.setValue("usuarioInputPanel:contato", "1234");
		form.setValue("usuarioInputPanel:situacao", "Ativo");
		form.setValue("usuarioInputPanel:instituicao", "0");
		form.setValue("usuarioInputPanel:grupoUsuario", "0");
		form.submit();

		// check errors
		tester.assertErrorMessages(new String[] { "As senhas não são iguais!" });
		tester.assertNoInfoMessage();
	}

	/**
	 * Teste de Sucesso do incluir usuário
	 * */
	@Test
	public void testInsertUserSuccessful() {
		tester.startPage(IncluirUsuarioPage.class);
		form = tester.newFormTester("formUsuario");
		form.setValue("usuarioInputPanel:nome", "TESTE USUARIO");
		form.setValue("usuarioInputPanel:login", "teste usuario");
		form.setValue("usuarioInputPanel:senha", "123456");
		form.setValue("usuarioInputPanel:confirmarSenha", "123456");
		form.setValue("usuarioInputPanel:email", "e@mail.com");
		form.setValue("usuarioInputPanel:contato", "1234");
		form.setValue("usuarioInputPanel:situacao", "Ativo");
		form.setValue("usuarioInputPanel:instituicao", "0");
		form.setValue("usuarioInputPanel:grupoUsuario", "0");
		form.submit();
		tester.assertNoErrorMessage();
		tester.assertInfoMessages(new String[] { "Usuário criado com sucesso" });
	}
}
