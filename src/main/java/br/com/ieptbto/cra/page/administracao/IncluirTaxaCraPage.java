package br.com.ieptbto.cra.page.administracao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.TaxaCra;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.TaxaCraMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class IncluirTaxaCraPage extends BasePage<TaxaCra> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TaxaCraMediator taxaCraMediator;

	private List<TaxaCra> taxasCra;
	private TaxaCra taxaCra;
	private TaxaCra taxaCraAtual;

	public IncluirTaxaCraPage() {
		this.taxaCra = new TaxaCra();
		this.taxaCraAtual = taxaCraMediator.buscarTaxaCraVigente(new LocalDate());
		adicionarComponentes();
	}

	public IncluirTaxaCraPage(String message) {
		this.taxaCra = new TaxaCra();
		this.taxaCraAtual = taxaCraMediator.buscarTaxaCraVigente(new LocalDate());
		success(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		listaTaxasAntigas();
		formTaxaCra();
	}

	private void formTaxaCra() {
		Form<TaxaCra> form = new Form<TaxaCra>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				TaxaCra taxaCra = getModelObject();
				TaxaCra taxaCraAtual = getTaxaCraAtual();
				taxaCra.setDataFim(new LocalDate().minusDays(1).toDate());

				try {
					if (taxaCra.getDataInicio() != null) {
						if (taxaCra.getDataFim() != null) {
							if (!taxaCra.getDataInicio().before(taxaCra.getDataFim()))
								if (!taxaCra.getDataInicio().equals(taxaCra.getDataFim()))
									throw new InfraException("A data de início da vigência deve ser antes da data fim.");
						} else
							throw new InfraException("As duas datas devem ser preenchidas.");
					}

					if (taxaCraAtual.getId() != taxaCra.getId()) {
						if (taxaCraAtual.getDataFim().after(taxaCra.getDataInicio())) {
							throw new InfraException("A data de início da nova vigência não pode ser anterior ao final da atual!");
						}
						if (PeriodoDataUtil.diferencaDeDiasEntreData(taxaCraAtual.getDataFim(), taxaCra.getDataInicio()) > 1) {
							throw new InfraException(
									"A diferença de dias entre o fim da vigência atual e o início da nova, não pode ser maior que um (1) dia!");
						}

					}
					taxaCra = taxaCraMediator.salvarTaxa(taxaCra, taxaCraAtual);
					setResponsePage(new IncluirTaxaCraPage("A vigência de Taxa CRA foi salva com sucesso"));

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível salvar a nova vigência de taxa CRA ! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(fieldValorTaxa());
		form.add(fieldDataInicio());
		form.add(fieldDataFim());
		form.add(labelDataInicioVigenciaAtual());
		form.add(labelDataFimVigenciaAtual());
		form.add(labelNovaDataFimVigenciaAtual());
		add(form);
	}

	private Label labelNovaDataFimVigenciaAtual() {
		return new Label("novaDataFim", DataUtil.localDateToString(new LocalDate().minusDays(1)));
	}

	private Label labelDataFimVigenciaAtual() {
		return new Label("dataFimVigenciaAtual", DataUtil.dateToString(getTaxaCraAtual().getDataFim()));
	}

	private Label labelDataInicioVigenciaAtual() {
		return new Label("dataInicioVigenciaAtual", DataUtil.dateToString(getTaxaCraAtual().getDataInicio()));
	}

	private TextField<String> fieldValorTaxa() {
		TextField<String> field = new TextField<String>("valor");
		field.setLabel(new Model<String>("Valor"));
		field.setRequired(true);
		field.setOutputMarkupId(true);
		return field;
	}

	private TextField<String> fieldDataInicio() {
		TextField<String> field = new TextField<String>("dataInicio");
		if (getModel().getObject().getDataInicio() != null) {
			field = new TextField<String>("dataInicio",
					new Model<String>(DataUtil.localDateToString(new LocalDate(getModel().getObject().getDataInicio()))));
		}
		field.setLabel(new Model<String>("Data Início"));
		field.setRequired(true);
		return field;
	}

	private TextField<String> fieldDataFim() {
		TextField<String> field = new TextField<String>("dataFim");
		if (getModel().getObject().getDataFim() != null) {
			field = new TextField<String>("dataFim",
					new Model<String>(DataUtil.localDateToString(new LocalDate(getModel().getObject().getDataFim()))));
		}
		field.setLabel(new Model<String>("Data Fim"));
		field.setRequired(true);
		return field;
	}

	private void listaTaxasAntigas() {
		add(new ListView<TaxaCra>("listTaxas", getTaxasCra()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TaxaCra> item) {
				final TaxaCra taxa = item.getModelObject();
				item.add(new Label("dataInicio", new SimpleDateFormat("dd/MM/yyyy").format(taxa.getDataInicio())));
				item.add(new Label("dataFim", new SimpleDateFormat("dd/MM/yyyy").format(taxa.getDataFim())));
				item.add(new LabelValorMonetario<BigDecimal>("valor", taxa.getValor()));
				if (DataUtil.dataEstaNoPeriodo(new LocalDate(), new LocalDate(taxa.getDataInicio()), new LocalDate(taxa.getDataFim()))
						|| taxa.getDataInicio().equals(new LocalDate().toDate())) {
					item.setOutputMarkupId(true);
					item.setMarkupId("taxaCraVigente");
				}
			}
		});
	}

	public List<TaxaCra> getTaxasCra() {
		if (taxasCra == null) {
			taxasCra = taxaCraMediator.buscarTaxasCra();
		}
		return taxasCra;
	}

	public TaxaCra getTaxaCraAtual() {
		return taxaCraAtual;
	}

	@Override
	protected IModel<TaxaCra> getModel() {
		return new CompoundPropertyModel<TaxaCra>(taxaCra);
	}
}