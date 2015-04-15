package br.com.ieptbto.cra.page.municipio;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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

	public ListaMunicipioPage() {
		super();
		this.municipio = new Municipio();
		add(carregarListaMunicipio());
	}

	@SuppressWarnings("rawtypes")
	private ListView<Municipio> carregarListaMunicipio(){
		return new ListView<Municipio>("listViewMunicipio", buscarMunicipios()) {
			@Override
			protected void populateItem(ListItem<Municipio> item) {
				final Municipio municipioLista = item.getModelObject();
				
				Link linkAlterar = new Link("linkAlterar") {
					/***/
					private static final long serialVersionUID = 1L;
					
					public void onClick() {
						setResponsePage(new IncluirMunicipioPage(municipioLista));
					}
				};
				linkAlterar.add(new Label("nomeMunicipio", municipioLista.getNomeMunicipio()));
				item.add(linkAlterar);
				
				item.add(new Label("uf", municipioLista.getUf()));
				item.add(new Label("codIBGE", municipioLista.getCodigoIBGE()));
			}
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
