package br.com.ieptbto.cra.page.relatorio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author thasso
 *
 */
@SuppressWarnings("serial")
public class RelatorioTitulosPage extends BasePage<TituloRemessa> {

	private static final Logger logger = Logger.getLogger(RelatorioTitulosPage.class);
	
	@SpringBean
	RelatorioMediator relatorioMediator;
	private TituloRemessa titulo;
	private Instituicao instituicao;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private TipoRelatorio tipoRelatorio;
	private List<TituloRemessa> titulos;
	
	public RelatorioTitulosPage(Instituicao instituicao, TipoRelatorio situacaoTitulos, LocalDate dataInicio, LocalDate dataFim) {
		this.instituicao = instituicao;
		this.dataInicio =dataInicio;
		this.dataFim = dataFim;
		this.tipoRelatorio = situacaoTitulos;
		setTitulos(relatorioMediator.buscarTitulosParaRelatorio(instituicao, situacaoTitulos, dataInicio, dataFim, getUser()));
		addCampos();
	}
	
	private void addCampos() {
		add(carregarListaTitulos());
		add(dataInicio());
		add(dataFim());
		add(label());
		add(instituicao());
		add(botaoGerarRelatorio());
	}

	private ListView<TituloRemessa> carregarListaTitulos() {
		return new ListView<TituloRemessa>("listViewTitulos", getTitulos()) {

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
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
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto()));
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", tituloLista.getValorTitulo()));
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}

	private Link<TituloRemessa> botaoGerarRelatorio(){
		return new Link<TituloRemessa>("gerarRelatorio"){
			
			@Override
			public void onClick() {
				HashMap<String, Object> parametros = new HashMap<String, Object>();
				JasperPrint jasperPrint = null;
				
				parametros.put("DATA_INICIO", DataUtil.localDateToString(dataInicio));
				parametros.put("DATA_FIM", DataUtil.localDateToString(dataFim));
				parametros.put("INSTITUICAO", getInstituicao().getNomeFantasia());

				try {
					if (getTitulos().isEmpty()) {
						throw new InfraException("Não foi possível gerar o relatório! A busca não retornou títulos!");
					}
					
					JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(getTitulos());
					if (getTipoRelatorio().equals(TipoRelatorio.GERAL) || getTipoRelatorio().equals(TipoRelatorio.EM_ABERTO)) {
						if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA) || 
								getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CONVENIO)) {
							JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioTitulos.jrxml"));
							jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
						} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)){
							JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioTitulosPorMunicipio.jrxml"));
							jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
						}
					} else {
						parametros.put("TIPO_RELATORIO", getTipoRelatorio().getLabel().toUpperCase());
						if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA) || 
								getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CONVENIO)) {
							JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioTitulosPorSituacao.jrxml"));
							jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
						} else if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)){
							JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioTitulosPorMunicipioPorSituacao.jrxml"));
							jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
						}
					}
					
					JasperExportManager.exportReportToPdfStream(jasperPrint, getResponse().getOutputStream());
				} catch (InfraException ex) {
					error(ex.getMessage());
					logger.error(ex.getMessage());
				} catch (JRException e) {
					logger.error(e);
					error("Não foi possível gerar o relatório! A busca não retornou títulos!");
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
	
	private Label label(){
		if (getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			return new Label("label", new Model<String>("Portador:"));
		}
		return new Label("label", new Model<String>("Cartório:"));
	}
	
	private TextField<String> instituicao(){
		return new TextField<String>("instituicao", new Model<String>(getInstituicao().getNomeFantasia()));
	}
	
	public List<TituloRemessa> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<TituloRemessa> titulos) {
		this.titulos = titulos;
	}

	public Instituicao getInstituicao() {
		return instituicao;
	}
	
	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(titulo);
	}

	public TipoRelatorio getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
}
