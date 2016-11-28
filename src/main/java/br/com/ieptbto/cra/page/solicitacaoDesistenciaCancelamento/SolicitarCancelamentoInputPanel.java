package br.com.ieptbto.cra.page.solicitacaoDesistenciaCancelamento;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.TipoSolicitacaoDesistenciaCancelamento;

/**
 * @author Thasso Araújo
 *
 */
public class SolicitarCancelamentoInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	private TituloRemessa titulo;
	private DropDownChoice<CodigoIrregularidade> dropDownMotivoCancelamento;
	private Boolean solicitacaoCancelamentoAtiva;

	public SolicitarCancelamentoInputPanel(String id, IModel<SolicitacaoDesistenciaCancelamento> model, TituloRemessa titulo) {
		super(id, model);
		this.titulo = titulo;

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(tipoSolicitacaoCancelamento());
		add(dropDownCancelamentoIrregularidade());
		add(buttonEnviar());
		add(numeroTituloModal());
		add(nomeDevedorModal());
		add(saldoTituloModal());
	}

	private RadioGroup<TipoSolicitacaoDesistenciaCancelamento> tipoSolicitacaoCancelamento() {
		final RadioGroup<TipoSolicitacaoDesistenciaCancelamento> radioTipo =
				new RadioGroup<TipoSolicitacaoDesistenciaCancelamento>("tipoSolicitacao");
		radioTipo.setLabel(new Model<String>("Tipo de Cancelamento"));
		radioTipo.setRequired(true);
		radioTipo.setOutputMarkupId(true);

		radioTipo.add(new Radio<TipoSolicitacaoDesistenciaCancelamento>("cp", new Model<TipoSolicitacaoDesistenciaCancelamento>(
				TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO)));
		radioTipo.add(new Radio<TipoSolicitacaoDesistenciaCancelamento>("ac", new Model<TipoSolicitacaoDesistenciaCancelamento>(
				TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO)));
		radioTipo.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				TipoSolicitacaoDesistenciaCancelamento tipoSolicitacao = radioTipo.getModelObject();
				if (tipoSolicitacao != null) {
					if (TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO.equals(tipoSolicitacao)) {
						dropDownMotivoCancelamento.setEnabled(true);
						dropDownMotivoCancelamento.setRequired(true);
					} else {
						dropDownMotivoCancelamento.setDefaultModelObject(null);
						dropDownMotivoCancelamento.setEnabled(false);
						dropDownMotivoCancelamento.setRequired(false);
					}
					target.add(dropDownMotivoCancelamento);
				} else {
					dropDownMotivoCancelamento.setDefaultModelObject(null);
					dropDownMotivoCancelamento.setEnabled(false);
					dropDownMotivoCancelamento.setRequired(false);
				}
			}
		});
		radioTipo.setEnabled(getSolicitacaoCancelamentoAtiva());
		return radioTipo;
	}

	private DropDownChoice<CodigoIrregularidade> dropDownCancelamentoIrregularidade() {
		IChoiceRenderer<CodigoIrregularidade> renderer = new ChoiceRenderer<CodigoIrregularidade>("motivo");
		List<CodigoIrregularidade> irregularidades = Arrays.asList(CodigoIrregularidade.values());
		this.dropDownMotivoCancelamento = new DropDownChoice<CodigoIrregularidade>("codigoIrregularidade", irregularidades, renderer);
		this.dropDownMotivoCancelamento.setLabel(new Model<String>("Motivo do Cancelamento"));
		this.dropDownMotivoCancelamento.setOutputMarkupId(true);
		this.dropDownMotivoCancelamento.setEnabled(false);
		return dropDownMotivoCancelamento;
	}

	private Button buttonEnviar() {
		Button buttonEnviar = new Button("submitSolicitarCancelamento");
		buttonEnviar.setEnabled(getSolicitacaoCancelamentoAtiva());
		return buttonEnviar;
	}

	private Label numeroTituloModal() {
		return new Label("numeroTituloModal", new Model<String>(titulo.getNumeroTitulo()));
	}

	private Label saldoTituloModal() {
		return new Label("saldoTituloModal", new Model<String>("R$ " + titulo.getSaldoTitulo().toString()));
	}

	private Label nomeDevedorModal() {
		return new Label("nomeDevedorModal", new Model<String>(titulo.getNomeDevedor()));
	}

	public Boolean getSolicitacaoCancelamentoAtiva() {
		if (solicitacaoCancelamentoAtiva == null) {
			if (titulo.getSituacaoTitulo().equals("PROTESTADO")) {
				this.solicitacaoCancelamentoAtiva = true;
			} else {
				this.solicitacaoCancelamentoAtiva = false;
			}
		}
		return solicitacaoCancelamentoAtiva;
	}
}