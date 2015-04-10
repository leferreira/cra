package br.com.ieptbto.cra.page.titulo;

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

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;

public class ListaTitulosPage extends BasePage<Titulo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	private final Titulo titulo;
	private Instituicao portador;
	
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaTitulo;
	private WebMarkupContainer dataTableTitulo;
	
	public ListaTitulosPage(Titulo titulo) {
		super();
		this.titulo=titulo;
		adicionarCampos();
	}
	
	public void adicionarCampos() {
		divListRetorno = carregarDataTableTitulo();
		add(divListRetorno);
	}

	private WebMarkupContainer carregarDataTableTitulo() {
		divListaTitulo = new WebMarkupContainer("divListView");
		dataTableTitulo = new WebMarkupContainer("dataTableTitulo");
		PageableListView<Titulo> listView = getListViewTitulos();
		dataTableTitulo.setOutputMarkupId(true);
		dataTableTitulo.add(listView);

		divListaTitulo.add(dataTableTitulo);
		return divListaTitulo;
	}

	private PageableListView<Titulo> getListViewTitulos() {
		return new PageableListView<Titulo>("listViewTitulo",buscarTitulos(), 10) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Titulo> item) {
				Titulo tituloLista = item.getModelObject();
				portador = instituicaoMediator.getInstituicaoPorCodigoPortador(tituloLista.getCodigoPortador());
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));
				item.add(new Label("protocolo", tituloLista.getNumeroProtocoloCartorio()));
				item.add(new Label("portador", portador.getNomeFantasia()));
				item.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto()));
//				item.add(new Label("situacao", ));
				item.add(historicoTitulo(tituloLista));
			}

			private Component historicoTitulo(final Titulo t) {
				return new Link<Titulo>("historicoTitulo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new HistoricoPage(t));
					}
					
				};
			}

		};
	}
	
	public IModel<List<Titulo>> buscarTitulos() {
		return new LoadableDetachableModel<List<Titulo>>() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Titulo> load() {
				return tituloMediator.buscarListaTitulos(titulo, getUser());
			}
		};
	}

    
	@Override
	protected IModel<Titulo> getModel() {
		return new CompoundPropertyModel<Titulo>(titulo);
	}

}
