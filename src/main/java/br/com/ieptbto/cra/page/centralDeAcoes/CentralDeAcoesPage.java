package br.com.ieptbto.cra.page.centralDeAcoes;

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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.dataProvider.DataProvider;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
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
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	LoggerMediator loggerMediator;

	private LocalDate dataInicio;
	private LocalDate dataFim;
	private Instituicao instituicao;
	private List<Instituicao> listaInstituicoes;

	private TextField<String> dataEnvioInicio;
	private TextField<String> dataEnvioFinal;
	private DropDownChoice<Instituicao> dropDownBancosConveniosCartorios;

	public CentralDeAcoesPage() {
		this.dataInicio = new LocalDate();
		this.dataFim = new LocalDate();
		adicionarComponentes();
	}

	public CentralDeAcoesPage(LocalDate dataInicio, LocalDate dataFim, Instituicao instituicao) {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.instituicao = instituicao;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioConsultaAcoes();
		dataTableAcoes();
	}

	private void formularioConsultaAcoes() {
		Form<LogCra> form = new Form<LogCra>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				if (dropDownBancosConveniosCartorios.getModelObject() != null) {
					instituicao = dropDownBancosConveniosCartorios.getModelObject();
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
					setResponsePage(new CentralDeAcoesPage(dataInicio, dataFim, instituicao));

				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
		form.add(textFieldDataEnvioInicio());
		form.add(textFieldDataEnvioFinal());
		form.add(textFieldTipoInstituicao());
		form.add(dropDownInstituicaoBancosConvenios());
		add(form);
	}

	private TextField<String> textFieldDataEnvioInicio() {
		return dataEnvioInicio = new TextField<String>("dataEnvioInicio", new Model<String>(DataUtil.localDateToString(dataInicio)));
	}

	private TextField<String> textFieldDataEnvioFinal() {
		return dataEnvioFinal = new TextField<String>("dataEnvioFinal", new Model<String>(DataUtil.localDateToString(dataFim)));
	}

	private DropDownChoice<TipoInstituicaoCRA> textFieldTipoInstituicao() {
		List<TipoInstituicaoCRA> choices = new ArrayList<TipoInstituicaoCRA>();
		choices.add(TipoInstituicaoCRA.CARTORIO);
		choices.add(TipoInstituicaoCRA.CONVENIO);
		choices.add(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA);
		final DropDownChoice<TipoInstituicaoCRA> tipoInstituicao = new DropDownChoice<TipoInstituicaoCRA>("tipoInstituicao",
				new Model<TipoInstituicaoCRA>(), choices, new ChoiceRenderer<TipoInstituicaoCRA>("label"));
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

					dropDownBancosConveniosCartorios.setEnabled(true);
					dropDownBancosConveniosCartorios.setRequired(true);
				} else {
					dropDownBancosConveniosCartorios.setEnabled(false);
					dropDownBancosConveniosCartorios.setRequired(false);
					getListaInstituicoes().clear();
				}
				target.add(dropDownBancosConveniosCartorios);
			}
		});
		tipoInstituicao.setLabel(new Model<String>("Tipo Instituição"));
		return tipoInstituicao;
	}

	private DropDownChoice<Instituicao> dropDownInstituicaoBancosConvenios() {
		dropDownBancosConveniosCartorios = new DropDownChoice<Instituicao>("instituicaoOrigem", new Model<Instituicao>(instituicao),
				getListaInstituicoes(), new ChoiceRenderer<Instituicao>("nomeFantasia"));
		dropDownBancosConveniosCartorios.setLabel(new Model<String>("Banco/Convênio"));
		dropDownBancosConveniosCartorios.setOutputMarkupId(true);
		dropDownBancosConveniosCartorios.setEnabled(false);
		return dropDownBancosConveniosCartorios;
	}

	private void dataTableAcoes() {
		add(new DataTableAcoesPanel("panelDataTableAcoes", new DataProvider<LogCra>(buscarLoggs())));
	}

	public List<Instituicao> getListaInstituicoes() {
		if (listaInstituicoes == null) {
			listaInstituicoes = new ArrayList<Instituicao>();
		}
		return listaInstituicoes;
	}

	public List<LogCra> buscarLoggs() {
		return loggerMediator.buscarAcoes(dataInicio, dataFim, instituicao);
	}

	@Override
	protected IModel<LogCra> getModel() {
		return null;
	}
}
