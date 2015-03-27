package br.com.ieptbto.cra.page.municipio;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.page.base.BasePage;

@SuppressWarnings("serial")
public class ListaMunicipioPage extends BasePage<Municipio> {

	@SpringBean
	private MunicipioMediator municipioMediator;

	private final Municipio municipio;
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaMunicipio;
	private WebMarkupContainer dataTableMunicipio;

	public ListaMunicipioPage() {
		super();
		municipio = new Municipio();
		adicionarCampos();
	}

	public void adicionarCampos() {
		divListRetorno = carregarDataTableMunicipio();
		add(divListRetorno);
	}

	private WebMarkupContainer carregarDataTableMunicipio() {
		divListaMunicipio = new WebMarkupContainer("divListView");
		dataTableMunicipio = new WebMarkupContainer("dataTableMunicipio");
		PageableListView<Municipio> listView = getListViewMunicipio();
		dataTableMunicipio.setOutputMarkupId(true);
		dataTableMunicipio.add(listView);

		divListaMunicipio.add(dataTableMunicipio);
		return divListaMunicipio;
	}

	private PageableListView<Municipio> getListViewMunicipio() {
		return new PageableListView<Municipio>("listViewMunicipio",
				buscarMunicipios(), 10) {
			@Override
			protected void populateItem(ListItem<Municipio> item) {
				Municipio municipioLista = item.getModelObject();
				item.add(new Label("nomeMunicipio", municipioLista
						.getNomeMunicipio()));
				item.add(new Label("uf", municipioLista.getUf()));
				item.add(new Label("codIBGE", municipioLista.getCodigoIBGE()));

				item.add(detalharMunicipio(municipioLista));
//				item.add(desativarMunicipio(municipioLista));
			}

			private Component detalharMunicipio(final Municipio m) {
				return new Link<Municipio>("detalharMunicipio") {

					@Override
					public void onClick() {
						setResponsePage(new IncluirMunicipioPage(m));
					}
				};
			}

//			private Component desativarMunicipio(final Municipio m) {
//				return new Link<Municipio>("desativarMunicipio") {
//
//					@Override
//					public void onClick() {
//						m.setSituacao(false);
//						municipioMediator.alterarMunicipio(m);
//						info("Município desativado!");
//					}
//				};
//			}
		};

	}

	public IModel<List<Municipio>> buscarMunicipios() {
		return new LoadableDetachableModel<List<Municipio>>() {
			@Override
			protected List<Municipio> load() {
				List<Municipio> list = municipioMediator.listarTodos();
				return list;
			}
		};
	}

	@Override
	protected IModel<Municipio> getModel() {
		return new CompoundPropertyModel<Municipio>(municipio);
	}

}
