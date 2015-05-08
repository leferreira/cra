package br.com.ieptbto.cra.page.usuario;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;


/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class ListaUsuarioPage extends BasePage<Usuario> {

	@SpringBean
	private UsuarioMediator usuarioMediator;

	private final Usuario usuario;

	@SuppressWarnings("rawtypes")
	public ListaUsuarioPage() {
		super();
		usuario = new Usuario();
		add(new Link("botaoNovo") {
            /***/
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(new IncluirUsuarioPage());
            }
        });
		add(carregarListaUsuario());
	}

	@SuppressWarnings("rawtypes")
	private ListView<Usuario> carregarListaUsuario(){
		return new ListView<Usuario>("listViewUsuario", buscarUsuarios()) {
			@Override
			protected void populateItem(ListItem<Usuario> item) {
				final Usuario usuarioLista = item.getModelObject();
				
				Link linkAlterar = new Link("linkAlterar") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new IncluirUsuarioPage(usuarioLista));
		            }
		        };
		        linkAlterar.add(new Label("nomeUsuario", usuarioLista.getNome()));
		        item.add(linkAlterar);
		        
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
			}
		};
	}

	public IModel<List<Usuario>> buscarUsuarios() {
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
