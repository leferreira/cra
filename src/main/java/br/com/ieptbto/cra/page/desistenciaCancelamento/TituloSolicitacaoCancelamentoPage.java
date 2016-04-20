package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.StatusSolicitacaoCancelamento;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class TituloSolicitacaoCancelamentoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoProtestoMediator;

	private TituloRemessa tituloRemessa;

	private DropDownChoice<CodigoIrregularidade> dropDownMotivoCancelamento;

	public TituloSolicitacaoCancelamentoPage(TituloRemessa titulo) {
		this.tituloRemessa = titulo;
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		informacoesTitulo();
		formEnviarSolicitacao();

	}

	private void formEnviarSolicitacao() {
		Form<TituloRemessa> form = new Form<TituloRemessa>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				TituloRemessa titulo = getModelObject();

				try {
					if (titulo.getStatusSolicitacaoCancelamento() != StatusSolicitacaoCancelamento.NAO_SOLICITADO) {
						throw new InfraException("Solicitação de cancelamento para este título já foi enviada anteriormente !");
					}
					if (dropDownMotivoCancelamento.getModelObject() == null) {
						titulo.setStatusSolicitacaoCancelamento(StatusSolicitacaoCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO);
					} else {
						CodigoIrregularidade codigoIrregularidade = dropDownMotivoCancelamento.getModelObject();
						if (codigoIrregularidade == CodigoIrregularidade.IRREGULARIDADE_0) {
							titulo.setStatusSolicitacaoCancelamento(StatusSolicitacaoCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO);
						} else {
							if (titulo.getConfirmacao().getDataProtocolo().isAfter(DataUtil.stringToLocalDate("17/03/2016"))) {
								throw new InfraException("Segundo ofício do Tribunal de Justiça, não é permitido "
										+ "o cancelamento por irregularidade para protocolos após 16/03/2016!");
							}
							titulo.setCodigoIrregularidadeCancelamento(codigoIrregularidade);
							titulo.setStatusSolicitacaoCancelamento(StatusSolicitacaoCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO);
						}
					}
					cancelamentoProtestoMediator.salvarSolicitacaoCancelamento(titulo);
					success("Solicitação de cancelamento efetuada com sucesso!");
					info("O devedor deverá comparacer ao cartório para quitação das custas do cancelamento e do protesto! ");

				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					getFeedbackPanel().error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar a solicitação de cancelamento ! \n Entre em contato com o IEPTB ! ");
				}
			}
		};
		form.add(dropDownMotivoCancelamento());
		form.add(numeroTituloModal());
		form.add(nomeDevedorModal());
		form.add(saldoTituloModal());
		add(form);
	}

	private void informacoesTitulo() {
		add(pracaProtesto());
		add(status());
		add(nomeSacadorVendedor());
		add(documentoSacador());
		add(nomeDevedor());
		add(documentoDevedor());
		add(numeroTitulo());
		add(especieTitulo());
		add(dataEmissaoTitulo());
		add(dataVencimentoTitulo());
		add(valorTitulo());
		add(saldoTitulo());
		add(numeroProtocoloCartorio());

	}

	private DropDownChoice<CodigoIrregularidade> dropDownMotivoCancelamento() {
		IChoiceRenderer<CodigoIrregularidade> renderer = new ChoiceRenderer<CodigoIrregularidade>("motivo");
		dropDownMotivoCancelamento = new DropDownChoice<CodigoIrregularidade>("tipoMotivo", new Model<CodigoIrregularidade>(),
				Arrays.asList(CodigoIrregularidade.values()), renderer);
		dropDownMotivoCancelamento.setLabel(new Model<String>("Motivo do Cancelameto"));
		dropDownMotivoCancelamento.setOutputMarkupId(true);
		return dropDownMotivoCancelamento;
	}

	private Label pracaProtesto() {
		return new Label("pracaProtesto", new Model<String>(getTituloRemessa().getPracaProtesto()));
	}

	private Label status() {
		return new Label("situacaoTitulo", new Model<String>(getTituloRemessa().getSituacaoTitulo()));
	}

	private Label numeroTitulo() {
		return new Label("numeroTitulo", new Model<String>(getTituloRemessa().getNumeroTitulo()));
	}

	private Label numeroTituloModal() {
		return new Label("numeroTituloModal", new Model<String>(getTituloRemessa().getNumeroTitulo()));
	}

	public Label saldoTituloModal() {
		Label textField = new Label("saldoTituloModal", new Model<String>("R$ " + getTituloRemessa().getSaldoTitulo().toString()));
		return textField;
	}

	private Label nomeDevedorModal() {
		Label textField = new Label("nomeDevedorModal", new Model<String>(getTituloRemessa().getNomeDevedor()));
		return textField;
	}

	private Label especieTitulo() {
		return new Label("especieTitulo", new Model<String>(getTituloRemessa().getEspecieTitulo()));
	}

	private Label dataEmissaoTitulo() {
		return new Label("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataEmissaoTitulo())));
	}

	private Label dataVencimentoTitulo() {
		return new Label("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(getTituloRemessa().getDataVencimentoTitulo())));
	}

	public Label valorTitulo() {
		Label textField = new Label("valorTitulo", new Model<String>("R$ " + getTituloRemessa().getValorTitulo().toString()));
		return textField;
	}

	public Label saldoTitulo() {
		Label textField = new Label("saldoTitulo", new Model<String>("R$ " + getTituloRemessa().getSaldoTitulo().toString()));
		return textField;
	}

	private Label numeroProtocoloCartorio() {
		String numeroProtocolo = StringUtils.EMPTY;
		if (getTituloRemessa().getConfirmacao() != null) {
			numeroProtocolo = getTituloRemessa().getConfirmacao().getNumeroProtocoloCartorio();
		}
		return new Label("numeroProtocoloCartorio", new Model<String>(numeroProtocolo));
	}

	private Label nomeSacadorVendedor() {
		Label textField = new Label("nomeSacadorVendedor", new Model<String>(getTituloRemessa().getNomeSacadorVendedor()));
		return textField;
	}

	private Label documentoSacador() {
		Label textField = new Label("documentoSacador", new Model<String>(getTituloRemessa().getDocumentoSacador()));
		return textField;
	}

	private Label nomeDevedor() {
		Label textField = new Label("nomeDevedor", new Model<String>(getTituloRemessa().getNomeDevedor()));
		return textField;
	}

	private Label documentoDevedor() {
		Label textField = new Label("documentoDevedor", new Model<String>(getTituloRemessa().getNumeroIdentificacaoDevedor()));
		return textField;
	}

	private TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}
}