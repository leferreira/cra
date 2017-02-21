package br.com.ieptbto.cra.webpage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.page.login.LoginPage;
import br.com.ieptbto.cra.security.UserSession;

/**
 * 
 * @author Leandro
 * 
 * @param <T>
 */
public abstract class AbstractWebPage<T extends AbstractEntidade<?>> extends WebPage implements IHeaderContributor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** * Wicket-ID do feedback panel. */
	protected static final String WID_FEEDBACK = "feedback";

	/**
	 * Construtor padrao.
	 */
	public AbstractWebPage() {
		super();
		carregarComponentes();
	}

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            See Component
	 * @see Component#Component(String, IModel)
	 */
	public AbstractWebPage(IModel<?> model) {
		super(model);
		carregarComponentes();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
	}

	protected void carregarComponentes() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserSession<Usuario> getSession() {
		return (UserSession<Usuario>) super.getSession();
	}

	/**
	 * Retorna o usuario corrente.
	 * 
	 * @return {@link Colaborador}
	 */
	public Usuario getUser() {
		Usuario userSession = getSession().getUser();
		if (userSession == null) {
			getSession().invalidateNow();
			setResponsePage(LoginPage.class);
		}
		return userSession;
	}

	/**
	 * Recupera o {@link FeedbackPanel} da pagina.
	 * 
	 * @return {@link FeedbackPanel}
	 */
	public FeedbackPanel getFeedbackPanel() {
		return (FeedbackPanel) get(WID_FEEDBACK);
	}
}