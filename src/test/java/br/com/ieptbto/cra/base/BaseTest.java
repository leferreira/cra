package br.com.ieptbto.cra.base;

import java.util.Locale;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import br.com.ieptbto.cra.app.CraApplication;
import br.com.ieptbto.cra.dao.UsuarioDAO;
import br.com.ieptbto.cra.entidade.GrupoUsuario;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.page.base.HomePage;
import br.com.ieptbto.cra.page.login.LoginPage;
import br.com.ieptbto.cra.security.UserSession;

/**
 * 
 * @author Lefer
 *
 */
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class BaseTest extends AbstractJUnit4SpringContextTests {
	public static final String USUARIO_TESTE = "teste";
	public static final String SENHA_TESTE = "teste1234";
	protected WicketTester tester;
	protected UserSession<Usuario> session;
	protected Usuario usuario;

	@Autowired
	protected UsuarioDAO usuarioDAO;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		CraApplication app = new CraApplication() {
			@Override
			protected void initSpring() {
				getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
			};
		};
		tester = new WicketTester(app);
		session = (UserSession<Usuario>) tester.getSession();
		session.setLocale(new Locale("pt", "BR"));

		// create test user
		usuario = new Usuario();
		usuario.setLogin(USUARIO_TESTE);
		usuario.setSenha(SENHA_TESTE);
		usuario.setGrupoUsuario(getGrupo("Administrador", Roles.ADMIN));
	}

	private GrupoUsuario getGrupo(String nomeGrupo, String roles) {
		GrupoUsuario grupo = new GrupoUsuario();
		grupo.setGrupo(nomeGrupo);
		grupo.setRoles(new Roles(roles));
		return grupo;
	}

	@Test
	public void testInit() {
		tester.startPage(HomePage.class);
		tester.assertRenderedPage(LoginPage.class);
	}
}
