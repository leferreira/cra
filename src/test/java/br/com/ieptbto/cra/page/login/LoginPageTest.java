package br.com.ieptbto.cra.page.login;

import static org.junit.Assert.assertEquals;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.base.BaseTest;

/**
 * 
 * @author Lefer
 *
 */

@SuppressWarnings("unused")
public class LoginPageTest extends BaseTest {
	private final String loginLinkPath = "anonymPanel:url_login";
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
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testRenderMyPage() {
		// tester.startPage(LoginPage.class);
		tester.assertRenderedPage(LoginPage.class);
		tester.assertComponent("loginForm:login", RequiredTextField.class);
		tester.assertComponent("loginForm:senha", PasswordTextField.class);
	}

	@Test
	public void testLoginUserFailInvalideUser() {

		form.setValue("login", "1" + usuario.getLogin());
		form.setValue("senha", usuario.getSenha());
		form.submit();

		// check errors
		tester.assertErrorMessages(new String[] { "Login ou senha inválido(s)." });
		tester.assertNoInfoMessage();
	}

	@Test
	public void testLoginUserSuccessful() {

		form.setValue("login", USUARIO_TESTE);
		form.setValue("senha", SENHA_TESTE);
		form.submit();

		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		assertEquals(usuario, session.getUser());

		// tester.assertVisible(logoutLinkPath);

		// tester.assertLabel("userGreeting", "Guten Tag, test@test.test!");
	}
}
