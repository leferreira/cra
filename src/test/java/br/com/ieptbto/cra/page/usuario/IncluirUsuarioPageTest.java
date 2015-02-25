package br.com.ieptbto.cra.page.usuario;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.base.BaseTest;
import br.com.ieptbto.cra.page.login.LoginPage;

@SuppressWarnings("unused")
public class IncluirUsuarioPageTest extends BaseTest {
	// private final String loginLinkPath = "anonymPanel:url_login";
	private final String logoutLinkPath = "url_logout";
	private final String formPath = "loginForm";

	private FormTester form;

	@Before
	public void setUp() {
		super.setUp();
		tester.startPage(LoginPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
		form = tester.newFormTester(formPath);
		form.setValue("login", USUARIO_TESTE);
		form.setValue("senha", SENHA_TESTE);
		form.submit();
		tester.assertVisible(logoutLinkPath);
		tester.assertLabel("userGreeting", "Olá Teste");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testCarregarIncluirUsuarioPage() {
		tester.startPage(IncluirUsuarioPage.class);
		form = tester.newFormTester("formUsuario");
		tester.assertComponent("formUsuario:usuarioInputPanel:nome",
				TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:login",
				TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:senha",
				TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:email",
				TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:contato",
				TextField.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:instituicao",
				Component.class);
		tester.assertComponent("formUsuario:usuarioInputPanel:grupoUsuario",
				Component.class);
		tester.assertNoErrorMessage();
	}

	/**
	 * Teste falho devido ao nome de usuário já existir e o email não segue o
	 * padrão.
	 * */
	@Test
	public void testInsertUserFail() {
		tester.startPage(IncluirUsuarioPage.class);
		form = tester.newFormTester("formUsuario");
		form.setValue("usuarioInputPanel:nome", "Teste");
		form.setValue("usuarioInputPanel:login", "teste");
		form.setValue("usuarioInputPanel:senha", "123");
		form.setValue("usuarioInputPanel:email", "e@mail.com"); 
		form.setValue("usuarioInputPanel:contato", "1234");
		form.setValue("usuarioInputPanel:instituicao", "0");
		form.setValue("usuarioInputPanel:grupoUsuario", "0");
		form.submit();
		
		// check errors
		tester.assertErrorMessages(new String[] { "Usuário não criado. O login já existe!" });
		tester.assertNoInfoMessage();
	}

	@Test
	public void testInsertUserSuccessful() {
		tester.startPage(IncluirUsuarioPage.class);
		form = tester.newFormTester("formUsuario");
		form.setValue("usuarioInputPanel:nome", "Teste");
		form.setValue("usuarioInputPanel:login", "test");
		form.setValue("usuarioInputPanel:senha", "123");
		form.setValue("usuarioInputPanel:email", "e@mail.com");
		form.setValue("usuarioInputPanel:contato", "1234");
		form.setValue("usuarioInputPanel:instituicao", "0");
		form.setValue("usuarioInputPanel:grupoUsuario", "0");
		form.submit();
		tester.assertErrorMessages();
		tester.assertInfoMessages();

	}
}
