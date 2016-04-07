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
import org.apache.wicket.markup.html.form.Button;
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
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoPage;
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
	private static final Logger logger = Logger.getLogger(GerarRetornoPage.class);

	@SpringBean
	RetornoMediator retornoMediator;

	private Retorno retorno;
	private List<Remessa> retornosPendentes;

	public GerarRetornoPage() {
		this.retorno = new Retorno();
		this.retornosPendentes = retornoMediator.buscarRetornosConfirmados();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioGerarRetorno();

	}

	private void formularioGerarRetorno() {
		Form<Retorno> formRetorno = new Form<Retorno>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {

				try {
					if (retornoMediator.verificarArquivoRetornoGeradoCra().equals(true)) {
						throw new InfraException("Não é possível gerar os retornos novamente, arquivos já liberados hoje !");
					}
					if (getRetornosPendentes().isEmpty()) {
						throw new InfraException("Não há retornos pendentes para envio !");
					}
					retornoMediator.gerarRetornos(getUser(), getRetornosPendentes());
					setResponsePage(new RelatorioRetornoPage("Os arquivos de retorno foram gerados com sucesso !", "GERAR RETORNO"));

				} catch (InfraException e) {
					logger.error(e.getMessage(), e);
					error(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível gerar os arquivos de retorno! Entre em contato com a CRA.");
				}
			}
		};
		formRetorno.add(carregarListaRetornos());
		formRetorno.add(new Button("botaoRetorno"));
		add(formRetorno);
	}

	private ListView<Remessa> carregarListaRetornos() {
		return new ListView<Remessa>("retornos", getRetornosPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa retorno = item.getModelObject();

				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(retorno.getArquivo().getHoraEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				item.add(new Label("sequencialCabecalho", retorno.getCabecalho().getNumeroSequencialRemessa()));
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
				BigDecimal valorPagos = retornoMediator.buscarValorDeTitulosPagos(retorno);
				if (valorPagos == null || valorPagos.equals(BigDecimal.ZERO)) {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", BigDecimal.ZERO));
				} else {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", valorPagos));
				}

				BigDecimal valorCustas = retornoMediator.buscarValorDeCustasCartorio(retorno);
				if (valorCustas == null || valorCustas.equals(BigDecimal.ZERO)) {
					item.add(new LabelValorMonetario<BigDecimal>("valorCustas", BigDecimal.ZERO));
				} else {
					item.add(new LabelValorMonetario<BigDecimal>("valorCustas", valorCustas));
				}
				item.add(botaoGerarRelatorio(retorno));
				item.add(removerConfirmado(retorno));
			}

			private Link<Arquivo> removerConfirmado(final Remessa retorno) {
				return new Link<Arquivo>("removerConfirmado") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							retornoMediator.removerBatimento(retorno);
							setResponsePage(new MensagemPage<Remessa>(GerarRetornoPage.class, "GERAR RETORNO", "O arquivo "
									+ retorno.getArquivo().getNomeArquivo() + " do " + retorno.getInstituicaoOrigem().getNomeFantasia()
									+ " foi retornado ao batimento!"));

						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
							System.out.println(ex.getMessage() + ex.getCause());
						} catch (Exception ex) {
							getFeedbackPanel().error("Não foi possível cancelar o batimento do arquivo de retorno selecionado!");
							System.out.println(ex.getMessage() + ex.getCause());
						}
					}
				};
			}

			private Link<Remessa> botaoGerarRelatorio(final Remessa retorno) {
				return new Link<Remessa>("gerarRelatorio") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoCartorio(retorno);
							File pdf = File.createTempFile("report", ".pdf");
							JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
							IResourceStream resourceStream = new FileResourceStream(pdf);
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_"
									+ retorno.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
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

	public List<Remessa> getRetornosPendentes() {
		if (retornosPendentes == null) {
			retornosPendentes = new ArrayList<Remessa>();
		}
		return retornosPendentes;
	}

	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}
}
