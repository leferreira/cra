package br.com.ieptbto.cra.page.instituicao;

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

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@SuppressWarnings("serial")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN,
		CraRoles.SUPER, })
public class ListaInstituicaoPage extends BasePage<Instituicao> {

	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	
	private final Instituicao instituicao;
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaInstituicao;
	private WebMarkupContainer dataTableInstituicao;
	
	public ListaInstituicaoPage() {
		super();
		instituicao = new Instituicao();
		adicionarCampos();
	}

	public void adicionarCampos() {
		divListRetorno = carregarDataTableInstiuicao();
		add(divListRetorno);
	}
	
	private WebMarkupContainer carregarDataTableInstiuicao() {
		divListaInstituicao = new WebMarkupContainer("divListView");
		dataTableInstituicao = new WebMarkupContainer("dataTableInstituicao");
		PageableListView<Instituicao> listView = getListViewInstituicao();
		dataTableInstituicao.setOutputMarkupId(true);
		dataTableInstituicao.add(new PagingNavigator("navigation", listView));
		dataTableInstituicao.add(listView);

		divListaInstituicao.add(dataTableInstituicao);
		return divListaInstituicao;
	}
	
	private PageableListView<Instituicao> getListViewInstituicao() {
		return new PageableListView<Instituicao>("listViewInstituicao",
				listAllInstituicao(), 10) {
			@Override
			protected void populateItem(ListItem<Instituicao> item) {
				Instituicao instituicaoLista = item.getModelObject();
				item.add(new Label("instituicao", instituicaoLista
						.getInstituicao()));
				item.add(new Label("tipoInstituicao", instituicaoLista
						.getTipoInstituicao().getTipoInstituicao()));
				item.add(new Label("responsavel", instituicaoLista
						.getResponsavel()));
				item.add(new Label("contato", instituicaoLista.getContato()));
				if (instituicaoLista.isSituacao()) {
					item.add(new Label("situacao", "Sim"));
				} else {
					item.add(new Label("situacao", "Não"));
				}

				item.add(detalharInstituicao(instituicaoLista));
				item.add(desativarInstituicao(instituicaoLista));
			}

			private Component detalharInstituicao(final Instituicao instituicao) {
				return new Link<Instituicao>("detalharInstituicao") {
					@Override
					public void onClick() {
						setResponsePage(new IncluirInstituicaoPage(instituicao));
					}
				};
			}

			private Component desativarInstituicao(final Instituicao instituicao) {
				return new Link<Instituicao>("desativarInstituicao") {
					@Override
					public void onClick() {
						instituicao.setSituacao(false);
						instituicaoMediator.alterar(instituicao);
					}
				};
			}
		};
	}
	
	public IModel<List<Instituicao>> listAllInstituicao() {
		return new LoadableDetachableModel<List<Instituicao>>() {
			@Override
			protected List<Instituicao> load() {
				List<Instituicao> list = instituicaoMediator.listaDeInstituicao();
				return list;
			}
		};
	}
	
	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(instituicao);
	}
}
