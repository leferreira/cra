package br.com.ieptbto.cra.page.relatorio;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
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
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author thasso
 *
 */
public class RelatorioTitulosPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RelatorioMediator relatorioMediator;
	
	private TituloRemessa titulo;
	private Instituicao instituicao;
	private Municipio municipio;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private List<TituloRemessa> titulos;
	
	public RelatorioTitulosPage(Instituicao instituicao, Municipio municipio,
			LocalDate dataInicio, LocalDate dataFim) {
		this.instituicao = instituicao;
		this.municipio = municipio;
		this.dataInicio =dataInicio;
		this.dataFim = dataFim;
		setTitulos(relatorioMediator.buscarTitulosParaRelatorio(instituicao, municipio, dataInicio, dataFim, getUser()));
		add(carregarListaTitulos());
		add(dataInicio());
		add(dataFim());
		add(label());
		add(instituicao());
		add(botaoGerarRelatorio());
		add(quantidadeDeTitulos());
	}

	private ListView<TituloRemessa> carregarListaTitulos() {
		return new ListView<TituloRemessa>("listViewTitulos", getTitulos()) {
			/***/
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	private Component botaoGerarRelatorio(){
		return new Link("gerarRelatorio"){
			
			/***/
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick() {
				JasperPrint jasperPrint = null;
				
				try {
					
					if (!getTitulos().isEmpty()) {
						
						if (instituicao!=null) {
							jasperPrint = relatorioMediator.novoRelatorioDeTitulosPorInstituicao(instituicao, getTitulos(), dataInicio, dataFim);
							getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
						} else if (municipio!=null){
							jasperPrint = relatorioMediator.novoRelatorioDeTitulosPorMunicipio(municipio, getTitulos(), dataInicio, dataFim);
							getResponse().write(JasperExportManager.exportReportToPdf(jasperPrint));
						} else {
							error("Não foi possível gerar o relatório!");
						}
					} else 
						error("Não foi possível gerar o relatório! A busca não retornou títulos!");
				} catch (JRException e) {
					e.printStackTrace();
				}
			}
			
		};
	}
	
	private TextField<String> dataInicio(){
		return new TextField<String>("dataInicio", new Model<String>(DataUtil.localDateToString(dataInicio)));
	}
	
	private TextField<String> dataFim(){
		return new TextField<String>("dataFim", new Model<String>(DataUtil.localDateToString(dataFim)));
	}
	
	private TextField<Integer> quantidadeDeTitulos(){
		return new TextField<Integer>("totalTitulos", new Model<Integer>(getTitulos().size()));
	}
	
	private Component label(){
		if (instituicao!=null)
			return new Label("label", new Model<String>("Portador:"));
		else
			return new Label("label", new Model<String>("Município:"));
	}
	
	private TextField<String> instituicao(){
		if (instituicao!=null)
			return new TextField<String>("instituicao", new Model<String>(instituicao.getNomeFantasia()));
		else
			return new TextField<String>("instituicao", new Model<String>(municipio.getNomeMunicipio()));
	}
	
	public List<TituloRemessa> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<TituloRemessa> titulos) {
		this.titulos = titulos;
	}


	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}

}
