package br.com.ieptbto.cra.page.cra;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class RetornoAguardandoPage extends BasePage<Retorno> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RetornoAguardandoPage.class);
	
	@SpringBean
	private RetornoMediator retornoMediator;
	@SpringBean
	private RelatorioMediator relatorioMediator;
	private Retorno retorno;
	private TextField<String> campoDataBatimento;
	private ListView<Remessa> remessas;
	private List<Remessa> retornoBuscados;
	
	public RetornoAguardandoPage(){
		this.retorno = new Retorno();

		formularioBuscarBatimento();
		carregarGuiaRetorno();
	}
	
	public RetornoAguardandoPage(String message){
		this.retorno = new Retorno();

		info(message);
		formularioBuscarBatimento();
		carregarGuiaRetorno();
	}
	
	private void formularioBuscarBatimento() {
		Batimento batimento = new Batimento();
		Form<Batimento> formBuscar = new Form<Batimento>("formBuscarRetorno", new Model<Batimento>(batimento));
        formBuscar.add(campoDataBatimento());
		add(formBuscar);
	}

	private TextField<String> campoDataBatimento() {
		return campoDataBatimento = new TextField<String>("dataBatimento", new Model<String>());
	}

	private void carregarGuiaRetorno(){
		final CheckGroup<Remessa> grupo = new CheckGroup<Remessa>("group", new ArrayList<Remessa>());
        Form<Retorno> form = new Form<Retorno>("form"){

        	/***/
			private static final long serialVersionUID = 1L;

			protected void onSubmit(){
				List<Remessa> retornoLiberados = (List<Remessa>)grupo.getModelObject();
				
				try{
					retornoMediator.liberarRetornoBatimentoInstituicao(retornoLiberados);
					campoDataBatimento.setModelObject(null);
					setResponsePage(new RetornoAguardandoPage("Os arquivos de retorno foram"
							+ " liberados para serem gerados ao banco!"));
				
				} catch (InfraException e) {
					logger.error(e.getMessage(), e);
					error(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento! Entre em contato com a CRA.");
				}
            }
        };
        form.add(carregarListaRetornos());
        form.add(grupo);
        
        remessas.setReuseItems(true);
		grupo.add(remessas);
		add(form);
	}
	
	
	private ListView<Remessa> carregarListaRetornos(){
		return remessas = new ListView<Remessa>("retornos", buscarRetornosAguardandoLiberacao()){
			
			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<Remessa> item){
				final Remessa retorno = item.getModelObject();
				
				item.add(new Label("dataBatimento", DataUtil.localDateToString(retorno.getBatimento().getData())));
				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(retorno.getArquivo().getHoraEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				BigDecimal valorPagos = retornoMediator.buscarValorDeTitulosPagos(retorno);
				if (valorPagos==null || valorPagos.equals(BigDecimal.ZERO)) {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", BigDecimal.ZERO));
				} else {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", valorPagos));
				}
				
				BigDecimal valorCustas = retornoMediator.buscarValorDeCustasCartorio(retorno);
				if (valorCustas==null || valorCustas.equals(BigDecimal.ZERO)) {
					item.add(new LabelValorMonetario<BigDecimal>("valorCustas", BigDecimal.ZERO));
				} else {
					item.add(new LabelValorMonetario<BigDecimal>("valorCustas", valorCustas));
				}
				Link<Remessa> linkArquivo = new Link<Remessa>("linkArquivo") {
					
					/***/
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoPage(retorno));  
					}
				};
				linkArquivo.add(new Label("arquivo.nomeArquivo", retorno.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Check<Remessa>("checkbox", item.getModel()));
				item.add(removerConfirmado(retorno));
				item.add(botaoGerarRelatorio(retorno));
			}
			
			private Link<Arquivo> removerConfirmado(final Remessa retorno) {
				return new Link<Arquivo>("removerConfirmado") {
					
					/***/
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onClick() {
						
						try {
							retornoMediator.removerBatimento(retorno);
							setResponsePage(new RetornoAguardandoPage("O arquivo " + retorno.getArquivo().getNomeArquivo() + " do " 
								+ retorno.getInstituicaoOrigem().getNomeFantasia() + " foi retornado ao batimento!"));
							
						}  catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
							System.out.println(ex.getMessage() + ex.getCause());
						}  catch (Exception ex) {
							getFeedbackPanel().error("Não foi possível cancelar o batimento do arquivo de retorno selecionado!");
							System.out.println(ex.getMessage() + ex.getCause());
						}
					}
				};
			}
			
			private Link<Remessa> botaoGerarRelatorio(final Remessa retorno){
				return new Link<Remessa>("gerarRelatorio"){
					
					/***/
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onClick() {
						try {
							JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRetorno.jrxml"));
							JasperPrint jasperPrint = relatorioMediator.relatorioRetorno(jasperReport ,retorno, getUser().getInstituicao());
							
							File pdf = File.createTempFile("report", ".pdf");
							JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
							IResourceStream resourceStream = new FileResourceStream(pdf);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(
							        new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_" + retorno.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
						} catch (InfraException ex) { 
							error(ex.getMessage());
						} catch (Exception e) { 
							error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
							e.printStackTrace();
						}
					}
				};
			}
		};
	}
	
	public IModel<List<Remessa>> buscarRetornosAguardandoLiberacao() {
		return new LoadableDetachableModel<List<Remessa>>() {
			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Remessa> load() {
				return getRetornoBuscados();
			}
		};
	}
	
	public List<Remessa> getRetornoBuscados() {
		if  (this.retornoBuscados == null) {
			this.retornoBuscados = new ArrayList<Remessa>();
		}
		this.retornoBuscados.clear();

		LocalDate dataBatimento = null;
		if (campoDataBatimento.getModelObject() != null) {
			dataBatimento = DataUtil.stringToLocalDate(campoDataBatimento.getModelObject().toString());
		}
		return retornoBuscados = retornoMediator.buscarRetornosAguardandoLiberacao(dataBatimento);
	}

	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}
}
