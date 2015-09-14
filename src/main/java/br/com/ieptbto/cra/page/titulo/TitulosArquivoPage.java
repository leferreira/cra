package br.com.ieptbto.cra.page.titulo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
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
public class TitulosArquivoPage extends BasePage<Remessa> {

	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	@SpringBean
	RemessaMediator remessaMediator;
	private Remessa remessa;
	private List<TituloRemessa> titulos;
	
	public TitulosArquivoPage(Remessa remessa) {
		this.titulos = tituloMediator.buscarTitulosPorRemessa(remessa, getUser().getInstituicao()); 
		setRemessa(remessa);
		carregarInformacoes();
	}
	
	private void carregarInformacoes(){
		add(nomeArquivo());
		add(tipoArquivo());
		add(instituicaoEnvio());
		add(instituicaoDestino());
		add(dataEnvio());
		add(usuarioEnvio());
		add(carregarListaTitulos());
		add(botaoGerarRelatorio());
		add(downloadArquivoTXT(getRemessa()));
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
	
	private Link<Remessa> botaoGerarRelatorio(){
		return new Link<Remessa>("gerarRelatorio"){
			
			@Override
			public void onClick() {
				TipoArquivoEnum tipoArquivo = remessa.getArquivo().getTipoArquivo().getTipoArquivo();
				JasperPrint jasperPrint = null;

				try {
					if (tipoArquivo.equals(TipoArquivoEnum.REMESSA)) {
						JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRemessa.jrxml"));
						jasperPrint = relatorioMediator.relatorioRemessa(jasperReport ,remessa, getUser().getInstituicao());
					} else if (tipoArquivo.equals(TipoArquivoEnum.CONFIRMACAO)) {
						JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioConfirmação.jrxml"));
						jasperPrint = relatorioMediator.relatorioConfirmacao(jasperReport, remessa, getUser().getInstituicao());
					} else if (tipoArquivo.equals(TipoArquivoEnum.RETORNO)) {
						JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRetorno.jrxml"));
						jasperPrint = relatorioMediator.relatorioRetorno(jasperReport ,remessa, getUser().getInstituicao());
					}
					
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
					        new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_" + remessa.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
				} catch (InfraException ex) { 
					error(ex.getMessage());
				} catch (Exception e) { 
					error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
	}
	
	private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
		return new Link<Remessa>("downloadArquivo") {

			@Override
			public void onClick() {
				File file = remessaMediator.baixarRemessaTXT(getUser().getInstituicao(), remessa);
				IResourceStream resourceStream = new FileResourceStream(file);

				getRequestCycle().scheduleRequestHandlerAfterCurrent(
				        new ResourceStreamRequestHandler(resourceStream, file.getName()));
			}
		};
	}
	
	private Label nomeArquivo(){
		return new Label("nomeArquivo", getRemessa().getArquivo().getNomeArquivo());
	}
	
	private Label tipoArquivo(){
		return new Label("tipo", getRemessa().getArquivo().getTipoArquivo().getTipoArquivo().getLabel());
	}

	private Label instituicaoEnvio(){
		return new Label("instituicaoEnvio", getRemessa().getInstituicaoOrigem().getNomeFantasia());
	}
	
	private Label instituicaoDestino(){
		return new Label("instituicaoDestino", getRemessa().getInstituicaoDestino().getNomeFantasia());
	}
	
	private Label usuarioEnvio(){
		return new Label("usuario", getRemessa().getArquivo().getUsuarioEnvio().getNome());
	}
	
	private Label dataEnvio(){
//		if (getRemessa().getArquivo().getHoraEnvio() != null) {
//			return new Label("dataEnvio", DataUtil.localDateToString(getRemessa().getArquivo().getDataEnvio()) + " " + getRemessa().getArquivo().getHoraEnvio());			
//		}
		return new Label("dataEnvio", DataUtil.localDateToString(getRemessa().getArquivo().getDataEnvio()));			
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