package br.com.ieptbto.cra.page.usuario;

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
//	private final String loginLinkPath = "anonymPanel:url_login";
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
		 tester.assertComponent("formUsuario:usuarioInputPanel:nome",TextField.class);
		 tester.assertNoErrorMessage();
	}
//
//	@Test
//	public void testInsertUserFail() {
//	}
//
//	@Test
//	public void testInsertUserSuccessful() {
//	}

}
