package br.com.ieptbto.cra.page.login;

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
	private final String formPath = "loginPanel:loginForm";

	private FormTester form;

	@Before
	public void setUp() {
		super.setUp();
		tester.startPage(LoginPage.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
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
}
