package br.com.ieptbto.cra.page.base;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.app.IWebApplication;
import br.com.ieptbto.cra.component.CustomFeedbackPanel;
import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.page.login.LoginPage;
import br.com.ieptbto.cra.page.usuario.PerfilUsuarioPage;
import br.com.ieptbto.cra.webpage.AbstractWebPage;

/**
 * 
 * @author Leandro
 * 
 * @param <T
 *            extends AbstractEntidade<T>>
 */
public abstract class BasePage<T extends AbstractEntidade<T>> extends AbstractWebPage<T> {

	/****/
	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(BasePage.class);
	protected static final String WID_MENU = "menu";
	private static final String SAUDACAO = "userGreeting";
	private static final String URL_LOGOUT = "url_logout";

	public BasePage(IModel<?> model) {
		super(model);
		addComponentesBase();

	}

	public BasePage() {
		super();
		addComponentesBase();
	}

	private void addComponentesBase() {
		String nome = null;
		if (StringUtils.isNotBlank(getUser().getNome())) {
			if (getUser().getNome().length() > 15) {
				nome = getUser().getNome().substring(0, 14);
			} else
				nome = getUser().getNome();
		}

		add(new Label(SAUDACAO, nome));

		addFeedbackPanel();
		addLogout();
		adicionarMenu();
		adicionarLinkProfileUser();
		adicionarTrocaSenha();
	}

	private void adicionarLinkProfileUser() {
		add(new Link<Usuario>("configuracoesUsuario") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new PerfilUsuarioPage(getUser()));
			}
		});
	}

	private void adicionarTrocaSenha() {
		// add(new TrocaSenhaPanel("trocaSenhaPanel", getUser()));
	}

	private void addLogout() {
		add(new Link<Void>(URL_LOGOUT) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				logger.info("O usuário <<" + getUser().getLogin() + ">> saiu do sistema.");
				getSession().invalidateNow();
				setResponsePage(LoginPage.class);
			}
		});
	}

	private void addFeedbackPanel() {
		CustomFeedbackPanel feedBackPanel = new CustomFeedbackPanel(WID_FEEDBACK);
		feedBackPanel.setOutputMarkupId(true);
		add(feedBackPanel);
	}

	private void adicionarMenu() {
		Component menu = createMenu(WID_MENU);
		if (menu == null) {
			menu = new WebMarkupContainer(WID_MENU);
		}
		menu.setRenderBodyOnly(true);
		add(menu);
	}

	protected Component createMenu(String containerId) {
		return getWicketApplication().createMenuSistema(this, containerId, getUser());
	}

	private IWebApplication getWicketApplication() {
		return (IWebApplication) getApplication();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	protected abstract void adicionarComponentes();

	protected abstract IModel<T> getModel();

}
