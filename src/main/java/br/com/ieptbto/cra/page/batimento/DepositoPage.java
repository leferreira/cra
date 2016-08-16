package br.com.ieptbto.cra.page.batimento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.enumeration.SituacaoDeposito;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class DepositoPage extends BasePage<Deposito> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	BatimentoMediator batimentoMediator;

	private Deposito deposito;
	private List<Deposito> depositos;

	public DepositoPage(Deposito deposito, List<Deposito> depositos) {
		this.deposito = deposito;
		this.depositos = depositos;
		adicionarComponentes();
	}

	public DepositoPage(String message, Deposito deposito, List<Deposito> depositos) {
		this.deposito = deposito;
		this.depositos = depositos;

		success(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarFormulario();
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
					setResponsePage(new DepositoPage("Informações do depósito foram atualizadas com sucesso!", getDeposito(), getDepositos()));

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
		form.add(descricao());
		form.add(botaoVoltar());
		add(form);
	}

	private Link<Deposito> botaoVoltar() {
		return new Link<Deposito>("botaoVoltar") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ListaDepositoPage(getDepositos()));
			}
		};
	}

	private RadioChoice<SituacaoDeposito> situacaoDeposito() {
		IChoiceRenderer<SituacaoDeposito> renderer = new ChoiceRenderer<SituacaoDeposito>("label");
		List<SituacaoDeposito> list = new ArrayList<SituacaoDeposito>(Arrays.asList(SituacaoDeposito.values()));
		RadioChoice<SituacaoDeposito> comboSituacao = new RadioChoice<SituacaoDeposito>("situacaoDeposito", list, renderer);
		return comboSituacao;
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
		TextArea<String> text = new TextArea<String>("descricao");
		return text;
	}

	public Deposito getDeposito() {
		return deposito;
	}

	public List<Deposito> getDepositos() {
		return depositos;
	}

	@Override
	protected IModel<Deposito> getModel() {
		return new CompoundPropertyModel<Deposito>(deposito);
	}
}
