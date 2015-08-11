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
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class TitulosArquivoPage extends BasePage<Remessa> {

	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private RelatorioMediator relatorioMediator;
	private Remessa remessa;
	private List<TituloRemessa> titulos;
	
	public TitulosArquivoPage(Remessa remessa) {
		this.titulos = tituloMediator.buscarTitulosPorRemessa(remessa, getUser().getInstituicao()); 
		setRemessa(remessa);
		carregarInformacoes();
	}
	
	private void carregarInformacoes(){
		add(nomeArquivo());
		add(portador());
		add(dataEnvio());
		add(tipoArquivo());
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
					JasperPrint jasperPrint = relatorioMediator.novoRelatorioDeArquivoDetalhado(getUser().getInstituicao(), getRemessa().getArquivo(), getTitulos());
					getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
				} catch (JRException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	private TextField<String> nomeArquivo(){
		return new TextField<String>("nomeArquivo", new Model<String>(getRemessa().getArquivo().getNomeArquivo()));
	}
	
	private TextField<String> portador(){
		return new TextField<String>("nomePortador", new Model<String>(getRemessa().getCabecalho().getNomePortador()));
	}
	
	private TextField<String> dataEnvio(){
		return new TextField<String>("dataEnvio", new Model<String>(DataUtil.localDateToString(getRemessa().getArquivo().getDataEnvio())));
	}
	
	private TextField<String> tipoArquivo(){
		return new TextField<String>("tipo", new Model<String>(getRemessa().getArquivo().getTipoArquivo().getTipoArquivo().getLabel()));
	}
	
	private List<TituloRemessa> getTitulos() {
		return titulos;
	}

	public Remessa getRemessa() {
		return remessa;
	}
	
	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}
	
	@Override
	protected IModel<Remessa> getModel() {
		return new CompoundPropertyModel<Remessa>(remessa);
	}
}
