package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.util.DataUtil;


/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
public class HistoricoPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;
	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	private TituloRemessa titulo;
	
	private WebMarkupContainer divListRetorno;
	private WebMarkupContainer divListaHistorico;
	private WebMarkupContainer dataTableHistorico;

	public HistoricoPanel(String id, IModel<TituloRemessa> model,TituloRemessa titulo) {
		super(id, model);
		this.titulo = titulo;
		adicionarComponentes();
		divListRetorno = carregarHistorico();
		add(divListRetorno);
	}
	
	private WebMarkupContainer carregarHistorico() {
		divListaHistorico = new WebMarkupContainer("divListView");
		dataTableHistorico = new WebMarkupContainer("historicoTable");
		PageableListView<Historico> listView = getListViewHistorico();
		dataTableHistorico.setOutputMarkupId(true);
		dataTableHistorico.add(listView);

		divListaHistorico.add(dataTableHistorico);
		return divListaHistorico;
	}

	private PageableListView<Historico> getListViewHistorico() {
		return new PageableListView<Historico>("divListaHistorico", buscarHistorico(), 10) {
			/***/
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("rawtypes")
			@Override
			protected void populateItem(ListItem<Historico> item) {
				final Historico historico = item.getModelObject();
				
				Link linkArquivo = new Link("linkArquivo") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
		            	setResponsePage(new TitulosDoArquivoPage(historico.getRemessa()));  
		            }
		        };
		        linkArquivo.add(new Label("nomeArquivo", historico.getRemessa().getArquivo().getNomeArquivo()));
		        item.add(linkArquivo);
				item.add(new Label("dataOcorrencia", DataUtil.localDateTimeToString(historico.getDataOcorrencia())));
				item.add(new Label("usuarioAcao", historico.getUsuarioAcao().getNome()));
			}
		};
	}
	
	public IModel<List<Historico>> buscarHistorico() {
		return new LoadableDetachableModel<List<Historico>>() {
			/***/
			private static final long serialVersionUID = 1L;
			@Override
			protected List<Historico> load() {
				return tituloMediator.getHistoricoTitulo(titulo);
			}
		};
	}
	
	public TextField<String> numeroProtocoloCartorio() {
		return new TextField<String>("confirmacao.numeroProtocoloCartorio");
	}

	public TextField<String> dataProtocolo() {
		return new TextField<String>("confirmacao.dataProtocolo", new Model<String>(DataUtil.localDateToString(titulo.getConfirmacao().getDataProtocolo())));
	}

	public TextField<String> codigoCartorio() {
		return new TextField<String>("codigoCartorio");
	}

	public TextField<String> cartorio(){
		return new TextField<String>("remessa.instituicaoDestino.nomeFantasia");
	}

	public TextField<String> pracaProtesto() {
		return new TextField<String>("pracaProtesto");
	}

	 public TextField<String> status(){
		 return new TextField<String>("situacaoTitulo", new Model<String>(titulo.getSituacaoTitulo()));
	 }

	public TextField<String> dataRemessa(){
		return new TextField<String>("remessa.arquivo.dataEnvio", new Model<String>(DataUtil.localDateToString(titulo.getRemessa().getArquivo().getDataEnvio())));
	}
	
	public TextField<String> nomeSacadorVendedor() {
		return new TextField<String>("nomeSacadorVendedor");
	}

	public TextField<String> documentoSacador() {
		return new TextField<String>("documentoSacador");
	}

	public TextField<String> ufSacadorVendedor() {
		return new TextField<String>("ufSacadorVendedor");
	}

	public TextField<String> cepSacadorVendedor() {
		return new TextField<String>("cepSacadorVendedor");
	}

	public TextField<String> cidadeSacadorVendedor() {
		return new TextField<String>("cidadeSacadorVendedor");
	}

	public TextField<String> enderecoSacadorVendedor() {
		return new TextField<String>("enderecoSacadorVendedor");
	}

	public TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor");
	}

	public TextField<String> documentoDevedor() {
		return new TextField<String>("documentoDevedor");
	}

	public TextField<String> ufDevedor() {
		return new TextField<String>("ufDevedor");
	}

	public TextField<String> cepDevedor() {
		return new TextField<String>("cepDevedor");
	}

	public TextField<String> cidadeDevedor() {
		return new TextField<String>("cidadeDevedor");
	}

	public TextField<String> enderecoDevedor() {
		return new TextField<String>("enderecoDevedor");
	}

	public TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo");
	}

	public TextField<String> portador(){
		return new TextField<String>("remessa.arquivo.instituicaoEnvio.nomeFantasia", new Model<String>(titulo.getRemessa().getCabecalho().getNomePortador()));
	}

	 public TextField<String> agencia(){
	 return new TextField<String>("agencia", new Model<String>(titulo.getRemessa().getCabecalho().getAgenciaCentralizadora()));
	 }

	public TextField<String> nossoNumero() {
		return new TextField<String>("nossoNumero");
	}

	public TextField<String> especieTitulo() {
		return new TextField<String>("especieTitulo");
	}

	public TextField<String> dataEmissaoTitulo() {
		return new TextField<String>("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(titulo.getDataEmissaoTitulo())));
	}

	public TextField<String> dataVencimentoTitulo() {
		return new TextField<String>("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(titulo.getDataVencimentoTitulo())));
	}

	public TextField<String> valorTitulo() {
		return new TextField<String>("valorTitulo");
	}

	public TextField<String> saldoTitulo() {
		return new TextField<String>("saldoTitulo");
	}

	public TextField<String> valorCustaCartorio() {
		return new TextField<String>("valorCustaCartorio");
	}

	public TextField<String> valorGravacaoEletronica() {
		return new TextField<String>("valorGravacaoEletronica");
	}

	public TextField<String> valorCustasCartorioDistribuidor() {
		return new TextField<String>("valorCustasCartorioDistribuidor");
	}

	public TextField<String> valorDemaisDespesas() {
		return new TextField<String>("valorDemaisDespesas");
	}

	public TextField<String> nomeCedenteFavorecido() {
		return new TextField<String>("nomeCedenteFavorecido");
	}

	public TextField<String> agenciaCodigoCedente() {
		return new TextField<String>("agenciaCodigoCedente");
	}
	
	public TituloRemessa getTitulo() {
		return titulo;
	}

	public void adicionarComponentes() {
		add(numeroProtocoloCartorio());
		add(dataProtocolo());
		add(codigoCartorio());
		add(pracaProtesto());
		add(nomeSacadorVendedor());
		add(documentoSacador());
		add(ufSacadorVendedor());
		add(cepSacadorVendedor());
		add(cidadeSacadorVendedor());
		add(enderecoSacadorVendedor());
		add(nomeDevedor());
		add(documentoDevedor());
		add(ufDevedor());
		add(cepDevedor());
		add(cidadeDevedor());
		add(enderecoDevedor());
		add(numeroTitulo());
		add(dataRemessa());
		add(portador());
		add(cartorio());
		add(agencia());
		add(nossoNumero());
		add(especieTitulo());
		add(dataEmissaoTitulo());
		add(dataVencimentoTitulo());
		add(valorTitulo());
		add(saldoTitulo());
		add(valorCustaCartorio());
		add(valorGravacaoEletronica());
		add(valorCustasCartorioDistribuidor());
		add(valorDemaisDespesas());
		add(nomeCedenteFavorecido());
		add(agenciaCodigoCedente());
		add(status());
	}
}
