package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
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
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

public class ListaArquivosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private RemessaMediator remessaMediator;
	private final Arquivo arquivo;
	private Municipio municipio;
	private Instituicao portador;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private ArrayList<String> tiposDeArquivos = new ArrayList<String>();
	
	private WebMarkupContainer divTable;
	private WebMarkupContainer divArquivos;
	private WebMarkupContainer tabelaArquivos;
	
	public ListaArquivosPage(Arquivo arquivo, Municipio municipio,Instituicao portador, LocalDate dataInicio, LocalDate dataFim, ArrayList<String> tiposArquivo) {
		super();
		this.arquivo = arquivo;
		this.municipio = municipio;
		this.portador = portador;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.tiposDeArquivos = tiposArquivo;

		divTable = adicionarTableArquivos();
		add(divTable);
	}
	
	/***
	 * Criando tabela de remessas de arquivos
	 * */
	private WebMarkupContainer adicionarTableArquivos() {
		divArquivos = new WebMarkupContainer("divListView");
		tabelaArquivos = new WebMarkupContainer("tabelaArquivos");
		PageableListView<Remessa> listView = getListaViewArquivos();
		tabelaArquivos.setOutputMarkupId(true);
		tabelaArquivos.add(listView);
		divArquivos.add(tabelaArquivos);
		divArquivos.setVisible(true);

		return divArquivos;
	}
	
	@SuppressWarnings("serial")
	private PageableListView<Remessa> getListaViewArquivos() {
		return new PageableListView<Remessa>("listViewArquivos",buscarListaRemessas(), 10) {
			@Override
			protected void populateItem(ListItem<Remessa> item) {
				Remessa remessa = item.getModelObject();
				item.add(new Label("tipoArquivo", remessa.getArquivo().getTipoArquivo().getTipoArquivo().constante));
				item.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
				item.add(new Label("instituicao", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
				item.add(new Label("status", remessa.getArquivo().getStatusArquivo().getStatus()));
				item.add(downloadArquivo(remessa.getArquivo()));
			}

			private Component downloadArquivo(final Arquivo file) {
				return new Link<Arquivo>("downloadArquivo") {

					@Override
					public void onClick() {
					}
				};
			}
		};
	}
	
	/**
	 * Método que recebe os parametros e buscará os arquivos
	 * */
	@SuppressWarnings("serial")
	private IModel<List<Remessa>> buscarListaRemessas() {
		return new LoadableDetachableModel<List<Remessa>>() {
			@Override
			protected List<Remessa> load() {
				return remessaMediator.buscarArquivos(arquivo, municipio, portador, dataInicio, dataFim, tiposDeArquivos, getUser());
			}
		};
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

}
