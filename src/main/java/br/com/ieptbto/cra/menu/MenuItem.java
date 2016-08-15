package br.com.ieptbto.cra.menu;

import java.util.Set;

import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * 
 * 
 */
public class MenuItem extends WebMarkupContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construtor.
	 * 
	 * @param id
	 *            wicket id.
	 */
	public MenuItem(String id) {
		super(id);
	}

	/**
	 * Contr adiciona um MenuItem ao menu dando acesso a qualquer usuario
	 * autenticado.
	 * 
	 * @param id
	 *            id do novo item
	 * @return o MenuItem criado
	 */
	public MenuItem addItem(String id) {
		MenuItem item = new MenuItem(id);
		add(item);
		return item;
	}

	/**
	 * Contr  adiciona um MenuItem ao menu indicando os roles que podem
	 * visualizar o menu. Todos os demais roles nao tem acesso ao menu.
	 * 
	 * @param id
	 *            id do novo item
	 * @param roles
	 *            lista de roles autorizados para ver o menu
	 * @return o MenuItem criado
	 */
	public MenuItem addItem(String id, String... roles) {
		MenuItem item = new MenuItem(id);
		item.authorize(roles);
		add(item);
		return item;
	}

	/**
	 * Contra adiciona um MenuItem ao menu indicando os roles que podem
	 * visualizar o menu. Todos os demais roles nao tam acesso ao menu.
	 * 
	 * @param id
	 *            id do novo item
	 * @param userRoles
	 *            coleaao de roles autorizados para ver o menu
	 * @return o MenuItem criado
	 */
	public MenuItem addItem(String id, Set<String> userRoles) {
		MenuItem item = new MenuItem(id);
		item.authorize(userRoles);
		add(item);
		return item;
	}

	/**
	 * Inclui uma autorizaaao para visualizar o menu.
	 * 
	 * @param roles
	 *            Roles do sistema.
	 */
	public void authorize(String... roles) {
		for (String role : roles) {
			MetaDataRoleAuthorizationStrategy.authorize(this, RENDER, role);
		}
	}

	/**
	 * Retira autorizaaaes previamente incluadas com um dos matodos
	 * <tt>authorize()</tt>.
	 * 
	 * @param userRoles
	 *            Roles do sistema.
	 */
	public void authorize(Set<String> userRoles) {
		for (String role : userRoles) {
			MetaDataRoleAuthorizationStrategy.authorize(this, RENDER, role);
		}
	}

	/**
	 * Retira autorizaaaes previamente incluadas com um dos matodos
	 * <tt>authorize()</tt>.
	 * 
	 * @param roles
	 *            Roles do sistema.
	 */
	public void unauthorize(String... roles) {
		for (String role : roles) {
			MetaDataRoleAuthorizationStrategy.unauthorize(this, RENDER, role);
		}
	}

}
