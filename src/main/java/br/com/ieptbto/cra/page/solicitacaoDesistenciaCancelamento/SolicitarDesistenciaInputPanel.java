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
public class SolicitarDesistenciaInputPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	private TituloRemessa titulo;
	private DropDownChoice<CodigoIrregularidade> dropDownMotivoCancelamento;
	private Boolean solicitacaoDesistenciaAtiva;

	public SolicitarDesistenciaInputPanel(String id, IModel<SolicitacaoDesistenciaCancelamento> model, TituloRemessa titulo) {
		super(id, model);
		this.titulo = titulo;

		adicionarCampos();
	}

	private void adicionarCampos() {
		add(tipoSolicitacaoDesistencia());
		add(dropDownDesistenciaIrregularidade());
		add(buttonEnviar());
		add(numeroTituloModal());
		add(nomeDevedorModal());
		add(saldoTituloModal());
	}

	private RadioGroup<TipoSolicitacaoDesistenciaCancelamento> tipoSolicitacaoDesistencia() {
		final RadioGroup<TipoSolicitacaoDesistenciaCancelamento> radioTipo =
				new RadioGroup<TipoSolicitacaoDesistenciaCancelamento>("tipoSolicitacao");
		radioTipo.setLabel(new Model<String>("Tipo de Desistência"));
		radioTipo.setRequired(true);
		radioTipo.setOutputMarkupId(true);

		radioTipo.add(
				new Radio<TipoSolicitacaoDesistenciaCancelamento>("dpIrregularidade", new Model<TipoSolicitacaoDesistenciaCancelamento>(
						TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE)));
		radioTipo.add(new Radio<TipoSolicitacaoDesistenciaCancelamento>("dp", new Model<TipoSolicitacaoDesistenciaCancelamento>(
				TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO)));
		radioTipo.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				TipoSolicitacaoDesistenciaCancelamento tipoSolicitacao = radioTipo.getModelObject();
				if (tipoSolicitacao != null) {
					if (TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE.equals(tipoSolicitacao)) {
						dropDownMotivoCancelamento.setEnabled(true);
						dropDownMotivoCancelamento.setRequired(true);
					} else {
						dropDownMotivoCancelamento.setDefaultModelObject(null);
						dropDownMotivoCancelamento.setEnabled(false);
						dropDownMotivoCancelamento.setRequired(false);
					}
					target.add(dropDownMotivoCancelamento);
				} else {
					dropDownMotivoCancelamento.setEnabled(false);
					dropDownMotivoCancelamento.setRequired(false);
				}
			}
		});
		radioTipo.setEnabled(getSolicitacaoDesistenciaAtiva());
		return radioTipo;
	}

	private DropDownChoice<CodigoIrregularidade> dropDownDesistenciaIrregularidade() {
		IChoiceRenderer<CodigoIrregularidade> renderer = new ChoiceRenderer<CodigoIrregularidade>("motivo");
		List<CodigoIrregularidade> irregularidades = Arrays.asList(CodigoIrregularidade.values());
		this.dropDownMotivoCancelamento = new DropDownChoice<CodigoIrregularidade>("codigoIrregularidade", irregularidades, renderer);
		this.dropDownMotivoCancelamento.setLabel(new Model<String>("Motivo da Desistência"));
		this.dropDownMotivoCancelamento.setOutputMarkupId(true);
		this.dropDownMotivoCancelamento.setEnabled(false);
		return dropDownMotivoCancelamento;
	}

	private Button buttonEnviar() {
		Button buttonEnviar = new Button("submitSolicitarDesistencia");
		buttonEnviar.setEnabled(getSolicitacaoDesistenciaAtiva());
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

	public Boolean getSolicitacaoDesistenciaAtiva() {
		if (solicitacaoDesistenciaAtiva == null) {
			if (titulo.getSituacaoTitulo().equals("ABERTO")) {
				this.solicitacaoDesistenciaAtiva = true;
			} else {
				this.solicitacaoDesistenciaAtiva = false;
			}
		}
		return solicitacaoDesistenciaAtiva;
	}
}