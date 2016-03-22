package br.com.ieptbto.cra.page.centralDeAcoes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoLog;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.LoggerMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Aráujo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class CentralDeAcoesPage extends BasePage<LogCra> {

    /***/
    private static final long serialVersionUID = 1L;
    @SpringBean
    private InstituicaoMediator instituicaoMediator;
    @SpringBean
    private LoggerMediator loggerMediator;
    private LogCra logCra;
    private List<Instituicao> listaInstituicoes;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Instituicao instituicao;
    private Integer pagination;
    private TipoLog tipoLog;

    private Link<TipoLog> buttonLogSucess;
    private Link<TipoLog> buttonLogError;
    private Link<TipoLog> buttonLogAlert;
    private Link<TipoLog> buttonLogAll;
    private TextField<String> dataEnvioInicio;
    private TextField<String> dataEnvioFinal;
    private DropDownChoice<Instituicao> comboInstituicao;

    public CentralDeAcoesPage() {
	this.logCra = new LogCra();
	this.dataInicio = new LocalDate();
	this.dataFim = new LocalDate();
	this.tipoLog = null;
	this.instituicao = null;

	carregarCentralDeAcoes();
    }

    public CentralDeAcoesPage(LocalDate dataInicio, LocalDate dataFim, Instituicao instituicao, Integer itemsPerPage, TipoLog tipolog) {
	this.logCra = new LogCra();
	this.pagination = itemsPerPage;
	this.dataInicio = dataInicio;
	this.dataFim = dataFim;
	this.tipoLog = tipolog;
	this.instituicao = instituicao;

	carregarCentralDeAcoes();
    }

    private void carregarCentralDeAcoes() {
	formularioConsultaAcoes();
	listaLogAcoes();
	filtroSucessoErroAlertaTodos();
    }

    private void formularioConsultaAcoes() {
	Form<LogCra> form = new Form<LogCra>("form") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit() {
		if (comboInstituicao.getModelObject() != null) {
		    instituicao = comboInstituicao.getModelObject();
		}

		try {
		    if (dataEnvioInicio.getDefaultModelObject() != null) {
			if (dataEnvioFinal.getDefaultModelObject() != null) {
			    dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
			    dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
			    if (!dataInicio.isBefore(dataFim))
				if (!dataInicio.isEqual(dataFim))
				    throw new InfraException("A data de início deve ser antes da data fim.");
			} else
			    throw new InfraException("As duas datas devem ser preenchidas.");
		    }

		    setResponsePage(new CentralDeAcoesPage(dataInicio, dataFim, instituicao, pagination, tipoLog));
		} catch (InfraException ex) {
		    error(ex.getMessage());
		} catch (Exception e) {
		    error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
		    e.printStackTrace();
		}
	    }
	};
	form.add(dataEnvioInicio());
	form.add(dataEnvioFinal());
	form.add(tipoInstituicao());
	form.add(instituicaoCartorio());
	add(form);
    }

    private TextField<String> dataEnvioInicio() {
	return dataEnvioInicio = new TextField<String>("dataEnvioInicio", new Model<String>(DataUtil.localDateToString(dataInicio)));
    }

    private TextField<String> dataEnvioFinal() {
	return dataEnvioFinal = new TextField<String>("dataEnvioFinal", new Model<String>(DataUtil.localDateToString(dataFim)));
    }

    private DropDownChoice<TipoInstituicaoCRA> tipoInstituicao() {
	IChoiceRenderer<TipoInstituicaoCRA> renderer = new ChoiceRenderer<TipoInstituicaoCRA>("label");
	List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
	choices.add(TipoInstituicaoCRA.CARTORIO);
	choices.add(TipoInstituicaoCRA.CONVENIO);
	choices.add(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA);
	final DropDownChoice<TipoInstituicaoCRA> tipoInstituicao = new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao", new Model<TipoInstituicaoCRA>(), choices, renderer);
	tipoInstituicao.add(new OnChangeAjaxBehavior() {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		TipoInstituicaoCRA tipo = tipoInstituicao.getModelObject();

		if (tipoInstituicao.getModelObject() != null) {
		    if (tipo.equals(TipoInstituicaoCRA.CONVENIO)) {
			getListaInstituicoes().clear();
			getListaInstituicoes().addAll(instituicaoMediator.getConvenios());
		    } else if (tipo.equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
			getListaInstituicoes().clear();
			getListaInstituicoes().addAll(instituicaoMediator.getInstituicoesFinanceiras());
		    } else if (tipo.equals(TipoInstituicaoCRA.CARTORIO)) {
			getListaInstituicoes().clear();
			getListaInstituicoes().addAll(instituicaoMediator.getCartorios());
		    }

		    comboInstituicao.setEnabled(true);
		    comboInstituicao.setRequired(true);
		} else {
		    comboInstituicao.setEnabled(false);
		    comboInstituicao.setRequired(false);
		    getListaInstituicoes().clear();
		}
		target.add(comboInstituicao);
	    }
	});
	tipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
	return tipoInstituicao;
    }

    private DropDownChoice<Instituicao> instituicaoCartorio() {
	IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
	comboInstituicao = new DropDownChoice<Instituicao>("instituicaoOrigem", new Model<Instituicao>(), getListaInstituicoes(), renderer);
	comboInstituicao.setLabel(new Model<String>("Portador"));
	comboInstituicao.setOutputMarkupId(true);
	comboInstituicao.setEnabled(false);
	return comboInstituicao;
    }

    public List<Instituicao> getListaInstituicoes() {
	if (listaInstituicoes == null) {
	    listaInstituicoes = new ArrayList<Instituicao>();
	}
	return listaInstituicoes;
    }

    private void listaLogAcoes() {
	final PageableListView<LogCra> pageableListView = new PageableListView<LogCra>("listaAcoes", loggerMediator.buscarAcoes(dataInicio, dataFim, instituicao, tipoLog), getPagination()) {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void populateItem(ListItem<LogCra> item) {
		LogCra log = item.getModelObject();

		if (log.getInstituicao() != null) {
		    item.add(new Label("instituicao", log.getInstituicao().toUpperCase()));
		} else {
		    item.add(new Label("instituicao", "Central de Remessa de Arquivos".toUpperCase()));
		}
		item.add(new Label("tipoacao", log.getAcao().getLabel()));
		item.add(new Label("tipoLog", log.getTipoLog()).setMarkupId(log.getTipoLog().getIdHtml()));
		item.add(new Label("data", DataUtil.localDateToString(log.getData())));
		item.add(new Label("hora", DataUtil.localTimeToString(log.getHora())));
		item.add(new Label("descricao", log.getDescricao()));
	    }
	};
	pageableListView.setOutputMarkupId(true);
	add(new PagingNavigator("pager", pageableListView));
	add(pageableListView);

	Integer paginacao[] = { 5, 10, 25, 50, 100 };
	final DropDownChoice<Integer> dropDownQuantidadeOcorrencias = new DropDownChoice<Integer>("quantidadeOcorrencias", new Model<Integer>(pagination), Arrays.asList(paginacao));
	dropDownQuantidadeOcorrencias.add(new OnChangeAjaxBehavior() {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		setResponsePage(new CentralDeAcoesPage(dataInicio, dataFim, instituicao, dropDownQuantidadeOcorrencias.getModelObject(), tipoLog));
	    }
	});
	dropDownQuantidadeOcorrencias.setOutputMarkupId(true);
	add(dropDownQuantidadeOcorrencias);
    }

    private void filtroSucessoErroAlertaTodos() {
	buttonLogSucess = new Link<TipoLog>("buttonLogSucess") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		setResponsePage(new CentralDeAcoesPage(dataInicio, dataFim, instituicao, pagination, TipoLog.SUCESSO));
	    }
	};
	buttonLogError = new Link<TipoLog>("buttonLogError") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		setResponsePage(new CentralDeAcoesPage(dataInicio, dataFim, instituicao, pagination, TipoLog.OCORRENCIA_ERRO));
	    }
	};
	buttonLogAlert = new Link<TipoLog>("buttonLogAlert") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		setResponsePage(new CentralDeAcoesPage(dataInicio, dataFim, instituicao, pagination, TipoLog.ALERTA));
	    }
	};
	buttonLogAll = new Link<TipoLog>("buttonLogAll") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		setResponsePage(new CentralDeAcoesPage(dataInicio, dataFim, instituicao, pagination, null));
	    }
	};

	add(buttonLogSucess);
	add(buttonLogError);
	add(buttonLogAlert);
	add(buttonLogAll);
    }

    public Integer getPagination() {
	if (pagination == null) {
	    pagination = 5;
	}
	return pagination;
    }

    public LogCra getLogCra() {
	return logCra;
    }

    @Override
    protected IModel<LogCra> getModel() {
	return null;
    }
}
