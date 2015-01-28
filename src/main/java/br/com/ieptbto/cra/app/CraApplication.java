/**
 * 
 */
package br.com.ieptbto.cra.app;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import br.com.ieptbto.cra.security.ISecureApplication;
import br.com.ieptbto.cra.webpage.AbstractWebPage;

/**
 * @author Lefer
 *
 */
public class CraApplication extends WebApplication implements ISecureApplication, IWebApplication {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Component createMenuSistema(AbstractWebPage<?> page, String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTituloSistema(AbstractWebPage<?> page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Page> getLoginPage() {
		// TODO Auto-generated method stub
		return null;
	}

}
