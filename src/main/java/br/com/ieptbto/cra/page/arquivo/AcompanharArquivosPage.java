package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@SuppressWarnings("unused")
public class AcompanharArquivosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(AcompanharArquivosPage.class);

	@SpringBean
	private RemessaMediator remessasMediator;
	@SpringBean
	private ArquivoMediator arquivoMediator;

	private Arquivo arquivo;
	private Usuario user;
	private Instituicao instituicao;
	private Form<Arquivo> formFilters;

	final CheckBox enviados = new CheckBox("enviados",
			new PropertyModel<Boolean>(this, "enviados"));
	final CheckBox recebidos = new CheckBox("recebidos",
			new PropertyModel<Boolean>(this, "recebidos"));
	final CheckBox aguardando = new CheckBox("aguardando",
			new PropertyModel<Boolean>(this, "aguardando"));
	final CheckBox b = new CheckBox("b", new PropertyModel<Boolean>(this, "b"));
	final CheckBox c = new CheckBox("c", new PropertyModel<Boolean>(this, "c"));
	final CheckBox r = new CheckBox("r", new PropertyModel<Boolean>(this, "r"));
	final CheckBox cp = new CheckBox("cp", new PropertyModel<Boolean>(this,
			"cp"));
	final CheckBox dp = new CheckBox("dp", new PropertyModel<Boolean>(this,
			"dp"));
	final CheckBox ac = new CheckBox("ac", new PropertyModel<Boolean>(this,
			"ac"));

	private TextField<Date> dataEnvio = null;
	private List<TipoArquivoEnum> enumLista = new ArrayList<TipoArquivoEnum>();

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
		form.add(enviados);
		form.add(recebidos);
		form.add(aguardando);
		form.add(b);
		form.add(c);
		form.add(r);
		form.add(cp);
		form.add(dp);
		form.add(ac);
	}

	public TextField<Date> campoDataEnvio() {
		return dataEnvio = new TextField<Date>("dataEnvio", new Model<Date>(
				null));
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
				buscarListaRemessas(), 10) {
			@Override
			protected void populateItem(ListItem<Remessa> item) {
				Remessa remessa = item.getModelObject();
				item.add(new Label("tipoArquivo", remessa.getArquivo()
						.getTipoArquivo().getTipoArquivo().constante));
				item.add(new Label("nomeArquivo", remessa.getArquivo()
						.getNomeArquivo()));
				item.add(new Label("dataEnvio", DataUtil.formatarData(remessa
						.getArquivo().getDataEnvio())));
				item.add(new Label("instituicao", remessa.getArquivo()
						.getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", remessa.getInstituicaoDestino()
						.getNomeFantasia()));

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
				return remessasMediator.buscarRemessa(instituicao);
			}
		};
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

}
