package br.com.ieptbto.cra.page.home;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.page.arquivo.EnviarArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	private static final long serialVersionUID = 1L;
	private Usuario usuario;

	public HomePage() {
		this.usuario = getUser();
		adicionarComponentes();
	}

	public HomePage(PageParameters parameters) {
		this.usuario = getUser();
		error(parameters.get("error"));
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(linkEnviarArquivo());
		add(centralAcoesPanel());
		add(informacoesCraPanel());
		add(arquivosPendentesPanel());
	}

	private Panel informacoesCraPanel() {
		return new InformacoesCraPanel("informacoesCraPanel", usuario);
	}

	private Panel centralAcoesPanel() {
		return new CentralAcoesPanel("centralAcoesPanel", usuario);
	}

	private Panel arquivosPendentesPanel() {
		return new ArquivosPendentesPanel("arquivosPendentesPanel", usuario);
	}

	private Link<Remessa> linkEnviarArquivo() {
		return new Link<Remessa>("linkArquivo") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new EnviarArquivoPage());
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}