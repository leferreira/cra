package br.com.ieptbto.cra.page.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.enumeration.TipoRelatorio;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
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
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioArquivosPage extends BasePage<Remessa> {

    /***/
    private static final long serialVersionUID = 1L;
    @SpringBean
    private InstituicaoMediator instituicaoMediator;
    private Remessa remessa;
    private List<Instituicao> listaInstituicoes;
    private RadioChoice<TipoArquivoEnum> radioTipoArquivo;
    private RadioChoice<TipoRelatorio> radioTipoRelatorio;
    private DropDownChoice<Instituicao> comboInstituicao;
    private TextField<LocalDate> dataEnvioInicio;
    private TextField<LocalDate> dataEnvioFinal;

    public RelatorioArquivosPage() {
	this.remessa = new Remessa();

	carregarFormularioArquivoCartorio();
    }

    private void carregarFormularioArquivoCartorio() {
	Form<Remessa> form = new Form<Remessa>("form") {

	    /***/
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit() {
		TipoArquivoEnum tipoArquivo = radioTipoArquivo.getModelObject();
		TipoRelatorio tipoRelatorio = radioTipoRelatorio.getModelObject();
		Instituicao instituicao = comboInstituicao.getModelObject();
		LocalDate dataInicio = null;
		LocalDate dataFim = null;

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

		    JasperPrint jasperPrint = new RelatorioUtil().gerarRelatorioArquivos(tipoArquivo, tipoRelatorio, instituicao, dataInicio, dataFim);
		    File pdf = File.createTempFile("report", ".pdf");
		    JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
		    IResourceStream resourceStream = new FileResourceStream(pdf);
		    getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_"
			    + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));
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
	form.add(tipoRelatorio());
	form.add(tipoArquivo());
	form.add(tipoInstituicao());
	form.add(instituicaoCartorio());
	add(form);
    }

    private TextField<LocalDate> dataEnvioInicio() {
	dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
	dataEnvioInicio.setLabel(new Model<String>("Período de datas"));
	dataEnvioInicio.setRequired(true);
	dataEnvioInicio.setMarkupId("date");
	return dataEnvioInicio;
    }

    private TextField<LocalDate> dataEnvioFinal() {
	dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	dataEnvioFinal.setMarkupId("date1");
	return dataEnvioFinal;
    }

    private RadioChoice<TipoArquivoEnum> tipoArquivo() {
	List<TipoArquivoEnum> listaTipos = new ArrayList<TipoArquivoEnum>();
	listaTipos.add(TipoArquivoEnum.REMESSA);
	listaTipos.add(TipoArquivoEnum.CONFIRMACAO);
	listaTipos.add(TipoArquivoEnum.RETORNO);
	radioTipoArquivo = new RadioChoice<TipoArquivoEnum>("tipoArquivos", new Model<TipoArquivoEnum>(), listaTipos);
	radioTipoArquivo.setLabel(new Model<String>("Tipo do Arquivo"));
	radioTipoArquivo.setRequired(true);
	return radioTipoArquivo;
    }

    private RadioChoice<TipoRelatorio> tipoRelatorio() {
	List<TipoRelatorio> choices = Arrays.asList(TipoRelatorio.values());
	radioTipoRelatorio = new RadioChoice<TipoRelatorio>("tipoRelatorio", new Model<TipoRelatorio>(), choices);
	radioTipoRelatorio.setLabel(new Model<String>("Tipo Relatório"));
	radioTipoRelatorio.setRequired(true);
	return radioTipoRelatorio;
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
	tipoInstituicao.setRequired(true);
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

    @Override
    protected IModel<Remessa> getModel() {
	return new CompoundPropertyModel<Remessa>(remessa);
    }
}