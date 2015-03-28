package br.com.ieptbto.cra.page.cartorio;

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

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

@SuppressWarnings("serial")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, })
public class ListaCartorioPage extends BasePage<Instituicao> {

	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	private Instituicao cartorio;
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaInstituicao;
	private WebMarkupContainer dataTableInstituicao;

	public ListaCartorioPage() {
		super();
		cartorio = new Instituicao();
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
		dataTableInstituicao.add(listView);

		divListaInstituicao.add(dataTableInstituicao);
		return divListaInstituicao;
	}

	private PageableListView<Instituicao> getListViewInstituicao() {
		return new PageableListView<Instituicao>("listViewInstituicao", listInstituicoes(), 10) {
			@Override
			protected void populateItem(ListItem<Instituicao> item) {
				Instituicao instituicaoLista = item.getModelObject();
				item.add(new Label("nomeFantasia", instituicaoLista.getNomeFantasia()));
				item.add(new Label("municipio", instituicaoLista.getMunicipio().getNomeMunicipio()));
				item.add(new Label("responsavel", instituicaoLista.getResponsavel()));
				item.add(new Label("email", instituicaoLista.getEmail()));
				item.add(new Label("contato", instituicaoLista.getContato()));
				if (instituicaoLista.isSituacao()) {
					item.add(new Label("situacao", "Sim"));
				} else {
					item.add(new Label("situacao", "Não"));
				}

				item.add(detalharCartorio(instituicaoLista));
				item.add(desativarCartorio(instituicaoLista));
			}

			private Component detalharCartorio(final Instituicao cartorio) {
				return new Link<Instituicao>("detalharCartorio") {
					@Override
					public void onClick() {
						setResponsePage(new IncluirCartorioPage(cartorio));
					}
				};
			}

			private Component desativarCartorio(final Instituicao cartorio) {
				return new Link<Instituicao>("desativarCartorio") {
					@Override
					public void onClick() {
						cartorio.setSituacao(false);
						instituicaoMediator.alterar(cartorio);
					}
				};
			}
		};
	}

	public IModel<List<Instituicao>> listInstituicoes() {
		return new LoadableDetachableModel<List<Instituicao>>() {
			@Override
			protected List<Instituicao> load() {
				List<Instituicao> list = instituicaoMediator.listaDeCartorio();
				return list;
			}
		};
	}

	@Override
	protected IModel<Instituicao> getModel() {
		return new CompoundPropertyModel<Instituicao>(cartorio);
	}

}
