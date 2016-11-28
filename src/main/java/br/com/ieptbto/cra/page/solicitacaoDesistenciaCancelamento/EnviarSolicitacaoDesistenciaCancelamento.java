package br.com.ieptbto.cra.page.solicitacaoDesistenciaCancelamento;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.TipoSolicitacaoDesistenciaCancelamento;
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
public class EnviarSolicitacaoDesistenciaCancelamento extends BasePage<SolicitacaoDesistenciaCancelamento> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private CancelamentoProtestoMediator cancelamentoProtestoMediator;

	private TituloRemessa titulo;
	private SolicitacaoDesistenciaCancelamento solicitacaoDesistenciaCancelamento;

	public EnviarSolicitacaoDesistenciaCancelamento(TituloRemessa titulo) {
		this.solicitacaoDesistenciaCancelamento = new SolicitacaoDesistenciaCancelamento();
		this.solicitacaoDesistenciaCancelamento.setDataSolicitacao(new Date());
		this.solicitacaoDesistenciaCancelamento.setHoraSolicitacao(new LocalTime());
		this.solicitacaoDesistenciaCancelamento.setUsuario(getUser());
		this.solicitacaoDesistenciaCancelamento.setTituloRemessa(titulo);
		this.solicitacaoDesistenciaCancelamento.setStatusLiberacao(false);
		this.titulo = titulo;

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		informacoesTitulo();
		formSolicitacaoDesistencia();
		formSolicitacaoCancelamento();
	}

	private void formSolicitacaoDesistencia() {
		Form<SolicitacaoDesistenciaCancelamento> formDesistencia =
				new Form<SolicitacaoDesistenciaCancelamento>("formDesistencia", getModel()) {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit() {
						SolicitacaoDesistenciaCancelamento solicitacaoDesistencia = getModelObject();

						try {
							if (TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE
									.equals(solicitacaoDesistencia.getTipoSolicitacao())) {
								if (solicitacaoDesistencia.getCodigoIrregularidade().equals(CodigoIrregularidade.IRREGULARIDADE_0)
										|| solicitacaoDesistencia.getCodigoIrregularidade()
												.equals(CodigoIrregularidade.IRREGULARIDADE_CONVENIO)) {
									throw new InfraException(
											"A irregularidade informada não pode ser aplicada a esta desistência de protesto. Por favor informe uma outra irregularidade!");
								}
							}

							cancelamentoProtestoMediator.salvarSolicitacaoDesistenciaCancelamento(solicitacaoDesistencia);
							if (TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE
									.equals(solicitacaoDesistencia.getTipoSolicitacao())) {
								success("A Desistência de Protesto, pela Irregularidade "
										+ solicitacaoDesistencia.getCodigoIrregularidade().getMotivo() + ",foi enviada com sucesso!");
							} else if (TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO
									.equals(solicitacaoDesistencia.getTipoSolicitacao())) {
								success("A Desistência de Protesto, por Pagamento ao Credor, foi enviada com sucesso! "
										+ "O devedor deverá comparecer em cartório para <span class=\"alert-link\">quitação das custas</span>! ");
							}
							info("O IEPTB-TO pede um prazo de até <span class=\"alert-link\">48 horas</span> para a solicitação ser processada em cartório.");

						} catch (InfraException ex) {
							logger.error(ex.getMessage(), ex);
							error(ex.getMessage());
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							error("Não foi possível enviar a solicitação de desistência de protesto. Favor entrar em contato com o IEPTB...");
						}
					}
				};
		formDesistencia.add(new SolicitarDesistenciaInputPanel("solicitarDesistenciaInputPanel", getModel(), titulo));
		add(formDesistencia);
	}

	private void formSolicitacaoCancelamento() {
		Form<SolicitacaoDesistenciaCancelamento> formCancelamento =
				new Form<SolicitacaoDesistenciaCancelamento>("formCancelamento", getModel()) {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit() {
						SolicitacaoDesistenciaCancelamento solicitacaoCancelamento = getModelObject();

						try {
							if (TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO
									.equals(solicitacaoCancelamento.getTipoSolicitacao())) {
								if (solicitacaoCancelamento.getCodigoIrregularidade().equals(CodigoIrregularidade.IRREGULARIDADE_0)
										|| solicitacaoCancelamento.getCodigoIrregularidade()
												.equals(CodigoIrregularidade.IRREGULARIDADE_CONVENIO)) {
									throw new InfraException(
											"A irregularidade informada não pode ser aplicada ao cancelamento. Por favor informe uma outra irregularidade!");
								}
							}

							cancelamentoProtestoMediator.salvarSolicitacaoDesistenciaCancelamento(solicitacaoCancelamento);
							if (TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO
									.equals(solicitacaoCancelamento.getTipoSolicitacao())) {
								success("O Cancelamento de Protesto, pela Irregularidade "
										+ solicitacaoCancelamento.getCodigoIrregularidade().getMotivo() + ",foi enviada com sucesso!");
							} else if (TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO
									.equals(solicitacaoCancelamento.getTipoSolicitacao())) {
								success("A Carta de Anuência Eletrônica foi enviada com sucesso!"
										+ "O devedor deverá comparecer em cartório para <span class=\"alert-link\">quitação das custas</span>! ");
							}
							info("O IEPTB-TO pede um prazo de até <span class=\"alert-link\">48 horas</span> para a solicitação ser processada em cartório.");

						} catch (InfraException ex) {
							logger.error(ex.getMessage(), ex);
							error(ex.getMessage());
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							error("Não foi possível enviar a solicitação de cancelamento de protesto. Favor entrar em contato com o IEPTB...");
						}
					}
				};
		formCancelamento.add(new SolicitarCancelamentoInputPanel("solicitarCancelamentoInputPanel", getModel(), titulo));
		add(formCancelamento);
	}

	private void informacoesTitulo() {
		add(status());
		add(pracaProtesto());
		add(numeroProtocoloCartorio());
		add(numeroTitulo());
		add(dataEmissaoTitulo());
		add(dataVencimentoTitulo());
		add(nomeDevedor());
		add(documentoDevedor());
		add(valorTitulo());
		add(saldoTitulo());
	}

	private Label pracaProtesto() {
		return new Label("pracaProtesto", new Model<String>(titulo.getPracaProtesto()));
	}

	private Label status() {
		return new Label("situacaoTitulo", new Model<String>(titulo.getSituacaoTitulo()));
	}

	private Label numeroTitulo() {
		return new Label("numeroTitulo", new Model<String>(titulo.getNumeroTitulo()));
	}

	private Label dataEmissaoTitulo() {
		return new Label("dataEmissaoTitulo", new Model<String>(DataUtil.localDateToString(titulo.getDataEmissaoTitulo())));
	}

	private Label dataVencimentoTitulo() {
		return new Label("dataVencimentoTitulo", new Model<String>(DataUtil.localDateToString(titulo.getDataVencimentoTitulo())));
	}

	private Label valorTitulo() {
		return new Label("valorTitulo", new Model<String>("R$ " + titulo.getValorTitulo().toString()));
	}

	private Label saldoTitulo() {
		return new Label("saldoTitulo", new Model<String>("R$ " + titulo.getSaldoTitulo().toString()));
	}

	private Label numeroProtocoloCartorio() {
		String numeroProtocolo = StringUtils.EMPTY;
		if (titulo.getConfirmacao() != null) {
			numeroProtocolo = titulo.getConfirmacao().getNumeroProtocoloCartorio();
		}
		return new Label("numeroProtocoloCartorio", new Model<String>(numeroProtocolo));
	}

	private Label nomeDevedor() {
		return new Label("nomeDevedor", new Model<String>(titulo.getNomeDevedor()));
	}

	private Label documentoDevedor() {
		return new Label("documentoDevedor", new Model<String>(titulo.getNumeroIdentificacaoDevedor()));
	}

	@Override
	protected IModel<SolicitacaoDesistenciaCancelamento> getModel() {
		return new CompoundPropertyModel<SolicitacaoDesistenciaCancelamento>(solicitacaoDesistenciaCancelamento);
	}
}