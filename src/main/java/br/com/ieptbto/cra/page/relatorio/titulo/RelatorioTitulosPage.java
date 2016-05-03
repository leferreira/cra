package br.com.ieptbto.cra.page.relatorio.titulo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.enumeration.SituacaoTituloRelatorio;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
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
public class RelatorioTitulosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final String RELATORIO_PDF = "PDF";
	private static final String RELATORIO_CSV = "CSV";

	@SpringBean
	InstituicaoMediator instituicaoMediator;

	private Arquivo arquivo;

	private List<Instituicao> listaInstituicoes;
	private DropDownChoice<Instituicao> dropDownInstituicao;
	private DropDownChoice<Instituicao> dropDownCartorio;
	private RadioGroup<SituacaoTituloRelatorio> radioSituacaoTituloRelatorio;
	private RadioGroup<String> radioExportacao;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;

	public RelatorioTitulosPage() {
		this.arquivo = new Arquivo();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormulario();

	}

	private void carregarFormulario() {
		Form<Arquivo> form = new Form<Arquivo>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				SituacaoTituloRelatorio situacaoTitulo = radioSituacaoTituloRelatorio.getModelObject();
				Instituicao bancoConvenio = dropDownInstituicao.getModelObject();
				Instituicao cartorio = dropDownCartorio.getModelObject();
				String tipoExportacao = radioExportacao.getModelObject();
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

					if (dropDownInstituicao.getModelObject() == null && dropDownCartorio.getModelObject() == null) {
						throw new InfraException("Por favor, informe o Banco/Convênio ou a Comarca...");
					}

					if (tipoExportacao.equals(RELATORIO_PDF)) {
						JasperPrint jasperPrint =
								new RelatorioUtil().gerarRelatorioTitulosPorSituacao(situacaoTitulo, bancoConvenio, dataInicio, dataFim);
						File pdf = File.createTempFile("report", ".pdf");
						JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
						IResourceStream resourceStream = new FileResourceStream(pdf);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
								"CRA_RELATORIO_" + DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));

					} else if (tipoExportacao.equals(RELATORIO_CSV)) {
						setResponsePage(new RelatorioTitulosCsvPage(situacaoTitulo, bancoConvenio, cartorio, dataInicio, dataFim));
					}
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
		form.add(radioTipoExportacao());
		form.add(dataEnvioInicio());
		form.add(dataEnvioFinal());
		form.add(tipoInstituicao());
		form.add(dropDownBancoConvenios());
		form.add(dropDownCartorio());
		form.add(situcaoTituloRelatorio());
		add(form);
	}

	private RadioGroup<String> radioTipoExportacao() {
		radioExportacao = new RadioGroup<String>("exportacao", new Model<String>());
		radioExportacao.setLabel(new Model<String>("Tipo Exportação Relatório"));
		radioExportacao.setRequired(true);
		radioExportacao.add(new Radio<String>("pdf", new Model<String>(RELATORIO_PDF)).setEnabled(false));
		radioExportacao.add(new Radio<String>("csv", new Model<String>(RELATORIO_CSV)));
		return radioExportacao;
	}

	private RadioGroup<SituacaoTituloRelatorio> situcaoTituloRelatorio() {
		radioSituacaoTituloRelatorio = new RadioGroup<SituacaoTituloRelatorio>("situacao", new Model<SituacaoTituloRelatorio>());
		radioSituacaoTituloRelatorio.setLabel(new Model<String>("Situação dos Títulos"));
		radioSituacaoTituloRelatorio.setRequired(true);
		radioSituacaoTituloRelatorio
				.add(new Radio<SituacaoTituloRelatorio>("todos", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.GERAL)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("semConfirmacao",
				new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.SEM_CONFIRMACAO)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("comConfirmacao",
				new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.COM_CONFIRMACAO)));
		radioSituacaoTituloRelatorio.add(
				new Radio<SituacaoTituloRelatorio>("comRetorno", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.COM_RETORNO)));
		radioSituacaoTituloRelatorio
				.add(new Radio<SituacaoTituloRelatorio>("pagos", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.PAGOS)));
		radioSituacaoTituloRelatorio.add(
				new Radio<SituacaoTituloRelatorio>("protestados", new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.PROTESTADOS)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("retiradosDevolvidos",
				new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.RETIRADOS_DEVOLVIDOS)));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("desistencia",
				new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.DESISTÊNCIA_DE_PROTESTO)).setEnabled(false));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("cancelamento",
				new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.CANCELAMENTO_DE_PROTESTO)).setEnabled(false));
		radioSituacaoTituloRelatorio.add(new Radio<SituacaoTituloRelatorio>("semRetorno",
				new Model<SituacaoTituloRelatorio>(SituacaoTituloRelatorio.AUTORIZACAO_CANCELAMENTO)).setEnabled(false));
		return radioSituacaoTituloRelatorio;
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

	private DropDownChoice<TipoInstituicaoCRA> tipoInstituicao() {
		IChoiceRenderer<TipoInstituicaoCRA> renderer = new ChoiceRenderer<TipoInstituicaoCRA>("label");
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
		choices.add(TipoInstituicaoCRA.CONVENIO);
		choices.add(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA);
		final DropDownChoice<TipoInstituicaoCRA> tipoInstituicao =
				new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao", new Model<TipoInstituicaoCRA>(), choices, renderer);
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
					}

					dropDownInstituicao.setEnabled(true);
					dropDownInstituicao.setRequired(true);
				} else {
					dropDownInstituicao.setEnabled(false);
					dropDownInstituicao.setRequired(false);
					getListaInstituicoes().clear();
				}
				target.add(dropDownInstituicao);
			}
		});
		tipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
		return tipoInstituicao;
	}

	private DropDownChoice<Instituicao> dropDownBancoConvenios() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("nomeFantasia");
		dropDownInstituicao =
				new DropDownChoice<Instituicao>("instituicaoOrigem", new Model<Instituicao>(), getListaInstituicoes(), renderer);
		dropDownInstituicao.setLabel(new Model<String>("Portador"));
		dropDownInstituicao.setOutputMarkupId(true);
		dropDownInstituicao.setEnabled(false);
		return dropDownInstituicao;
	}

	private DropDownChoice<Instituicao> dropDownCartorio() {
		IChoiceRenderer<Instituicao> renderer = new ChoiceRenderer<Instituicao>("municipio.nomeMunicipio");
		dropDownCartorio =
				new DropDownChoice<Instituicao>("cartorio", new Model<Instituicao>(), instituicaoMediator.getCartorios(), renderer);
		dropDownCartorio.setOutputMarkupId(true);
		return dropDownCartorio;
	}

	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}