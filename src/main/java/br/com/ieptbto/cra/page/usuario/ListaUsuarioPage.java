package br.com.ieptbto.cra.page.usuario;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@SuppressWarnings("serial")

@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN,
		CraRoles.SUPER, })
public class ListaUsuarioPage extends BasePage<Usuario> {

	@SpringBean
	private UsuarioMediator usuarioMediator;

	private final Usuario usuario;
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaUsuario;
	private WebMarkupContainer dataTableUsuario;

	public ListaUsuarioPage() {
		super();
		usuario = new Usuario();
		adicionarCampos();
	}

	public void adicionarCampos() {
		divListRetorno = carregarDataTableUsuario();
		add(divListRetorno);
	}

	private WebMarkupContainer carregarDataTableUsuario() {
		divListaUsuario = new WebMarkupContainer("divListView");
		dataTableUsuario = new WebMarkupContainer("dataTableUsuario");
		PageableListView<Usuario> listView = getListViewUsuario();
		dataTableUsuario.setOutputMarkupId(true);
		dataTableUsuario.add(new PagingNavigator("navigation", listView));
		dataTableUsuario.add(listView);

		divListaUsuario.add(dataTableUsuario);
		return divListaUsuario;
	}

	private PageableListView<Usuario> getListViewUsuario() {
		return new PageableListView<Usuario>("listViewUsuario",
				listAllUsuarios(), 10) {
			@Override
			protected void populateItem(ListItem<Usuario> item) {
				Usuario usuarioLista = item.getModelObject();
				item.add(new Label("nomeUsuario", usuarioLista.getNome()));
				item.add(new Label("loginUsuario", usuarioLista.getLogin()));
				item.add(new Label("emailUsuario", usuarioLista.getEmail()));
				item.add(new Label("contato", usuarioLista.getContato()));
				item.add(new Label("instituicaoUsuario", usuarioLista
						.getInstituicao().getNomeFantasia()));
				if (usuarioLista.isStatus()){
					item.add(new Label("status", "Sim"));
				}else{
					item.add(new Label("status","Não" ));
				}

				item.add(detalharUsuario(usuarioLista));
				item.add(excluirUsuario(usuarioLista));
			}

			private Component detalharUsuario(final Usuario u) {
				return new Link<Usuario>("detalharUsuario") {

					@Override
					public void onClick() {
						setResponsePage(new IncluirUsuarioPage(u));
					}
				};
			}

			private Component excluirUsuario(final Usuario u) {
				return new Link<Usuario>("excluirUsuario") {

					@Override
					public void onClick() {
						Usuario corrente = getUser();
						if (corrente.equals(u)) {
							error("Não é possível desativar.O usuário está logado!");
						}else{
							u.setStatus(false);
							usuarioMediator.alterar(u);
							info("Usuario desativado!");
						}
					}
				};
			}
		};

	}

	public IModel<List<Usuario>> listAllUsuarios() {
		return new LoadableDetachableModel<List<Usuario>>() {
			@Override
			protected List<Usuario> load() {
				List<Usuario> list = usuarioMediator.listarTodos();
				return list;
			}
		};
	}

	@Override
	protected IModel<Usuario> getModel() {
		return new CompoundPropertyModel<Usuario>(usuario);
	}
}
