package br.com.ieptbto.cra.page.cartorio;

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

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@SuppressWarnings({ "serial"})
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class ListaCartorioPage extends BasePage<Instituicao> {

	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	private Instituicao cartorio;

	@SuppressWarnings("rawtypes")
	public ListaCartorioPage() {
		super();
		cartorio = new Instituicao();
		add(new Link("botaoNovo") {
            /***/
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(new IncluirCartorioPage());
            }
        });
		add(carregarListaCartorios());
	}

	@SuppressWarnings("rawtypes")
	private ListView<Instituicao> carregarListaCartorios(){
		return new ListView<Instituicao>("listViewCartorio", listInstituicoes()) {
			@Override
			protected void populateItem(ListItem<Instituicao> item) {
				final Instituicao instituicaoLista = item.getModelObject();
				
				Link linkAlterar = new Link("linkAlterar") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new IncluirCartorioPage(instituicaoLista));
		            }
		        };
		        linkAlterar.add(new Label("nomeFantasia", instituicaoLista.getNomeFantasia()));
		        item.add(linkAlterar);
		        
				item.add(new Label("municipio", instituicaoLista.getMunicipio().getNomeMunicipio()));
				item.add(new Label("responsavel", instituicaoLista.getResponsavel()));
				item.add(new Label("email", instituicaoLista.getEmail()));
				item.add(new Label("contato", instituicaoLista.getContato()));
				if (instituicaoLista.isSituacao()) {
					item.add(new Label("situacao", "Sim"));
				} else {
					item.add(new Label("situacao", "Não"));
				}
			}
		};
	}

	public IModel<List<Instituicao>> listInstituicoes() {
		return new LoadableDetachableModel<List<Instituicao>>() {
			@Override
			protected List<Instituicao> load() {
				return instituicaoMediator.listaDeCartorio();
			}
		};
	}

	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(cartorio);
	}

}
