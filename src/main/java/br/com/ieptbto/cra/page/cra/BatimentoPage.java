package br.com.ieptbto.cra.page.cra;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
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
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.ireport.TituloBean;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class BatimentoPage extends BasePage<Batimento> {

	private static final Logger logger = Logger.getLogger(BatimentoPage.class);
	
	@SpringBean
	RetornoMediator batimentoMediator;
	@SpringBean
	TituloMediator tituloMediator;
	
	private Batimento batimento;
	private ListView<Remessa> remessas;
	
	public BatimentoPage() {
		this.batimento = new Batimento();
		final CheckGroup<Remessa> grupo = new CheckGroup<Remessa>("group", new ArrayList<Remessa>());
		Form<Batimento> form = new Form<Batimento>("form"){

        	@Override
            protected void onSubmit(){
				List<Remessa> retornosParaConfirmar = new ArrayList<Remessa>();
				
				try{
					if (grupo.getModelObject().isEmpty()){
						error("Ao menos um retorno deve ser selecionado!");
					} else {
						retornosParaConfirmar = (List<Remessa>) grupo.getModelObject();
						batimentoMediator.confirmarBatimentos(retornosParaConfirmar);
					}
					setResponsePage(new BatimentoLabel());
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento! Entre em contato com a CRA.");
				}
            }
        };
        form.add(new Button("confirmarBotao"));
        form.add(grupo);
        add(carregarListaRetornos());
        remessas.setReuseItems(true);
        grupo.add(remessas);
        add(form);
	}
	
	private ListView<Remessa> carregarListaRetornos(){
		return remessas = new ListView<Remessa>("retornos", batimentoMediator.buscarRetornosParaBatimento()){

			@Override
            protected void populateItem(ListItem<Remessa> item){
				final Remessa retorno = item.getModelObject();
				item.add(new Check<Remessa>("checkbox", item.getModel()));
                item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				BigDecimal valorPagos = batimentoMediator.buscarValorDeTitulosPagos(retorno);
				if (valorPagos==null || valorPagos.equals(BigDecimal.ZERO)) {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", BigDecimal.ZERO));
				} else {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", valorPagos));
				}
				item.add(botaoGerarRelatorio(retorno));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					@Override
					public void onClick() {
		            	setResponsePage(new TitulosArquivoPage(retorno));  
		            }
		        };
		        linkArquivo.add(new Label("arquivo.nomeArquivo", retorno.getArquivo().getNomeArquivo()));
		        item.add(linkArquivo);
            }

			private Link<Remessa> botaoGerarRelatorio(final Remessa remessa){
				return new Link<Remessa>("gerarRelatorio"){
					
					@Override
					public void onClick() {
						try {
							HashMap<String, Object> parametros = new HashMap<String, Object>();
							List<TituloRemessa> titulos = tituloMediator.buscarTitulosPorRemessa(remessa, remessa.getArquivo().getInstituicaoEnvio(), remessa.getInstituicaoDestino());
							if (titulos.isEmpty())
								throw new InfraException("Não foi possível gerar o relatório. A busca não retornou resultados!");
							
							parametros.put("NOME_ARQUIVO", remessa.getArquivo().getNomeArquivo());
							parametros.put("DATA_ENVIO", DataUtil.localDateToString(remessa.getDataRecebimento()));
							parametros.put("INSTITUICAO", remessa.getInstituicaoOrigem().getNomeFantasia());
								
							List<TituloBean> titulosJR = new ArrayList<TituloBean>();
							for (TituloRemessa tituloRemessa : titulos) {
								TituloBean tituloJR = new TituloBean();
								tituloJR.parseToTituloRemessa(tituloRemessa);
								titulosJR.add(tituloJR);
							}
							JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(titulosJR);
							JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/RelatorioRetorno.jrxml"));
							JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);
							
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
        };
	}
	
	@Override
	protected IModel<Batimento> getModel() {
		return new CompoundPropertyModel<Batimento>(batimento);
	}
}
