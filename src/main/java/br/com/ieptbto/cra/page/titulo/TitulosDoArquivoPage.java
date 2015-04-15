package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

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
	private WebMarkupContainer divListaArquivo;
	private WebMarkupContainer dataTableTitulo;
	
	public TitulosDoArquivoPage(Arquivo arquivo) {
		this.arquivo = arquivo;
		divListRetorno = carregarDataTableTitulo();
		add(divListRetorno);
	}

	private WebMarkupContainer carregarDataTableTitulo() {
		divListaArquivo = new WebMarkupContainer("divListViewArquivo");
		dataTableTitulo = new WebMarkupContainer("dataTableTituloArquivo");
		PageableListView<TituloRemessa> listView = getListViewTitulos();
		dataTableTitulo.setOutputMarkupId(true);
		dataTableTitulo.add(listView);

		divListaArquivo.add(dataTableTitulo);
		divListaArquivo.add(nomeArquivo());
		divListaArquivo.add(portador());
		divListaArquivo.add(dataEnvio());
		divListaArquivo.add(tipoArquivo());
		divListaArquivo.add(usuarioEnvio());
		return divListaArquivo;
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
	
	public TextField<String> nomeArquivo(){
		return new TextField<String>("nomeArquivo", new Model<String>(arquivo.getNomeArquivo()));
	}
	
	public TextField<String> portador(){
		return new TextField<String>("remessas.cabecalho.nomePortador");
	}
	
	public TextField<String> dataEnvio(){
		return new TextField<String>("dataEnvio", new Model<String>(DataUtil.localDateToString(arquivo.getDataEnvio())));
	}
	
	public TextField<String> usuarioEnvio(){
		return new TextField<String>("usuarioEnvio", new Model<String>(arquivo.getUsuarioEnvio().getNome()));
	}
	
	public TextField<String> tipoArquivo(){
		return new TextField<String>("tipo", new Model<String>(arquivo.getTipoArquivo().getTipoArquivo().getLabel()));
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
