package br.com.ieptbto.cra.page.tipoInstituicao;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.mediator.PermissaoEnvioMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@SuppressWarnings("serial")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN,
		CraRoles.SUPER, })
public class ListaTipoInstituicaoPage extends BasePage<TipoInstituicao> {

	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;
	@SpringBean
	PermissaoEnvioMediator permissaoMediator;

	private final TipoInstituicao tipoInstituicao;
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaTipo;
	private WebMarkupContainer dataTableTipo;
	private List<PermissaoEnvio> permissoes;
	private String todasPermissoes = "";

	public ListaTipoInstituicaoPage() {
		super();
		this.tipoInstituicao = new TipoInstituicao();
		adicionarCampos();
	}

	public void adicionarCampos() {
		divListRetorno = carregarTableTipoInstituicao();
		add(divListRetorno);
	}

	private WebMarkupContainer carregarTableTipoInstituicao() {
		divListaTipo = new WebMarkupContainer("divListView");
		dataTableTipo = new WebMarkupContainer("dataTableTipo");
		PageableListView<TipoInstituicao> listView = getListViewTipoInstituicao();
		dataTableTipo.setOutputMarkupId(true);
		dataTableTipo.add(listView);

		divListaTipo.add(dataTableTipo);
		return divListaTipo;
	}

	private PageableListView<TipoInstituicao> getListViewTipoInstituicao() {
		return new PageableListView<TipoInstituicao>("listViewTipoInstituicao",
				listaTiposInstituicao(), 10) {
			@Override
			protected void populateItem(ListItem<TipoInstituicao> item) {
				TipoInstituicao tipoLista = item.getModelObject();
				item.add(new Label("tipoInstituicao", tipoLista
						.getTipoInstituicao()));
				permissoes = permissaoMediator.permissoesPorTipoInstituicao(tipoLista);
				for (PermissaoEnvio p: permissoes){
					todasPermissoes += p.getTipoArquivo().getTipoArquivo().constante + " ";
				}
				item.add(new Label("permissaoEnvio", todasPermissoes));
				todasPermissoes = "";
				item.add(alterarTipo(tipoLista));
			}

			private Component alterarTipo(final TipoInstituicao t) {
				return new Link<TipoInstituicao>("alterarTipo") {

					@Override
					public void onClick() {
						setResponsePage(new IncluirTipoInstituicaoPage(t));
					}
				};
			}
		};
	}

	public IModel<List<TipoInstituicao>> listaTiposInstituicao() {
		return new LoadableDetachableModel<List<TipoInstituicao>>() {
			@Override
			protected List<TipoInstituicao> load() {
				List<TipoInstituicao> list = tipoInstituicaoMediator
						.listarTipos();
				return list;
			}
		};
	}

	@Override
	protected IModel<TipoInstituicao> getModel() {
		return new CompoundPropertyModel<TipoInstituicao>(tipoInstituicao);
	}
}
