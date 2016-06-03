package br.com.ieptbto.cra.page.batimento;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
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
public class RetornosLiberadosPage extends BasePage<Retorno> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RetornoMediator retornoMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private Retorno retorno;
	private List<Remessa> arquivosAguardandoLiberacao;
	private TextField<String> campoDataBatimento;
	private LocalDate dataBatimento;

	public RetornosLiberadosPage() {
		this.retorno = new Retorno();

		adicionarComponentes();
	}

	public RetornosLiberadosPage(LocalDate dataBatimento) {
		this.retorno = new Retorno();
		this.dataBatimento = dataBatimento;
		this.arquivosAguardandoLiberacao = retornoMediator.buscarRetornosParaPagamentoInstituicao(dataBatimento);

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formBatimentoLiberacao();
		carregarListaRetornos();
	}

	private void formBatimentoLiberacao() {
		Form<Batimento> formFiltros = new Form<Batimento>("formFiltros") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				LocalDate dataBatimento = null;

				try {
					dataBatimento = DataUtil.stringToLocalDate(campoDataBatimento.getModelObject().toString());
					setResponsePage(new RetornosLiberadosPage(dataBatimento));

				} catch (InfraException ex) {
					error(ex.getMessage());
					System.out.println(ex.getMessage() + ex.getCause());
				} catch (Exception ex) {
					error("Não foi possível buscar os arquivos para serem liberados!");
					System.out.println(ex.getMessage() + ex.getCause());
				}
			}
		};
		formFiltros.add(campoDataBatimento());
		add(formFiltros);
	}

	private TextField<String> campoDataBatimento() {
		if (dataBatimento != null) {
			return campoDataBatimento = new TextField<String>("dataBatimento", new Model<String>(DataUtil.localDateToString(dataBatimento)));
		} else {
			campoDataBatimento = new TextField<String>("dataBatimento", new Model<String>());
		}
		campoDataBatimento.setRequired(true);
		campoDataBatimento.setLabel(new Model<String>("Data do Batimento"));
		return campoDataBatimento;
	}

	private void carregarListaRetornos() {
		add(new ListView<Remessa>("retornos", getArquivosAguardandoLiberacao()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Remessa> item) {
				final Remessa retorno = item.getModelObject();

				item.add(new Label("banco", retorno.getInstituicaoDestino().getNomeFantasia()));
				item.add(new Label("dataBatimento", DataUtil.localDateToString(retorno.getBatimento().getData())));
				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("horaEnvio", DataUtil.localTimeToString(retorno.getArquivo().getHoraEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				final BigDecimal valorPagos = retornoMediator.buscarValorDeTitulosPagos(retorno);
				if (valorPagos == null || valorPagos.equals(BigDecimal.ZERO)) {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", BigDecimal.ZERO));
				} else {
					item.add(new LabelValorMonetario<BigDecimal>("valorPagos", valorPagos));
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

				item.add(botaoGerarRelatorio(retorno));
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
							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
									"CRA_RELATORIO_" + retorno.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
							e.printStackTrace();
						}
					}
				};
			}
		});
	}

	public List<Remessa> getArquivosAguardandoLiberacao() {
		if (arquivosAguardandoLiberacao == null) {
			arquivosAguardandoLiberacao = new ArrayList<Remessa>();
		}
		return arquivosAguardandoLiberacao;
	}

	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}
}
