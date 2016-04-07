package br.com.ieptbto.cra.page.municipio;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class ListaMunicipioPage extends BasePage<Municipio> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	MunicipioMediator municipioMediator;

	private Municipio municipio;

	public ListaMunicipioPage() {
		this.municipio = new Municipio();
		adicionarComponentes();
	}

	public ListaMunicipioPage(String mensagem) {
		this.municipio = new Municipio();
		info(mensagem);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		botaoNovoMunicipio();
		listaMunicipio();

	}

	private void botaoNovoMunicipio() {
		add(new Link<Municipio>("botaoNovo") {

			/***/
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(new IncluirMunicipioPage());
			}
		});
	}

	private void listaMunicipio() {
		add(new ListView<Municipio>("listViewMunicipio", buscarMunicipios()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Municipio> item) {
				final Municipio municipioLista = item.getModelObject();
				Link<Municipio> linkAlterar = new Link<Municipio>("linkAlterar") {

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
				item.add(new Label("inicioCep", municipioLista.getFaixaInicialCep()));
				item.add(new Label("finalCep", municipioLista.getFaixaFinalCep()));
			}
		});
	}

	public IModel<List<Municipio>> buscarMunicipios() {
		return new LoadableDetachableModel<List<Municipio>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Municipio> load() {
				return municipioMediator.listarTodos();
			}
		};
	}

	@Override
	protected IModel<Municipio> getModel() {
		return new CompoundPropertyModel<Municipio>(municipio);
	}

}
