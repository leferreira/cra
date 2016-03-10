package br.com.ieptbto.cra.page.cra;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
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
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.enumeration.TipoDeposito;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
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
public class LiberarRetornoPage extends BasePage<Retorno> {

    /***/
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LiberarRetornoPage.class);

    @SpringBean
    private RetornoMediator retornoMediator;
    @SpringBean
    private InstituicaoMediator instituicaoMediator;
    private Retorno retorno;
    private List<Remessa> arquivosAguardandoLiberacao;
    private ListView<Remessa> remessas;
    private DropDownChoice<Instituicao> campoInstituicao;
    private TextField<String> campoDataBatimento;
    private LocalDate dataBatimento;
    private Instituicao instituicao;
    private boolean dataComoDataLimite;

    private CheckBox checkDataLimite;
    private Label labelTotalArquivos;
    private Label labelNaoSelecionados;
    private Label labelValorNaoSelecionados;
    private Label labelSelecionados;
    private Label labelValorSelecionados;
    private BigDecimal totalNaoSelecionados;

    public LiberarRetornoPage() {
	this.retorno = new Retorno();

	adicionarFiltros();
	carregarResumoArquivos();
	carregarGuiaRetorno();
    }

    public LiberarRetornoPage(LocalDate dataBatimento, Instituicao instituicao, boolean dataComoDataLimite) {
	this.retorno = new Retorno();
	this.dataBatimento = dataBatimento;
	this.instituicao = instituicao;
	this.dataComoDataLimite = dataComoDataLimite;
	this.arquivosAguardandoLiberacao = retornoMediator.buscarRetornosAguardandoLiberacao(instituicao, dataBatimento, dataComoDataLimite);
	this.totalNaoSelecionados = retornoMediator.buscarSomaValorTitulosPagosRemessas(instituicao, dataBatimento, dataComoDataLimite);

	adicionarFiltros();
	carregarResumoArquivos();
	carregarGuiaRetorno();
    }

    private void adicionarFiltros() {
	Form<Batimento> formFiltros = new Form<Batimento>("formFiltros") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		LocalDate dataBatimento = null;
		Instituicao instituicao = null;
		boolean dataComoDataLimite = false;

		try {
		    if (campoDataBatimento.getModelObject() == null) {
			throw new InfraException("O campo 'Data Batimento' deve ser preenchido pois é obrigatório!");
		    }
		    if (campoInstituicao.getModelObject() == null) {
			throw new InfraException("O campo 'Bancos e Convênios' deve ser preenchido pois é obrigatório!");
		    }
		    if (checkDataLimite.getModelObject() != null) {
			dataComoDataLimite = checkDataLimite.getModelObject();
		    }
		    dataBatimento = DataUtil.stringToLocalDate(campoDataBatimento.getModelObject().toString());
		    instituicao = campoInstituicao.getModelObject();
		    setResponsePage(new LiberarRetornoPage(dataBatimento, instituicao, dataComoDataLimite));

		} catch (InfraException ex) {
		    error(ex.getMessage());
		    System.out.println(ex.getMessage() + ex.getCause());
		} catch (Exception ex) {
		    error("Não foi possível buscar os arquivos para serem liberados!");
		    System.out.println(ex.getMessage() + ex.getCause());
		}
	    }
	};
	formFiltros.add(campoInstituicao());
	formFiltros.add(campoDataBatimento());
	formFiltros.add(checkUsarDataComoDataLimite());
	add(formFiltros);
    }

    private CheckBox checkUsarDataComoDataLimite() {
	if (dataComoDataLimite != false) {
	    checkDataLimite = new CheckBox("dataComoDataLimite", new Model<Boolean>(true));
	    checkDataLimite.setDefaultModelObject(true);
	    return checkDataLimite;
	}
	return checkDataLimite = new CheckBox("dataComoDataLimite", new Model<Boolean>());
    }

    private DropDownChoice<Instituicao> campoInstituicao() {
	IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
	if (instituicao == null) {
	    campoInstituicao = new DropDownChoice<Instituicao>("instituicaoEnvio", new Model<Instituicao>(), instituicaoMediator.getInstituicoesFinanceirasEConvenios(), renderer);
	}
	campoInstituicao = new DropDownChoice<Instituicao>("instituicaoEnvio", new Model<Instituicao>(instituicao), instituicaoMediator.getInstituicoesFinanceirasEConvenios(), renderer);
	return campoInstituicao;
    }

    private TextField<String> campoDataBatimento() {
	if (dataBatimento != null) {
	    return campoDataBatimento = new TextField<String>("dataBatimento", new Model<String>(DataUtil.localDateToString(dataBatimento)));
	}
	return campoDataBatimento = new TextField<String>("dataBatimento", new Model<String>());
    }

    private void carregarResumoArquivos() {
	this.labelTotalArquivos = new Label("labelTotalArquivos", getArquivosAguardandoLiberacao().size());
	this.labelNaoSelecionados = new Label("labelNaoSelecionados", getArquivosAguardandoLiberacao().size());
	if (totalNaoSelecionados == null) {
	    this.labelValorNaoSelecionados = new LabelValorMonetario<BigDecimal>("labelValorNaoSelecionados", BigDecimal.ZERO);
	} else {
	    this.labelValorNaoSelecionados = new LabelValorMonetario<BigDecimal>("labelValorNaoSelecionados", totalNaoSelecionados);
	}
	this.labelSelecionados = new Label("labelSelecionados", 0);
	this.labelValorSelecionados = new LabelValorMonetario<BigDecimal>("labelValorSelecionados", new BigDecimal(0));

	labelNaoSelecionados.setOutputMarkupId(true);
	labelValorNaoSelecionados.setOutputMarkupId(true);
	labelSelecionados.setOutputMarkupId(true);
	labelValorSelecionados.setOutputMarkupId(true);

	add(labelTotalArquivos);
	add(labelNaoSelecionados);
	add(labelValorNaoSelecionados);
	add(labelSelecionados);
	add(labelValorSelecionados);
    }

    private void carregarGuiaRetorno() {
	final CheckGroup<Remessa> grupo = new CheckGroup<Remessa>("group", new ArrayList<Remessa>());
	Form<Retorno> form = new Form<Retorno>("form") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    protected void onSubmit() {
		List<Remessa> retornoLiberados = (List<Remessa>) grupo.getModelObject();

		try {
		    if (retornoLiberados.isEmpty()) {
			throw new InfraException("Nenhum arquivo de retorno foi selecionado!");
		    }

		    retornoMediator.liberarRetornoBatimentoInstituicao(retornoLiberados);
		    campoDataBatimento.setModelObject(null);
		    setResponsePage(new MensagemPage<Batimento>(LiberarRetornoPage.class, "LIBERAR RETORNO", "Os arquivos de retorno foram"
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

    private ListView<Remessa> carregarListaRetornos() {
	return remessas = new ListView<Remessa>("retornos", getArquivosAguardandoLiberacao()) {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(final ListItem<Remessa> item) {
		final Remessa retorno = item.getModelObject();

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

		final Check<Remessa> check = new Check<Remessa>("checkbox", new Model<Remessa>());
		check.setOutputMarkupId(true);
		check.add(new AjaxEventBehavior("onchange") {

		    /***/
		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onEvent(AjaxRequestTarget target) {
			int naoSelecionados = Integer.parseInt(labelNaoSelecionados.getDefaultModelObjectAsString());
			BigDecimal valorNaoSelecionados = BigDecimal.class.cast(labelValorNaoSelecionados.getDefaultModelObject());
			int selecionados = Integer.parseInt(labelSelecionados.getDefaultModelObjectAsString());
			BigDecimal valorSelecionados = BigDecimal.class.cast(labelValorSelecionados.getDefaultModelObject());

			if (check.getModelObject() == null) {
			    BigDecimal resultadoValorNaoSelecionados = valorNaoSelecionados.subtract(valorPagos);
			    BigDecimal resultadoValorSelecionados = valorSelecionados.add(valorPagos);
			    labelNaoSelecionados.setDefaultModelObject(Integer.toString(naoSelecionados - 1));
			    labelValorNaoSelecionados.setDefaultModelObject(resultadoValorNaoSelecionados);
			    labelSelecionados.setDefaultModelObject(Integer.toString(selecionados + 1));
			    labelValorSelecionados.setDefaultModelObject(resultadoValorSelecionados);
			    check.setModelObject(item.getModelObject());
			} else {
			    BigDecimal resultadoValorNaoSelecionados = valorNaoSelecionados.add(valorPagos);
			    BigDecimal resultadoValorSelecionados = valorSelecionados.subtract(valorPagos);
			    labelNaoSelecionados.setDefaultModelObject(Integer.toString(naoSelecionados + 1));
			    labelValorNaoSelecionados.setDefaultModelObject(resultadoValorNaoSelecionados);
			    labelSelecionados.setDefaultModelObject(Integer.toString(selecionados - 1));
			    labelValorSelecionados.setDefaultModelObject(resultadoValorSelecionados);
			    check.setModelObject(null);
			}

			target.add(labelNaoSelecionados);
			target.add(labelValorNaoSelecionados);
			target.add(labelSelecionados);
			target.add(labelValorSelecionados);
		    }
		});
		item.add(check);
		item.add(removerConfirmado(retorno));
		item.add(botaoGerarRelatorio(retorno));

		for (BatimentoDeposito batimentoDeposito : retorno.getBatimento().getDepositosBatimento()) {
		    if (batimentoDeposito.getDeposito().getTipoDeposito() != null) {
			if (batimentoDeposito.getDeposito().getTipoDeposito().equals(TipoDeposito.DEPOSITO_CARTORIO_PARA_BANCO)) {
			    item.setMarkupId("retornoLiberado");
			}
		    }
		}
	    }

	    private Link<Arquivo> removerConfirmado(final Remessa retorno) {
		return new Link<Arquivo>("removerConfirmado") {

		    /***/
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {

			try {
			    retornoMediator.removerBatimento(retorno);
			    getArquivosAguardandoLiberacao().remove(retorno);
			    info("O arquivo foi removido com sucesso e voltou ao batimento!");

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
