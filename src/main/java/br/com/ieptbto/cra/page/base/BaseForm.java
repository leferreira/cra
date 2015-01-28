package br.com.ieptbto.cra.page.base;

import static br.com.ieptbto.cra.util.MessageUtils._;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.app.CraApplication;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.security.UserSession;

public abstract class BaseForm<T> extends Form<T> {

	private static final long serialVersionUID = 1L;

	public BaseForm(String id) {
		super(id);
	}

	public BaseForm(String id, IModel<T> model) {
		super(id, model);
	}

	/**
	 * Get application specific UserSession
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UserSession<Usuario> getSession() {
		return (UserSession<Usuario>) Session.get();
	}

	/**
	 * Get application specific CraApplication
	 */
	public CraApplication getApp() {
		return (CraApplication) getApplication();
	}

	/**
	 * Translate and post error message in form
	 * 
	 * @param message
	 */
	public void transError(String message) {
		error(_(message).getString());
	}

	/**
	 * Translate and flash info message
	 * 
	 * @param message
	 */
	public void flashInfo(String message) {
		getSession().info(_(message).getString());
	}
}
