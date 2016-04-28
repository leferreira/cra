package br.com.ieptbto.cra.page.batimento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.enumeration.TipoDeposito;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class BuscarDepositoPage extends BasePage<Deposito> {

	/***/
	private static final long serialVersionUID = 1L;

	private Deposito deposito;

	private TextField<LocalDate> fieldDataInicio;
	private TextField<LocalDate> fieldDataFinal;

	public BuscarDepositoPage() {
		this.deposito = new Deposito();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		formularioBuscarDepositos();

	}

	private void formularioBuscarDepositos() {
		Form<Deposito> form = new Form<Deposito>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Deposito deposito = getModelObject();
				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				try {
					if (fieldDataInicio.getDefaultModelObject() != null) {
						if (fieldDataFinal.getDefaultModelObject() != null) {
							dataInicio = DataUtil.stringToLocalDate(fieldDataInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(fieldDataFinal.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						} else
							throw new InfraException("As duas datas devem ser preenchidas.");
					}

					setResponsePage(new ListaDepositoPage(deposito, dataInicio, dataFim));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(dataInicial());
		form.add(dataFinal());
		form.add(numeroDocumento());
		form.add(valor());
		form.add(situacaoDeposito());
		form.add(tipoDeposito());
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
		return new TextField<BigDecimal>("valorCredito");
	}

	private TextField<String> numeroDocumento() {
		return new TextField<String>("numeroDocumento");
	}

	private TextField<LocalDate> dataFinal() {
		fieldDataFinal = new TextField<LocalDate>("dataFinal", new Model<LocalDate>());
		return fieldDataFinal;
	}

	private TextField<LocalDate> dataInicial() {
		fieldDataInicio = new TextField<LocalDate>("dataInicial", new Model<LocalDate>());
		fieldDataInicio.setLabel(new Model<String>("Período de Datas"));
		fieldDataInicio.setRequired(true);
		return fieldDataInicio;
	}

	@Override
	protected IModel<Deposito> getModel() {
		return new CompoundPropertyModel<Deposito>(deposito);
	}
}
