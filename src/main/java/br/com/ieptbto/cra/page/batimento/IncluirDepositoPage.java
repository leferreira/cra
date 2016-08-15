package br.com.ieptbto.cra.page.batimento;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
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

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoDeposito;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
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
public class IncluirDepositoPage extends BasePage<Deposito> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	BatimentoMediator batimentoMediator;
	@SpringBean
	RetornoMediator retornoMediator;

	private Deposito deposito;
	private List<Deposito> depositos;

	public IncluirDepositoPage(Deposito deposito, List<Deposito> depositos) {
		this.deposito = deposito;
		this.depositos = depositos;
		adicionarComponentes();
	}

	public IncluirDepositoPage(String message, Deposito deposito, List<Deposito> depositos) {
		this.deposito = deposito;
		this.depositos = depositos;
		success(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormulario();
		listRetornosVinculados();
	}

	private void carregarFormulario() {
		Form<Deposito> form = new Form<Deposito>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Deposito deposito = getModelObject();

				try {
					batimentoMediator.atualizarInformacoesDeposito(deposito);
					setResponsePage(new IncluirDepositoPage("Informações do depósito foram atualizadas com sucesso!", getDeposito(), getDepositos()));

				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(dataImportacao());
		form.add(data());
		form.add(numeroDocumento());
		form.add(valor());
		form.add(situacaoDeposito());
		form.add(tipoDeposito());
		form.add(descricao());
		form.add(new Link<Deposito>("botaoVoltar") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ListaDepositoPage(getDepositos()));
			}
		});
		add(form);
	}

	private RadioChoice<SituacaoDeposito> situacaoDeposito() {
		IChoiceRenderer<SituacaoDeposito> renderer = new ChoiceRenderer<SituacaoDeposito>("label");
		List<SituacaoDeposito> list = new ArrayList<SituacaoDeposito>(Arrays.asList(SituacaoDeposito.values()));
		RadioChoice<SituacaoDeposito> comboSituacao = new RadioChoice<SituacaoDeposito>("situacaoDeposito", list, renderer);
		return comboSituacao;
	}

	private DropDownChoice<TipoDeposito> tipoDeposito() {
		IChoiceRenderer<TipoDeposito> renderer = new ChoiceRenderer<TipoDeposito>("label");
		List<TipoDeposito> list = new ArrayList<TipoDeposito>(Arrays.asList(TipoDeposito.values()));
		DropDownChoice<TipoDeposito> comboTipoDeposito = new DropDownChoice<TipoDeposito>("tipoDeposito", list, renderer);
		return comboTipoDeposito;
	}

	private TextField<BigDecimal> valor() {
		TextField<BigDecimal> textField = new TextField<BigDecimal>("valorCredito");
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> numeroDocumento() {
		TextField<String> textField = new TextField<String>("numeroDocumento");
		textField.setEnabled(false);
		return textField;
	}

	private TextField<String> dataImportacao() {
		return new TextField<String>("dataImportacao", new Model<String>(DataUtil.localDateToString(getDeposito().getData())));
	}

	private TextField<String> data() {
		TextField<String> textField = new TextField<String>("data", new Model<String>(DataUtil.localDateToString(getDeposito().getData())));
		return textField;
	}

	private TextArea<String> descricao() {
		return new TextArea<String>("descricao");
	}

	public void listRetornosVinculados() {
		add(new ListView<Remessa>("listRetornosVinculados", buscarRetornos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa retorno = item.getModelObject();

				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
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

	public Deposito getDeposito() {
		return deposito;
	}

	public List<Deposito> getDepositos() {
		return depositos;
	}

	public IModel<List<Remessa>> buscarRetornos() {
		return new LoadableDetachableModel<List<Remessa>>() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Remessa> load() {
				return batimentoMediator.carregarRetornosVinculados(deposito);
			}
		};
	}

	@Override
	protected IModel<Deposito> getModel() {
		return new CompoundPropertyModel<Deposito>(deposito);
	}
}
