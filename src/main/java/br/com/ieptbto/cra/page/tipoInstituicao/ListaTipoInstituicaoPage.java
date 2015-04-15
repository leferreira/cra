package br.com.ieptbto.cra.page.tipoInstituicao;

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

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, })
public class ListaTipoInstituicaoPage extends BasePage<TipoInstituicao> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;

	private final TipoInstituicao tipoInstituicao;

	public ListaTipoInstituicaoPage() {
		super();
		this.tipoInstituicao = new TipoInstituicao();
		add(carregarListaTipos());
	}

	@SuppressWarnings("rawtypes")
	private ListView<TipoInstituicao> carregarListaTipos(){
		return new ListView<TipoInstituicao>("listViewTipos", listaTiposInstituicao()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TipoInstituicao> item) {
				String todasPermissoes = "";
				final TipoInstituicao tipoLista = item.getModelObject();
				
				Link linkAlterar = new Link("linkAlterar") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new IncluirTipoInstituicaoPage(tipoLista));
		            }
		        };
		        linkAlterar.add(new Label("tipoInstituicao", tipoLista.getTipoInstituicao()));
		        item.add(linkAlterar);
		        
				List<PermissaoEnvio> permissoes = tipoInstituicaoMediator.permissoesPorTipoInstituicao(tipoLista);
				for (PermissaoEnvio p : permissoes) {
					todasPermissoes += p.getTipoArquivo().getTipoArquivo().getConstante() + " ";
				}
				item.add(new Label("permissaoEnvio", todasPermissoes));
				todasPermissoes = "";
			}
		};
	}

	public IModel<List<TipoInstituicao>> listaTiposInstituicao() {
		return new LoadableDetachableModel<List<TipoInstituicao>>() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TipoInstituicao> load() {
				List<TipoInstituicao> list = tipoInstituicaoMediator.listarTipos();
				return list;
			}
		};
	}

	@Override
	protected IModel<TipoInstituicao> getModel() {
		return new CompoundPropertyModel<TipoInstituicao>(tipoInstituicao);
	}
}
