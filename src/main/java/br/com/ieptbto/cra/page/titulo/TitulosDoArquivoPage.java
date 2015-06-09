package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
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
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.relatorio.VerRelatorioPage;
import br.com.ieptbto.cra.util.DataUtil;
@SuppressWarnings("rawtypes")
public class TitulosDoArquivoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;

	private Remessa remessa;
	private Arquivo arquivo;
	private List<TituloRemessa> titulos;
	
	public TitulosDoArquivoPage(Remessa remessa) {
		setTitulos(tituloMediator.buscarTitulosPorRemessa(remessa, getUser().getInstituicao()));
		setArquivo(remessa.getArquivo());
		this.remessa = remessa;
		carregarInformacoes();
		add(carregarListaTitulos());
		add(botaoGerarRelatorio());
	}

	public TitulosDoArquivoPage(Arquivo arquivo) {
		
		TipoArquivoEnum tipoArquivo = arquivo.getTipoArquivo().getTipoArquivo();
		if (tipoArquivo.equals(TipoArquivoEnum.REMESSA))
			setTitulos(tituloMediator.buscarTitulosPorArquivo(arquivo, getUser().getInstituicao()));
		else 
			setTitulos(tituloMediator.buscarTitulosConfirmacaoRetorno(arquivo, getUser().getInstituicao()));
		setRemessa(arquivo);
		setArquivo(arquivo);
		carregarInformacoes();
		add(carregarListaTitulos());
		add(botaoGerarRelatorio());
//		add(botaoDownload());
	}
	
	private ListView<TituloRemessa> carregarListaTitulos() {
		return new ListView<TituloRemessa>("listViewTituloArquivo", getTitulos()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				} else { 
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(new Label("portador", tituloLista.getRemessa().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				
				Link linkHistorico = new Link("linkHistorico") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
		            }
		        };
		        linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		        item.add(linkHistorico);
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto()));
				item.add(new LabelValorMonetario("valorTitulo", tituloLista.getValorTitulo()));
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}
	
	private Component botaoGerarRelatorio(){
		return new Link("gerarRelatorio"){

			/***/
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick() {
				JasperPrint jasperPrint = null;
				
				try {
					jasperPrint = relatorioMediator.novoRelatorioDeArquivoDetalhado(getUser().getInstituicao(), getArquivo(), getTitulos());
					setResponsePage(new VerRelatorioPage(jasperPrint));
				
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
		add(usuarioEnvio());
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
	
	private TextField<String> usuarioEnvio(){
		return new TextField<String>("usuarioEnvio", new Model<String>(remessa.getArquivo().getUsuarioEnvio().getNome()));
	}
	
	private TextField<String> tipoArquivo(){
		return new TextField<String>("tipo", new Model<String>(remessa.getArquivo().getTipoArquivo().getTipoArquivo().getLabel()));
	}
	
	private List<TituloRemessa> getTitulos() {
		return titulos;
	}
	
	private void setRemessa(Arquivo arquivo) {
		this.remessa = remessaMediator.buscarRemessasDoArquivo(arquivo, getUser().getInstituicao()).get(0);
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
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(remessa.getArquivo());
	}
}
