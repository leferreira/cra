package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class HistoricoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;
	
	private TituloRemessa tituloRemessa;

	@SpringBean
	TituloMediator tituloMediator;

	public HistoricoPage(TituloRemessa titulo){
		this.tituloRemessa = tituloMediator.carregarDadosHistoricoTitulo(titulo);
		adicionarCampos();
	}
	
	private void adicionarCampos() {
		add(getListViewHistorico());
		add(numeroProtocoloCartorio());
		add(dataProtocolo());
		add(codigoCartorio());
		add(pracaProtesto());
		add(cartorio());
		add(status());
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
	}
	
	private ListView<Historico> getListViewHistorico(){
		return new ListView<Historico>("divListaHistorico", buscarHistorico()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Historico> item) {
				final Historico historico = item.getModelObject();
				Link<Remessa> linkArquivo = new Link<Remessa>("linkArquivo") {
		            /***/
					private static final long serialVersionUID = 1L;

					@Override
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
				return tituloMediator.getHistoricoTitulo(tituloRemessa);
			}
		};
	}
	
	private TextField<String> numeroProtocoloCartorio() {
		TextField<String> campoNumeroProtocolo = new TextField<String>("confirmacao.numeroProtocoloCartorio", new Model<String>(StringUtils.EMPTY));
		if (tituloRemessa.getConfirmacao() != null){
			campoNumeroProtocolo = new TextField<String>("confirmacao.numeroProtocoloCartorio", new Model<String>(tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
		} 
		campoNumeroProtocolo.setEnabled(false);
		return campoNumeroProtocolo;
	}

	private TextField<String> dataProtocolo() {
		String dataProtocolo = StringUtils.EMPTY;
		if (tituloRemessa.getConfirmacao() != null){
			dataProtocolo = DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataProtocolo()); 
		}
		TextField<String> textField = new TextField<String>("confirmacao.dataProtocolo", new Model<String>(dataProtocolo));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> codigoCartorio() {
		TextField<String> textField = new TextField<String>("codigoCartorio", new Model<String>(tituloRemessa.getCodigoCartorio().toString()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> cartorio(){
		TextField<String> textField = new TextField<String>("remessa.instituicaoDestino.nomeFantasia", new Model<String>(StringUtils.EMPTY));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> pracaProtesto() {
		TextField<String> textField = new TextField<String>("pracaProtesto", new Model<String>(tituloRemessa.getPracaProtesto()));
		textField.setEnabled(false);
		return textField;
	}

	 private TextField<String> status(){
		TextField<String> textField = new TextField<String>("situacaoTitulo", new Model<String>(tituloRemessa.getSituacaoTitulo()));
		textField.setEnabled(false);
		return textField;
	 }

	 private TextField<String> numeroTitulo() {
		TextField<String> textField = new TextField<String>("numeroTitulo", new Model<String>(tituloRemessa.getNumeroTitulo()));
		textField.setEnabled(false);
		return textField;
	 }

 	private TextField<String> dataRemessa(){
 		TextField<String> textField = new TextField<String>("remessa.arquivo.dataEnvio", 
 				new Model<String>(DataUtil.localDateToString(tituloRemessa.getRemessa().getArquivo().getDataEnvio())));
		textField.setEnabled(false);
		return textField;
	}
	
 	private TextField<String> portador(){
 		TextField<String> textField = new TextField<String>("remessa.arquivo.instituicaoEnvio.nomeFantasia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getNomePortador()));
		textField.setEnabled(false);
		return textField;
 	}

	 private TextField<String> agencia(){
		TextField<String> textField = new TextField<String>("agencia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getAgenciaCentralizadora()));
		textField.setEnabled(false);
		return textField;
	 }
	
	private TextField<String> nossoNumero() {
		TextField<String> textField = new TextField<String>("nossoNumero", new Model<String>(tituloRemessa.getNossoNumero()));
		textField.setEnabled(false);
		return textField;
	}
	
	private TextField<String> especieTitulo() {
		TextField<String> textField = new TextField<String>("especieTitulo", new Model<String>(tituloRemessa.getEspecieTitulo()));
		textField.setEnabled(false);
		return textField;
	}
	
	private TextField<String> dataEmissaoTitulo() {
		TextField<String> textField = new TextField<String>("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataEmissaoTitulo())));
		textField.setEnabled(false);
		return textField;
	}
	
	private TextField<String> dataVencimentoTitulo() {
		TextField<String> textField = new TextField<String>("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataVencimentoTitulo())));
		textField.setEnabled(false);
		return textField;
	}
	
	public TextField<String> valorTitulo() {
		TextField<String> textField = new TextField<String>("valorTitulo", new Model<String>("R$ " + tituloRemessa.getValorTitulo().toString()));
		textField.setEnabled(false);
		return textField;
	}
	
	public TextField<String> saldoTitulo() {
		TextField<String> textField = new TextField<String>("saldoTitulo", new Model<String>("R$ " + tituloRemessa.getSaldoTitulo().toString()));
		textField.setEnabled(false);
		return textField;
	}
	
	public TextField<String> valorCustaCartorio() {
		TextField<String> textField = new TextField<String>("valorCustaCartorio", new Model<String>("R$ " + tituloRemessa.getValorCustaCartorio().toString()));
		textField.setEnabled(false);
		return textField;
	}
	
	public TextField<String> valorGravacaoEletronica() {
		TextField<String> textField = new TextField<String>("valorGravacaoEletronica", new Model<String>("R$ " + tituloRemessa.getValorGravacaoEletronica().toString()));
		textField.setEnabled(false);
		return textField;
	}
	
	public TextField<String> valorCustasCartorioDistribuidor() {
		TextField<String> textField = new TextField<String>("valorCustasCartorioDistribuidor", new Model<String>("R$ " + tituloRemessa.getValorCustasCartorioDistribuidor().toString()));
		textField.setEnabled(false);
		return textField;
	}
	
	public TextField<String> valorDemaisDespesas() {
		TextField<String> textField = new TextField<String>("valorDemaisDespesas", new Model<String>("R$ " + tituloRemessa.getValorDemaisDespesas().toString()));
		textField.setEnabled(false);
		return textField;
	}
	
	public TextField<String> nomeCedenteFavorecido() {
		TextField<String> textField = new TextField<String>("nomeCedenteFavorecido", new Model<String>(tituloRemessa.getNomeCedenteFavorecido()));
		textField.setEnabled(false);
		return textField;
	}
	
	public TextField<String> agenciaCodigoCedente() {
		TextField<String> textField = new TextField<String>("agenciaCodigoCedente", new Model<String>(tituloRemessa.getAgenciaCodigoCedente()));
		textField.setEnabled(false);
		return textField;
	}
	
	private TextField<String> nomeSacadorVendedor() {
		TextField<String> textField = new TextField<String>("nomeSacadorVendedor", new Model<String>(tituloRemessa.getNomeSacadorVendedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> documentoSacador() {
		TextField<String> textField = new TextField<String>("documentoSacador", new Model<String>(tituloRemessa.getDocumentoSacador()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> ufSacadorVendedor() {
		TextField<String> textField = new TextField<String>("ufSacadorVendedor", new Model<String>(tituloRemessa.getUfSacadorVendedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> cepSacadorVendedor() {
		TextField<String> textField = new TextField<String>("cepSacadorVendedor", new Model<String>(tituloRemessa.getCepSacadorVendedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> cidadeSacadorVendedor() {
		TextField<String> textField = new TextField<String>("cidadeSacadorVendedor", new Model<String>(tituloRemessa.getCidadeSacadorVendedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> enderecoSacadorVendedor() {
		TextField<String> textField = new TextField<String>("enderecoSacadorVendedor", new Model<String>(tituloRemessa.getEnderecoSacadorVendedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> nomeDevedor() {
		TextField<String> textField = new TextField<String>("nomeDevedor", new Model<String>(tituloRemessa.getNomeDevedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> documentoDevedor() {
		TextField<String> textField = new TextField<String>("documentoDevedor", new Model<String>(tituloRemessa.getDocumentoDevedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> ufDevedor() {
		TextField<String> textField = new TextField<String>("ufDevedor", new Model<String>(tituloRemessa.getUfDevedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> cepDevedor() {
		TextField<String> textField = new TextField<String>("cepDevedor", new Model<String>(tituloRemessa.getCepDevedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> cidadeDevedor() {
		TextField<String> textField = new TextField<String>("cidadeDevedor", new Model<String>(tituloRemessa.getCidadeDevedor()));
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> enderecoDevedor() {
		TextField<String> textField = new TextField<String>("enderecoDevedor", new Model<String>(tituloRemessa.getEnderecoDevedor()));
		textField.setEnabled(false);
		return textField;
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}