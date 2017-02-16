package br.com.ieptbto.cra.page.cra;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.dataTable.CraButtonPanel;
import br.com.ieptbto.cra.component.dataTable.CraDataTable;
import br.com.ieptbto.cra.component.dataTable.CraLinksPanel;
import br.com.ieptbto.cra.component.dataTable.TypeCraButton;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.dataProvider.BatimentoRetornoProvider;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.view.ViewBatimentoRetorno;
import br.com.ieptbto.cra.enumeration.TipoBatimento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class GerarRetornoPage extends BasePage<Retorno> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	DownloadMediator downloadMediator;
	@SpringBean
	RetornoMediator retornoMediator;
	private Retorno retorno;
	private BatimentoRetornoProvider dataProvider;

	public GerarRetornoPage() {
		this.retorno = new Retorno();
		adicionarComponentes();
	}
	
	public GerarRetornoPage(String message) {
		this.retorno = new Retorno();
		success(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formularioGerarRetorno());
		add(dataTableRetorno());
	}

	private Form<ViewBatimentoRetorno> formularioGerarRetorno() {
		Form<ViewBatimentoRetorno> formRetorno = new Form<ViewBatimentoRetorno>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				List<ViewBatimentoRetorno> arquivos = dataProvider.getList();
				
				try {
					if (retornoMediator.verificarArquivoRetornoGeradoCra().equals(true)) {
						throw new InfraException("Não é possível gerar os retornos novamente, arquivos já liberados hoje!");
					}
					if (arquivos.isEmpty()) {
						throw new InfraException("Não há retornos pendentes para envio !");
					}
					retornoMediator.gerarRetornos(getUser(), arquivos);
					setResponsePage(new RelatorioRetornoPage("Os arquivos de retorno foram gerados com sucesso !", "GERAR RETORNO"));

				} catch (InfraException e) {
					logger.error(e.getMessage(), e);
					error(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível gerar os arquivos de retorno. Favor entrar em contato com a CRA...");
				}
			}
		};
		return formRetorno;
	}

	private CraDataTable<ViewBatimentoRetorno> dataTableRetorno() {
		dataProvider = new BatimentoRetornoProvider(retornoMediator.buscarRetornoConfirmados());
		List<IColumn<ViewBatimentoRetorno, String>> columns = new ArrayList<IColumn<ViewBatimentoRetorno, String>>();
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("DOWNLOAD"), "download"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimentoRetorno>> item, String id,final IModel<ViewBatimentoRetorno> model) {
				item.add(new CraButtonPanel<ViewBatimentoRetorno>(id, model, TypeCraButton.DOWNLOAD_FILE, new ILinkListener() {

		            private static final long serialVersionUID = 1L;

		            @Override
		            public void onLinkClicked() {
		            	Remessa remessa = remessaMediator.buscarRemessaPorPK(model.getObject().getIdRemessa_Remessa());

		            	try {
							File file = downloadMediator.baixarRemessaTXT(getUser(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);

							getRequestCycle().scheduleRequestHandlerAfterCurrent(
									new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA...");
						}
		            }
		        }));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center";
			}
		});
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("RELATÓRIO"), "relatorio"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimentoRetorno>> item, String id,final IModel<ViewBatimentoRetorno> model) {
				item.add(new CraButtonPanel<ViewBatimentoRetorno>(id, model, TypeCraButton.REPORT, new ILinkListener() {

		            private static final long serialVersionUID = 1L;

		            @Override
		            public void onLinkClicked() {
		            	Remessa remessa = remessaMediator.buscarRemessaPorPK(model.getObject().getIdRemessa_Remessa());

		            	try {
		            		JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoCartorio(remessa);
		            		File pdf = File.createTempFile("report", ".pdf");
		            		JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
		            		IResourceStream resourceStream = new FileResourceStream(pdf);
		            		getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
		            				"CRA_RELATORIO_" + remessa.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
		            	} catch (InfraException ex) {
		            		error(ex.getMessage());
		            	} catch (Exception e) {
		            		error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
		            		logger.info(e.getMessage(), e);
		            	}
		            }
		        }));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center";
			}
		});
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("INSTITUIÇÃO"), "nomeFantasia_Instituicao"));
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("ARQUIVO"), "nomeArquivo_Arquivo") {
			
			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimentoRetorno>> item, String id, IModel<ViewBatimentoRetorno> model) {
				item.add(new CraLinksPanel(id, model.getObject().getNomeArquivo_Arquivo(), model.getObject().getIdArquivo_Arquivo()));
			}
			
			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("DATA"), "dataEnvio_Arquivo"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimentoRetorno>> item, String id, IModel<ViewBatimentoRetorno> model) {
				item.add(new Label(id, DataUtil.localDateToString(model.getObject().getDataEnvio_Arquivo())));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center";
			}
		});
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("HORA"), "horaEnvio_Arquivo"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimentoRetorno>> item, String id, IModel<ViewBatimentoRetorno> model) {
				item.add(new Label(id, DataUtil.localTimeToString(model.getObject().getHoraEnvio_Arquivo())));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center";
			}
		});
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("ENVIADO POR"), "nomeFantasia_Cartorio"));
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("VALOR PAGOS"), "totalValorlPagos"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimentoRetorno>> item, String id, IModel<ViewBatimentoRetorno> model) {
				item.add(new LabelValorMonetario<>(id, model.getObject().getTotalValorlPagos()));
			}

			@Override
			public String getCssClass() {
				return "col-right valor";
			}
		});
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("CUSTAS CART."), "totalCustasCartorio"){
			
			/***/
			private static final long serialVersionUID = 1L;
			
			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimentoRetorno>> item, String id, IModel<ViewBatimentoRetorno> model) {
				item.add(new LabelValorMonetario<>(id, model.getObject().getTotalCustasCartorio()));
			}
			
			@Override
			public String getCssClass() {
				return "col-right valor";
			}
		});		
		columns.add(new PropertyColumn<ViewBatimentoRetorno, String>(new Model<String>("VOLTAR BATIMENTO"), "voltarBatimento"){
			
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewBatimentoRetorno>> item, String id,final IModel<ViewBatimentoRetorno> model) {
				item.add(new CraButtonPanel<ViewBatimentoRetorno>(id, model, TypeCraButton.RETURN, new ILinkListener() {

		            private static final long serialVersionUID = 1L;

		            @Override
		            public void onLinkClicked() {
		            	Remessa remessa = remessaMediator.buscarRemessaPorPK(model.getObject().getIdRemessa_Remessa());
		            	
						try {
							TipoBatimento tipoBatimento = remessa.getInstituicaoDestino().getTipoBatimento();
							if (TipoBatimento.BATIMENTO_REALIZADO_PELA_CRA.equals(tipoBatimento)) {
								retornoMediator.retornarArquivoRetornoParaBatimento(remessa);
								setResponsePage(new GerarRetornoPage("O arquivo " + remessa.getArquivo().getNomeArquivo() + " do "
										+ remessa.getInstituicaoOrigem().getNomeFantasia() + " foi retornado ao batimento!"));
							} else if (TipoBatimento.BATIMENTO_REALIZADO_PELA_INSTITUICAO.equals(tipoBatimento)) {
								retornoMediator.retornarArquivoRetornoParaAguardandoLiberacao(remessa);
								setResponsePage(new GerarRetornoPage("O arquivo " + remessa.getArquivo().getNomeArquivo() + " do "
										+ remessa.getInstituicaoOrigem().getNomeFantasia() + " foi retornado para aguardando liberação!"));
							}
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
							logger.info(ex.getMessage(), ex);
						} catch (Exception ex) {
							getFeedbackPanel().error("Não foi possível cancelar o batimento do arquivo de retorno selecionado!");
							logger.info(ex.getMessage(), ex);
						}
		            }
				}));
			}

			@Override
			public String getCssClass() {
				return "col-center text-center";
			}
		});
		CraDataTable<ViewBatimentoRetorno> dataTable = new CraDataTable<ViewBatimentoRetorno>("dataTable", new Model<ViewBatimentoRetorno>(), columns, dataProvider);
		dataTable.setOutputMarkupPlaceholderTag(true);
		dataTable.setOutputMarkupId(true);
		return dataTable;
	}
	
	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}
}
