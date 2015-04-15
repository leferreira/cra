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
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;
@SuppressWarnings("rawtypes")
public class TitulosDoArquivoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private Remessa remessa;
	private List<TituloRemessa> titulos;
	
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaArquivo;
	private WebMarkupContainer dataTableTitulo;
	
	public TitulosDoArquivoPage(Remessa remessa) {
		this.titulos = tituloMediator.buscarTitulosPorArquivo(remessa);
		this.remessa = remessa;
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

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = TituloRemessa.class.cast(item.getModelObject());
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));
				item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				item.add(new Label("portador", tituloLista.getRemessa().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				
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
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}
	
	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				return getTitulos();
			}
		};
	}
	
	public TextField<String> nomeArquivo(){
		return new TextField<String>("nomeArquivo", new Model<String>(remessa.getArquivo().getNomeArquivo()));
	}
	
	public TextField<String> portador(){
		return new TextField<String>("nomePortador", new Model<String>(remessa.getCabecalho().getNomePortador()));
	}
	
	public TextField<String> dataEnvio(){
		return new TextField<String>("dataEnvio", new Model<String>(DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
	}
	
	public TextField<String> usuarioEnvio(){
		return new TextField<String>("usuarioEnvio", new Model<String>(remessa.getArquivo().getUsuarioEnvio().getNome()));
	}
	
	public TextField<String> tipoArquivo(){
		return new TextField<String>("tipo", new Model<String>(remessa.getArquivo().getTipoArquivo().getTipoArquivo().getLabel()));
	}
	
	public List<TituloRemessa> getTitulos() {
		return titulos;
	}
	
	public void setTitulos( List<TituloRemessa> titulos) {
		this.titulos = titulos;
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(remessa.getArquivo());
	}
}
