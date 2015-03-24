package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.page.base.BasePage;

/**
 * @author Thasso Araújo
 *
 */
/**
 * @author Thasso Araújo
 *
 */
/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@SuppressWarnings({ "unused" })
public class AcompanharArquivosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private ArquivoMediator arquivoMediator;
	
	private Arquivo arquivo;

	private Usuario user;
	private Instituicao instituicao;
	private Form<Arquivo> formFilters;
	
	private static final List<String> CHOISE_SITUACAO = Arrays.asList(new String[] { "Enviados", "Aguardando", "Recebidos" });
	
	private ArrayList<String> situacaoSelect = new ArrayList<String>();
	private ArrayList<String> tipoSelect = new ArrayList<String>();
	private TextField<Date> dataEnvio = null;
	private List<TipoArquivoEnum> enumLista = new ArrayList<TipoArquivoEnum>();
	private List<String> choicesTipos = new ArrayList<String>();
	
	private WebMarkupContainer divTable;
	private WebMarkupContainer divArquivos;
	private WebMarkupContainer tabelaArquivos;

	public AcompanharArquivosPage() {
		this.arquivo = new Arquivo();
		this.user = getUser();
		this.instituicao = getUser().getInstituicao();

		formFilters = new Form<Arquivo>("form", getModel()) {
			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				System.out.println(situacaoSelect.toString());
				System.out.println(tipoSelect.toString());
				System.out.println(dataEnvio.getDefaultModelObject().toString());
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
	private void adicionarFiltros(Form<?> form) {
		form.add(comboSituacoes());
		form.add(comboTiposArquivos());
		form.add(campoDataEnvio());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Component comboSituacoes() {
		return new CheckBoxMultipleChoice<String>("situacao", new Model(
				situacaoSelect), CHOISE_SITUACAO);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Component comboTiposArquivos() {
		enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choicesTipos.add(tipo.constante);
		}
		return new CheckBoxMultipleChoice<String>("tipos",
				new Model(tipoSelect), choicesTipos);
	}

	public TextField<Date> campoDataEnvio(){
		return dataEnvio = new TextField<Date>("dataEnvio", new Model<Date>(null));
	}
	
	/***
	 * Criando tabela de arquivos
	 * */
	private WebMarkupContainer adicionarTableArquivos() {
		divArquivos = new WebMarkupContainer("divListView");
		tabelaArquivos = new WebMarkupContainer("tabelaArquivos");
		PageableListView<Arquivo> listView = getListaViewArquivos();
		tabelaArquivos.setOutputMarkupId(true);
		tabelaArquivos.add(listView);
		divArquivos.add(tabelaArquivos);
		divArquivos.setVisible(true);

		return divArquivos;
	}
	
	@SuppressWarnings("serial")
	private PageableListView<Arquivo> getListaViewArquivos() {
		return new PageableListView<Arquivo>("listViewArquivos", buscarListaArquivos(), 10) {
			@Override
			protected void populateItem(ListItem<Arquivo> item) {
				Arquivo solicitacaoDeServico = item.getModelObject();
				item.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
				item.add(new Label("dataEnvio", arquivo.getDataEnvio()));
				item.add(new Label("instituicao", arquivo.getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("listViewArquivos", arquivo.getTipoArquivo().getTipoArquivo().constante));
			}
		};
	}
	
	@SuppressWarnings("serial")
	private IModel<List<Arquivo>> buscarListaArquivos() {
		return new LoadableDetachableModel<List<Arquivo>>() {
			@Override
			protected List<Arquivo> load() {
//				List<Arquivo> lista = new ArrayList<Arquivo>();
//			try{
//				lista = arquivoMediator.buscarArquivos();
//				return lista;
//			} catch(NullPointerException)
//				error("Não foi encontrado arquivos!");
//			}
				return arquivoMediator.buscarArquivos();
			}
		};
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

}
