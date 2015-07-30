package br.com.ieptbto.cra.page.titulo;

import java.math.BigDecimal;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class TitulosDoArquivoPage extends BasePage<Arquivo> {

	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private RemessaMediator remessaMediator;
	@SpringBean
	private RelatorioMediator relatorioMediator;
	private Remessa remessa;
	private Arquivo arquivo;
	private TipoArquivoEnum tipoArquivo;
	private List<TituloRemessa> titulos;
	
	public TitulosDoArquivoPage(Remessa remessa) {
		setTitulos(tituloMediator.buscarTitulosPorRemessa(remessa, getUser().getInstituicao()));
		setArquivo(remessa.getArquivo());
		this.remessa = remessa;
		carregarInformacoes();
		add(carregarListaTitulos());
		add(botaoGerarRelatorio());
	}
	
	private ListView<TituloRemessa> carregarListaTitulos() {
		return new ListView<TituloRemessa>("listViewTituloArquivo", getTitulos()) {

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				} else if (tituloLista.getRetorno() != null) {
					item.add(new Label("protocolo", tituloLista.getRetorno().getNumeroProtocoloCartorio()));
				} else { 
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(new Label("portador", tituloLista.getRemessa().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
		            }
		        };
		        linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		        item.add(linkHistorico);
				item.add(new Label("praca", tituloLista.getPracaProtesto()));
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", tituloLista.getValorTitulo()));
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}
	
	private Button botaoGerarRelatorio(){
		return new Button("gerarRelatorio"){
			@Override
			public void onSubmit() {
				try {
					JasperPrint jasperPrint = relatorioMediator.novoRelatorioDeArquivoDetalhado(getUser().getInstituicao(), getArquivo(), getTitulos());
					getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
				} catch (JRException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	private void carregarInformacoes(){
		add(nomeArquivo());
		add(portador());
		add(dataEnvio());
		add(tipoArquivo());
	}

	private TextField<String> nomeArquivo(){
		return new TextField<String>("nomeArquivo", new Model<String>(remessa.getArquivo().getNomeArquivo()));
	}
	
	private TextField<String> portador(){
		return new TextField<String>("nomePortador", new Model<String>(remessa.getCabecalho().getNomePortador()));
	}
	
	private TextField<String> dataEnvio(){
		return new TextField<String>("dataEnvio", new Model<String>(DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
	}
	
	private TextField<String> tipoArquivo(){
		return new TextField<String>("tipo", new Model<String>(remessa.getArquivo().getTipoArquivo().getTipoArquivo().getLabel()));
	}
	
	private List<TituloRemessa> getTitulos() {
		return titulos;
	}

	private void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	private Arquivo getArquivo(){
		return arquivo;
	}
	
	public void setTitulos(List<TituloRemessa> titulos) {
		this.titulos = titulos;
	}
	
	public List<TituloRemessa> buscarTitulosDoArquivo() {
		
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
			return tituloMediator.buscarTitulosPorArquivo(arquivo, getUser().getInstituicao());
		} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
			return tituloMediator.buscarTitulosConfirmacaoRetorno(arquivo, getUser().getInstituicao());
		} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
			return tituloMediator.buscarTitulosConfirmacaoRetorno(arquivo, getUser().getInstituicao());
		} else if (tipoArquivo.equals(TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO)) {
			
		} else if (tipoArquivo.equals(TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO)) {
			
		} else if (tipoArquivo.equals(TipoArquivoEnum.AUTORIZACAO_DE_CANCELAMENTO)) {
			
		} else {
			throw new InfraException("Não foi possível identificar o tipo do Arquivo");
		}
		return titulos;
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(remessa.getArquivo());
	}
}
