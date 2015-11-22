package br.com.ieptbto.cra.page.titulo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.ArquivoOcorrenciaBean;
import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoOcorrencia;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER})
public class HistoricoPage extends BasePage<TituloRemessa> {

	@SpringBean
	private TituloMediator tituloMediator;
	private TituloRemessa tituloRemessa;
	private List<ArquivoOcorrenciaBean> arquivosOcorrencias;

	public HistoricoPage(TituloRemessa titulo){
		this.tituloRemessa = tituloMediator.carregarDadosHistoricoTitulo(titulo);
		carregarCampos();
		carregarArquivosOcorrencias();
	}
	
	private void carregarArquivosOcorrencias() {
		List<Historico> historicoArquivos = getTituloRemessa().getHistoricos();
		
		for (Historico historico : historicoArquivos) {
			ArquivoOcorrenciaBean novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToHistorico(historico);
			
			getArquivosOcorrencias().add(novaOcorrencia);
			if (historico.getRemessa().getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.CONFIRMACAO) ||
					historico.getRemessa().getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.RETORNO)) {
				if (historico.getRemessa().getArquivoGeradoProBanco().getId() != historico.getRemessa().getArquivo().getId()) {
					ArquivoOcorrenciaBean novaOcorrenciaArquivo = new ArquivoOcorrenciaBean();
					novaOcorrenciaArquivo.parseToArquivoGerado(historico.getRemessa().getArquivoGeradoProBanco());
					
					getArquivosOcorrencias().add(novaOcorrenciaArquivo);
				}
			}
		}
		
		if (getTituloRemessa().getPedidoDesistencia() != null) {
			ArquivoOcorrenciaBean novaOcorrencia = new ArquivoOcorrenciaBean();
			novaOcorrencia.parseToDesistenciaProtesto(getTituloRemessa().getPedidoDesistencia().getDesistenciaProtesto());
			
			getArquivosOcorrencias().add(novaOcorrencia);
		}
		
		add(listArquivoOcorrenciaBean());
	}

	private ListView<ArquivoOcorrenciaBean> listArquivoOcorrenciaBean(){
		return new ListView<ArquivoOcorrenciaBean>("divListaHistorico", getArquivosOcorrencias()) {

			@Override
			protected void populateItem(ListItem<ArquivoOcorrenciaBean> item) {
				final ArquivoOcorrenciaBean arquivoOcorrenciaBean = item.getModelObject();
				
				if (arquivoOcorrenciaBean.getRemessa() != null) {
					Link<Remessa> linkArquivo = new Link<Remessa>("linkArquivo") {
						
						@Override
						public void onClick() {
							setResponsePage(new TitulosArquivoPage(arquivoOcorrenciaBean.getRemessa()));  
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivoOcorrenciaBean.getRemessa().getArquivo().getNomeArquivo()));
					item.add(linkArquivo);
					item.add(new Label("acao", " enviado em "));
				} 
				
				if (arquivoOcorrenciaBean.getArquivoGerado() != null) {
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {
						
						@Override
						public void onClick() {
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivoOcorrenciaBean.getArquivoGerado()));
					item.add(linkArquivo);
					item.add(new Label("acao", " liberado em "));
				}
				
				if (arquivoOcorrenciaBean.getDesistenciaProtesto() != null) {
					Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {
						
						@Override
						public void onClick() {
						}
					};
					linkArquivo.add(new Label("nomeArquivo", arquivoOcorrenciaBean.getDesistenciaProtesto().getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
					item.add(linkArquivo);
					item.add(new Label("acao", " enviado em "));
				}
				
				item.add(new Label("dataOcorrencia", arquivoOcorrenciaBean.getDataHora()));
				item.add(new Label("usuarioAcao", arquivoOcorrenciaBean.getNomeUsuario()));
			}
		};
	}
	
	
    private void carregarCampos() {
    	add(numeroProtocoloCartorio());
    	add(dataProtocolo());
    	add(codigoCartorio());
    	add(dataOcorrencia());
    	add(irregularidade());
    	add(codigoMunicipio());
    	add(pracaProtesto());
    	add(cartorio());
    	add(status());
    	add(nomeSacadorVendedor());
    	add(documentoSacador());
    	add(ufSacadorVendedor());
    	add(cepSacadorVendedor());
    	add(cidadeSacadorVendedor());
    	add(enderecoSacadorVendedor());
    	add(complementoRegistro());
    	add(nomeDevedor());
    	add(documentoDevedor());
    	add(ufDevedor());
    	add(cepDevedor());
    	add(cidadeDevedor());
    	add(enderecoDevedor());
    	add(numeroTitulo());
    	add(portador());
    	add(agencia());
    	add(nossoNumero());
    	add(especieTitulo());
    	add(dataEmissaoTitulo());
    	add(dataVencimentoTitulo());
    	add(valorTitulo());
    	add(saldoTitulo());
    	add(valorCustaCartorio());
    	add(valorCustasCartorioDistribuidor());
    	add(valorDemaisDespesas());
    	add(valorGravacaoEletronica());
    	add(nomeCedenteFavorecido());
    	add(agenciaCodigoCedente());
	}

	private Label codigoCartorio() {
		return new Label("codigoCartorio", new Model<String>(getTituloRemessa().getRemessa().getInstituicaoDestino().getCodigoCartorio()));
	}

    private Label dataOcorrencia() {
    	LocalDate dataOcorrencia = getTituloRemessa().getRemessa().getCabecalho().getDataMovimento();
    	
    	if (getTituloRemessa().getConfirmacao() != null) {
    		dataOcorrencia = getTituloRemessa().getConfirmacao().getDataOcorrencia();
    	}
    	if (getTituloRemessa().getRetorno() != null) {
    		dataOcorrencia = getTituloRemessa().getRetorno().getDataOcorrencia();
    	}
		return new Label("dataOcorrencia", DataUtil.localDateToString(dataOcorrencia));
	}
    
    private Label irregularidade() {
    	String irregularidade = StringUtils.EMPTY;
//    	if (getTituloRemessa().getConfirmacao() != null) {
//    		if (getTituloRemessa().getConfirmacao().getCodigoIrregularidade() != null && 
//    				getTituloRemessa().getConfirmacao().getCodigoIrregularidade() != "  ") {
//    		}
//    	}
		return new Label("irregularidade", new Model<String>(irregularidade));
	}
    
    private Label complementoRegistro(){
    	String complementoRegistro = StringUtils.EMPTY;
    	if (getTituloRemessa().getComplementoRegistro() != null) {
    		if (getTituloRemessa().getComplementoRegistro().length() == 2){
    			complementoRegistro = getTituloRemessa().getComplementoRegistro();
    		}
    	}
    	return new Label("complementoRegistro", complementoRegistro);
    }
    
    private Label codigoMunicipio() {
		return new Label("codigoMunicipio", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getCodigoMunicipio()));
	}
    
    private Label valorGravacaoEletronica() {
    	BigDecimal valorGravacao = BigDecimal.ZERO;
    	if (getTituloRemessa().getConfirmacao() != null) {
    		if (getTituloRemessa().getConfirmacao().getTipoOcorrencia() != null) {
    			String tipoOcorrencia = getTituloRemessa().getConfirmacao().getTipoOcorrencia().trim();
    			if (!tipoOcorrencia.equals(StringUtils.EMPTY) || tipoOcorrencia.equals(TipoOcorrencia.DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS.getConstante())) {
    				valorGravacao = getTituloRemessa().getRemessa().getInstituicaoOrigem().getValorConfirmacao();
    			}
    		}
    	}
    	return new LabelValorMonetario<BigDecimal>("valorGravacaoEletronica", valorGravacao);
    }
    
	private Label numeroProtocoloCartorio() {
		String numeroProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null){
			numeroProtocolo = getTituloRemessa().getConfirmacao().getNumeroProtocoloCartorio();
		} 
		return new Label("numeroProtocoloCartorio", new Model<String>(numeroProtocolo));
	}

	private Label dataProtocolo() {	
		String dataProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null){
			dataProtocolo = DataUtil.localDateToString(getTituloRemessa().getConfirmacao().getDataProtocolo()); 
		}
		return new Label("dataProtocolo", new Model<String>(dataProtocolo));
	}

	private Label cartorio(){
		return new Label("cartorio", new Model<String>(getTituloRemessa().getRemessa().getInstituicaoDestino().getNomeFantasia().toUpperCase()));
	}

	private Label pracaProtesto() {
		return new Label("pracaProtesto", new Model<String>(getTituloRemessa().getPracaProtesto()));
	}

	 private Label status(){
		return new Label("situacaoTitulo", new Model<String>(getTituloRemessa().getSituacaoTitulo()));
	 }

	 private Label numeroTitulo() {
		return new Label("numeroTitulo", new Model<String>(getTituloRemessa().getNumeroTitulo()));
	 }

 	private Label portador(){
		return new Label("portador", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getNomePortador()));
 	}

	 private Label agencia(){
		return new Label("agencia", new Model<String>(getTituloRemessa().getRemessa().getCabecalho().getAgenciaCentralizadora()));
	 }
	
	private Label nossoNumero() {
		return new Label("nossoNumero", new Model<String>(getTituloRemessa().getNossoNumero()));
	}
	
	private Label especieTitulo() {
		return new Label("especieTitulo", new Model<String>(getTituloRemessa().getEspecieTitulo()));
	}
	
	private Label dataEmissaoTitulo() {
		return new Label("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataEmissaoTitulo())));
	}
	
	private Label dataVencimentoTitulo() {
		return new Label("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataVencimentoTitulo())));
	}
	
	public Label valorTitulo() {
		Label textField = new Label("valorTitulo", new Model<String>("R$ " + getTituloRemessa().getValorTitulo().toString()));
		return textField;
	}
	
	public Label saldoTitulo() {
		Label textField = new Label("saldoTitulo", new Model<String>("R$ " + getTituloRemessa().getSaldoTitulo().toString()));
		return textField;
	}
	
	public Label valorCustaCartorio() {
		BigDecimal valorCustaCartorio = BigDecimal.ZERO;
		if (getTituloRemessa().getConfirmacao() != null) {
			valorCustaCartorio = getTituloRemessa().getConfirmacao().getValorCustaCartorio();
		}
		if (getTituloRemessa().getRetorno() != null) {
			valorCustaCartorio = getTituloRemessa().getRetorno().getValorCustaCartorio();
		}
		return new LabelValorMonetario<BigDecimal>("valorCustaCartorio", valorCustaCartorio);
	}
	
	public Label valorCustasCartorioDistribuidor() {
		BigDecimal valorCustasCartorioDistribuidor = BigDecimal.ZERO;
		if (getTituloRemessa().getConfirmacao() != null) {
			valorCustasCartorioDistribuidor = getTituloRemessa().getConfirmacao().getValorCustasCartorioDistribuidor();
		}
		if (getTituloRemessa().getRetorno() != null) {
			valorCustasCartorioDistribuidor = getTituloRemessa().getRetorno().getValorCustasCartorioDistribuidor();
		}
		return new LabelValorMonetario<BigDecimal>("valorCustasCartorioDistribuidor", valorCustasCartorioDistribuidor);
	}
	
	public Label valorDemaisDespesas() {
		BigDecimal valorDemaisDespesas = BigDecimal.ZERO;
		if (getTituloRemessa().getConfirmacao() != null) {
			valorDemaisDespesas = getTituloRemessa().getConfirmacao().getValorDemaisDespesas();
		}
		if (getTituloRemessa().getRetorno() != null) {
			valorDemaisDespesas = getTituloRemessa().getRetorno().getValorDemaisDespesas();
		}
		return new LabelValorMonetario<BigDecimal>("valorDemaisDespesas", valorDemaisDespesas);
	}
	
	public Label nomeCedenteFavorecido() {
		Label textField = new Label("nomeCedenteFavorecido", new Model<String>(getTituloRemessa().getNomeCedenteFavorecido()));
		return textField;
	}
	
	public Label agenciaCodigoCedente() {
		Label textField = new Label("agenciaCodigoCedente", new Model<String>(getTituloRemessa().getAgenciaCodigoCedente()));
		return textField;
	}
	
	private Label nomeSacadorVendedor() {
		Label textField = new Label("nomeSacadorVendedor", new Model<String>(getTituloRemessa().getNomeSacadorVendedor()));
		return textField;
	}

	private Label documentoSacador() {
		Label textField = new Label("documentoSacador", new Model<String>(getTituloRemessa().getDocumentoSacador()));
		return textField;
	}

	private Label ufSacadorVendedor() {
		Label textField = new Label("ufSacadorVendedor", new Model<String>(getTituloRemessa().getUfSacadorVendedor()));
		return textField;
	}

	private Label cepSacadorVendedor() {
		Label textField = new Label("cepSacadorVendedor", new Model<String>(getTituloRemessa().getCepSacadorVendedor()));
		return textField;
	}

	private Label cidadeSacadorVendedor() {
		Label textField = new Label("cidadeSacadorVendedor", new Model<String>(getTituloRemessa().getCidadeSacadorVendedor()));
		return textField;
	}

	private Label enderecoSacadorVendedor() {
		Label textField = new Label("enderecoSacadorVendedor", new Model<String>(getTituloRemessa().getEnderecoSacadorVendedor()));
		return textField;
	}

	private Label nomeDevedor() {
		Label textField = new Label("nomeDevedor", new Model<String>(getTituloRemessa().getNomeDevedor()));
		return textField;
	}

	private Label documentoDevedor() {
		Label textField = new Label("documentoDevedor", new Model<String>(getTituloRemessa().getNumeroIdentificacaoDevedor()));
		return textField;
	}

	private Label ufDevedor() {
		Label textField = new Label("ufDevedor", new Model<String>(getTituloRemessa().getUfDevedor()));
		return textField;
	}

	private Label cepDevedor() {
		Label textField = new Label("cepDevedor", new Model<String>(getTituloRemessa().getCepDevedor()));
		return textField;
	}

	private Label cidadeDevedor() {
		Label textField = new Label("cidadeDevedor", new Model<String>(getTituloRemessa().getCidadeDevedor()));
		return textField;
	}

	private Label enderecoDevedor() {
		Label textField = new Label("enderecoDevedor", new Model<String>(getTituloRemessa().getEnderecoDevedor()));
		return textField;
	}
	
	private TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}
	
	public List<ArquivoOcorrenciaBean> getArquivosOcorrencias() {
		if (arquivosOcorrencias == null) {
			arquivosOcorrencias = new ArrayList<ArquivoOcorrenciaBean>();
		}
		return arquivosOcorrencias;
	}
	
	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}