package br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.MotivoSolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.enumeration.regra.CodigoIrregularidade;

/**
 * @author Thasso Araújo
 *
 */
public class EnviarDesistenciaCancelamentoInputPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private TituloRemessa titulo;
	private Instituicao instituicao;
	private RadioChoice<MotivoSolicitacaoDesistenciaCancelamento> radioMotivoSolicitacao;
	private DropDownChoice<CodigoIrregularidade> dropDownMotivoCancelamento;
	private FileUploadField fileUploadField;

	public EnviarDesistenciaCancelamentoInputPanel(String id, IModel<SolicitacaoDesistenciaCancelamento> model, TituloRemessa titulo,
			FileUploadField fileUpload, RadioChoice<MotivoSolicitacaoDesistenciaCancelamento> radioMotivo) {
		super(id, model);
		this.titulo = titulo;
		this.instituicao = titulo.getRemessa().getInstituicaoOrigem();
		this.fileUploadField = fileUpload;
		this.radioMotivoSolicitacao = radioMotivo;
		add(fileUploadAnexo());
		add(tipoSolicitacao());
		add(dropDownIrregularidade());
		add(numeroTituloModal());
		add(nomeDevedorModal());
		add(saldoTituloModal());
		add(buttonEnviar());
	}

	private RadioChoice<MotivoSolicitacaoDesistenciaCancelamento> tipoSolicitacao() {
		radioMotivoSolicitacao.add(new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				MotivoSolicitacaoDesistenciaCancelamento motivo = radioMotivoSolicitacao.getModelObject();
				if (motivo != null) {
					if (MotivoSolicitacaoDesistenciaCancelamento.IRREGULARIDADE_NO_TITULO_APRESENTADO.equals(motivo)) {
						dropDownMotivoCancelamento.setEnabled(true);
						dropDownMotivoCancelamento.setRequired(true);
						fileUploadField.setRequired(instituicao.isOficioDesistenciaCancelamentoObrigatorio());
					} else {
						dropDownMotivoCancelamento.setDefaultModelObject(null);
						dropDownMotivoCancelamento.setEnabled(false);
						dropDownMotivoCancelamento.setRequired(false);
						fileUploadField.setRequired(false);
					}
					target.add(fileUploadField);
					target.add(dropDownMotivoCancelamento);
				}
			}
		});
		return radioMotivoSolicitacao;
	}

	private DropDownChoice<CodigoIrregularidade> dropDownIrregularidade() {
		IChoiceRenderer<CodigoIrregularidade> renderer = new ChoiceRenderer<CodigoIrregularidade>("motivo");
		List<CodigoIrregularidade> irregularidades = Arrays.asList(CodigoIrregularidade.values());
		this.dropDownMotivoCancelamento = new DropDownChoice<CodigoIrregularidade>("codigoIrregularidade", irregularidades, renderer);
		this.dropDownMotivoCancelamento.setLabel(new Model<String>("Irregularidade"));
		this.dropDownMotivoCancelamento.setOutputMarkupId(true);
		this.dropDownMotivoCancelamento.setEnabled(false);
		return dropDownMotivoCancelamento;
	}

	private Button buttonEnviar() {
		return new Button("submit");
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

	private FileUploadField fileUploadAnexo() {
		this.fileUploadField.setOutputMarkupId(true);
		this.fileUploadField.setEnabled(true);
		return fileUploadField;
	}
}