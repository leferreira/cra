package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
public class AcompanharArquivosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AcompanharArquivosPage.class);

	@SpringBean
	private RemessaMediator remessasMediator;

	private Arquivo arquivo;
	private Form<Arquivo> formFilters;

	private String novaData = "";
	private TextField<LocalDate> dataEnvio;
	private ArrayList<String> tiposSelect = new ArrayList<String>();
	private ArrayList<String> statusSelect = new ArrayList<String>();
	private WebMarkupContainer divTable;
	private WebMarkupContainer divArquivos;
	private WebMarkupContainer tabelaArquivos;

	public AcompanharArquivosPage() {
		this.arquivo = new Arquivo();

		formFilters = new Form<Arquivo>("form", getModel()) {
			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				try { 

					if (dataEnvio.getDefaultModelObject() != null)
						novaData = dataEnvio.getDefaultModelObject().toString();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		};

		adicionarFiltros(formFilters);
		divTable = adicionarTableArquivos();
		add(divTable);
		formFilters.add(new Button("botaoEnviar"));
		add(formFilters);
	}

	/**
	 * Adicionando os campos de filtros
	 * */
	private void adicionarFiltros(Form<Arquivo> form) {
		form.add(campoDataEnvio());
		form.add(comboTipoArquivos());
		form.add(comboStatus());
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
		return new PageableListView<Remessa>("listViewArquivos",
				buscarListaRemessas(), 50) {
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
				return remessasMediator.buscarRemessasComFiltros(getUser().getInstituicao(), tiposSelect, statusSelect, novaData);
			}
		};
	}

	public Component comboTipoArquivos() {
		List<String> choices = new ArrayList<String>();
		List<TipoArquivoEnum> enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choices.add(tipo.constante);
		}
		return new CheckBoxMultipleChoice<String>("tipoArquivos",new Model<ArrayList<String>>(tiposSelect), choices);
	}

	public Component comboStatus() {
		List<String> choices = new ArrayList<String>(Arrays.asList(new String[] { "Aguardando", "Enviado","Recebido" }));
		return new CheckBoxMultipleChoice<String>("statusArquivos",	new Model<ArrayList<String>>(statusSelect), choices);
	}
	
	public TextField<LocalDate> campoDataEnvio() {
		return dataEnvio = new TextField<LocalDate>("dataEnvio", new Model<LocalDate>());
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}