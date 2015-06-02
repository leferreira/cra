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
import org.hibernate.Hibernate;

import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class HistoricoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	TituloMediator tituloMediator;
	private TituloRemessa tituloRemessa;

	public HistoricoPage(TituloRemessa titulo){
		Hibernate.initialize(titulo);
		this.tituloRemessa = titulo;
		add(getListViewHistorico());
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
	
	private ListView<Historico> getListViewHistorico(){
		return new ListView<Historico>("divListaHistorico", buscarHistorico()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Historico> item) {
				final Historico historico = item.getModelObject();
				Link linkArquivo = new Link("linkArquivo") {
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
		if (tituloRemessa.getConfirmacao() != null){
			return new TextField<String>("numeroProtocoloCartorio", new Model<String>(tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
		} else {
			return new TextField<String>("numeroProtocoloCartorio", new Model<String>(StringUtils.EMPTY));
		}
	}

	private TextField<String> dataProtocolo() {
		String dataProtocolo = StringUtils.EMPTY;
		Hibernate.initialize(tituloRemessa.getConfirmacao());
		if (tituloRemessa.getConfirmacao() != null){
			dataProtocolo = DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataProtocolo()); 
		}
		return new TextField<String>("confirmacao.dataProtocolo", new Model<String>(dataProtocolo));
	}

	private TextField<String> codigoCartorio() {
		return new TextField<String>("codigoCartorio", new Model<String>(tituloRemessa.getCodigoCartorio().toString()));
	}

	private TextField<String> cartorio(){
//		Hibernate.initialize(tituloRemessa.getRemessa().getInstituicaoDestino());
//		return new TextField<String>("remessa.instituicaoDestino.nomeFantasia", new Model<String>(tituloRemessa.getRemessa().getInstituicaoDestino().getNomeFantasia()));
		return new TextField<String>("remessa.instituicaoDestino.nomeFantasia", new Model<String>(StringUtils.EMPTY));
	}

	private TextField<String> pracaProtesto() {
		return new TextField<String>("pracaProtesto", new Model<String>(tituloRemessa.getPracaProtesto()));
	}

	 private TextField<String> status(){
		 return new TextField<String>("situacaoTitulo", new Model<String>(tituloRemessa.getSituacaoTitulo()));
	 }

	private TextField<String> dataRemessa(){
		return new TextField<String>("remessa.arquivo.dataEnvio", new Model<String>(DataUtil.localDateToString(tituloRemessa.getRemessa().getArquivo().getDataEnvio())));
	}
	
	private TextField<String> nomeSacadorVendedor() {
		return new TextField<String>("nomeSacadorVendedor", new Model<String>(tituloRemessa.getNomeSacadorVendedor()));
	}

	private TextField<String> documentoSacador() {
		return new TextField<String>("documentoSacador", new Model<String>(tituloRemessa.getDocumentoSacador()));
	}

	private TextField<String> ufSacadorVendedor() {
		return new TextField<String>("ufSacadorVendedor", new Model<String>(tituloRemessa.getUfSacadorVendedor()));
	}

	private TextField<String> cepSacadorVendedor() {
		return new TextField<String>("cepSacadorVendedor", new Model<String>(tituloRemessa.getCepSacadorVendedor()));
	}

	private TextField<String> cidadeSacadorVendedor() {
		return new TextField<String>("cidadeSacadorVendedor", new Model<String>(tituloRemessa.getCidadeSacadorVendedor()));
	}

	private TextField<String> enderecoSacadorVendedor() {
		return new TextField<String>("enderecoSacadorVendedor", new Model<String>(tituloRemessa.getEnderecoSacadorVendedor()));
	}

	private TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor", new Model<String>(tituloRemessa.getNomeDevedor()));
	}

	private TextField<String> documentoDevedor() {
		return new TextField<String>("documentoDevedor", new Model<String>(tituloRemessa.getDocumentoDevedor()));
	}

	private TextField<String> ufDevedor() {
		return new TextField<String>("ufDevedor", new Model<String>(tituloRemessa.getUfDevedor()));
	}

	private TextField<String> cepDevedor() {
		return new TextField<String>("cepDevedor", new Model<String>(tituloRemessa.getCepDevedor()));
	}

	private TextField<String> cidadeDevedor() {
		return new TextField<String>("cidadeDevedor", new Model<String>(tituloRemessa.getCidadeDevedor()));
	}

	private TextField<String> enderecoDevedor() {
		return new TextField<String>("enderecoDevedor", new Model<String>(tituloRemessa.getEnderecoDevedor()));
	}

	private TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo", new Model<String>(tituloRemessa.getNumeroTitulo()));
	}

	private TextField<String> portador(){
		return new TextField<String>("remessa.arquivo.instituicaoEnvio.nomeFantasia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getNomePortador()));
	}

	 private TextField<String> agencia(){
		 return new TextField<String>("agencia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getAgenciaCentralizadora()));
	 }

	private TextField<String> nossoNumero() {
		return new TextField<String>("nossoNumero", new Model<String>(tituloRemessa.getNossoNumero()));
	}

	private TextField<String> especieTitulo() {
		return new TextField<String>("especieTitulo", new Model<String>(tituloRemessa.getEspecieTitulo()));
	}

	private TextField<String> dataEmissaoTitulo() {
		return new TextField<String>("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataEmissaoTitulo())));
	}

	private TextField<String> dataVencimentoTitulo() {
		return new TextField<String>("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataVencimentoTitulo())));
	}

	public TextField<String> valorTitulo() {
		return new TextField<String>("valorTitulo", new Model<String>("R$ " + tituloRemessa.getValorTitulo().toString()));
	}

	public TextField<String> saldoTitulo() {
		return new TextField<String>("saldoTitulo", new Model<String>("R$ " + tituloRemessa.getSaldoTitulo().toString()));
	}

	public TextField<String> valorCustaCartorio() {
		return new TextField<String>("valorCustaCartorio", new Model<String>("R$ " + tituloRemessa.getValorCustaCartorio().toString()));
	}

	public TextField<String> valorGravacaoEletronica() {
		return new TextField<String>("valorGravacaoEletronica", new Model<String>("R$ " + tituloRemessa.getValorGravacaoEletronica().toString()));
	}

	public TextField<String> valorCustasCartorioDistribuidor() {
		return new TextField<String>("valorCustasCartorioDistribuidor", new Model<String>("R$ " + tituloRemessa.getValorCustasCartorioDistribuidor().toString()));
	}

	public TextField<String> valorDemaisDespesas() {
		return new TextField<String>("valorDemaisDespesas", new Model<String>("R$ " + tituloRemessa.getValorDemaisDespesas().toString()));
	}

	public TextField<String> nomeCedenteFavorecido() {
		return new TextField<String>("nomeCedenteFavorecido", new Model<String>(tituloRemessa.getNomeCedenteFavorecido()));
	}

	public TextField<String> agenciaCodigoCedente() {
		return new TextField<String>("agenciaCodigoCedente", new Model<String>(tituloRemessa.getAgenciaCodigoCedente()));
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}

}