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
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, })
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
	
	public TextField<String> numeroProtocoloCartorio() {
		return new TextField<String>("confirmacao.numeroProtocoloCartorio", new Model<String>(tituloRemessa.getNumeroProtocoloCartorio()));
	}

	public TextField<String> dataProtocolo() {
		String dataProtocolo = StringUtils.EMPTY;
		Hibernate.initialize(tituloRemessa.getConfirmacao());
		if (tituloRemessa.getConfirmacao() != null){
			dataProtocolo = DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataProtocolo()); 
		}
		return new TextField<String>("confirmacao.dataProtocolo", new Model<String>(dataProtocolo));
	}

	public TextField<String> codigoCartorio() {
		return new TextField<String>("codigoCartorio", new Model<String>(tituloRemessa.getCodigoCartorio().toString()));
	}

	public TextField<String> cartorio(){
//		Hibernate.initialize(tituloRemessa.getRemessa().getInstituicaoDestino());
//		return new TextField<String>("remessa.instituicaoDestino.nomeFantasia", new Model<String>(tituloRemessa.getRemessa().getInstituicaoDestino().getNomeFantasia()));
		return new TextField<String>("remessa.instituicaoDestino.nomeFantasia", new Model<String>(StringUtils.EMPTY));
	}

	public TextField<String> pracaProtesto() {
		return new TextField<String>("pracaProtesto", new Model<String>(tituloRemessa.getPracaProtesto()));
	}

	 public TextField<String> status(){
		 return new TextField<String>("situacaoTitulo", new Model<String>(tituloRemessa.getSituacaoTitulo()));
	 }

	public TextField<String> dataRemessa(){
		return new TextField<String>("remessa.arquivo.dataEnvio", new Model<String>(DataUtil.localDateToString(tituloRemessa.getRemessa().getArquivo().getDataEnvio())));
	}
	
	public TextField<String> nomeSacadorVendedor() {
		return new TextField<String>("nomeSacadorVendedor", new Model<String>(tituloRemessa.getNomeSacadorVendedor()));
	}

	public TextField<String> documentoSacador() {
		return new TextField<String>("documentoSacador", new Model<String>(tituloRemessa.getDocumentoSacador()));
	}

	public TextField<String> ufSacadorVendedor() {
		return new TextField<String>("ufSacadorVendedor", new Model<String>(tituloRemessa.getUfSacadorVendedor()));
	}

	public TextField<String> cepSacadorVendedor() {
		return new TextField<String>("cepSacadorVendedor", new Model<String>(tituloRemessa.getCepSacadorVendedor()));
	}

	public TextField<String> cidadeSacadorVendedor() {
		return new TextField<String>("cidadeSacadorVendedor", new Model<String>(tituloRemessa.getCidadeSacadorVendedor()));
	}

	public TextField<String> enderecoSacadorVendedor() {
		return new TextField<String>("enderecoSacadorVendedor", new Model<String>(tituloRemessa.getEnderecoSacadorVendedor()));
	}

	public TextField<String> nomeDevedor() {
		return new TextField<String>("nomeDevedor", new Model<String>(tituloRemessa.getNomeDevedor()));
	}

	public TextField<String> documentoDevedor() {
		return new TextField<String>("documentoDevedor", new Model<String>(tituloRemessa.getDocumentoDevedor()));
	}

	public TextField<String> ufDevedor() {
		return new TextField<String>("ufDevedor", new Model<String>(tituloRemessa.getUfDevedor()));
	}

	public TextField<String> cepDevedor() {
		return new TextField<String>("cepDevedor", new Model<String>(tituloRemessa.getCepDevedor()));
	}

	public TextField<String> cidadeDevedor() {
		return new TextField<String>("cidadeDevedor", new Model<String>(tituloRemessa.getCidadeDevedor()));
	}

	public TextField<String> enderecoDevedor() {
		return new TextField<String>("enderecoDevedor", new Model<String>(tituloRemessa.getEnderecoDevedor()));
	}

	public TextField<String> numeroTitulo() {
		return new TextField<String>("numeroTitulo", new Model<String>(tituloRemessa.getNumeroTitulo()));
	}

	public TextField<String> portador(){
		return new TextField<String>("remessa.arquivo.instituicaoEnvio.nomeFantasia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getNomePortador()));
	}

	 public TextField<String> agencia(){
		 return new TextField<String>("agencia", new Model<String>(tituloRemessa.getRemessa().getCabecalho().getAgenciaCentralizadora()));
	 }

	public TextField<String> nossoNumero() {
		return new TextField<String>("nossoNumero", new Model<String>(tituloRemessa.getNossoNumero()));
	}

	public TextField<String> especieTitulo() {
		return new TextField<String>("especieTitulo", new Model<String>(tituloRemessa.getEspecieTitulo()));
	}

	public TextField<String> dataEmissaoTitulo() {
		return new TextField<String>("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataEmissaoTitulo())));
	}

	public TextField<String> dataVencimentoTitulo() {
		return new TextField<String>("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(tituloRemessa.getDataVencimentoTitulo())));
	}

	public TextField<String> valorTitulo() {
		return new TextField<String>("valorTitulo", new Model<String>(tituloRemessa.getValorTitulo().toString()));
	}

	public TextField<String> saldoTitulo() {
		return new TextField<String>("saldoTitulo", new Model<String>(tituloRemessa.getSaldoTitulo().toString()));
	}

	public TextField<String> valorCustaCartorio() {
		return new TextField<String>("valorCustaCartorio", new Model<String>(tituloRemessa.getValorCustaCartorio().toString()));
	}

	public TextField<String> valorGravacaoEletronica() {
		return new TextField<String>("valorGravacaoEletronica", new Model<String>(tituloRemessa.getValorGravacaoEletronica().toString()));
	}

	public TextField<String> valorCustasCartorioDistribuidor() {
		return new TextField<String>("valorCustasCartorioDistribuidor", new Model<String>(tituloRemessa.getValorCustasCartorioDistribuidor().toString()));
	}

	public TextField<String> valorDemaisDespesas() {
		return new TextField<String>("valorDemaisDespesas", new Model<String>(tituloRemessa.getValorDemaisDespesas().toString()));
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