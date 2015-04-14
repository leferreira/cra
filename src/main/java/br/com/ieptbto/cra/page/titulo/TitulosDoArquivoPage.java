package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;

public class TitulosDoArquivoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private Arquivo arquivo;
	private Instituicao portador;
	
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaTitulo;
	private WebMarkupContainer dataTableTitulo;
	
	public TitulosDoArquivoPage(Arquivo arquivo) {
		this.arquivo = arquivo;
		adicionarCampos();
	}

	public void adicionarCampos() {
		divListRetorno = carregarDataTableTitulo();
		add(divListRetorno);
//		add(nomeArquivo());
//		add(portador());
//		add(dataEnvio());
//		add(usuarioEnvio());
//		add(qtdTitulos());
	}

	private WebMarkupContainer carregarDataTableTitulo() {
		divListaTitulo = new WebMarkupContainer("divListViewArquivo");
		dataTableTitulo = new WebMarkupContainer("dataTableTituloArquivo");
		PageableListView<TituloRemessa> listView = getListViewTitulos();
		dataTableTitulo.setOutputMarkupId(true);
		dataTableTitulo.add(listView);

		divListaTitulo.add(dataTableTitulo);
		return divListaTitulo;
	}

	private PageableListView<TituloRemessa> getListViewTitulos() {
		return new PageableListView<TituloRemessa>("listViewTituloArquivo",buscarTitulos(), 10) {
			/***/
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("rawtypes")
			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();
				portador = instituicaoMediator.getInstituicaoPorCodigoPortador(tituloLista.getCodigoPortador());
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));
				item.add(new Label("protocolo", tituloLista.getNumeroProtocoloCartorio()));
				item.add(new Label("portador", portador.getNomeFantasia()));
				
				Link linkHistorico = new Link("linkHistorico") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
		            }
		        };
		        linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		        item.add(linkHistorico);
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto()));
			}
		};
	}
	
	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				return tituloMediator.buscarTitulosPorArquivo(arquivo);
			}
		};
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
