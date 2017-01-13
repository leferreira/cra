package br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.component.label.DataUtil;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamento;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.CodigoIrregularidade;
import br.com.ieptbto.cra.enumeration.MotivoSolicitacaoDesistenciaCancelamento;
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
public class EnviarSolicitacaoDesistenciaCancelamentoPage extends BasePage<SolicitacaoDesistenciaCancelamento> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private TituloMediator tituloMediator;
	@SpringBean
	private CancelamentoProtestoMediator cancelamentoProtestoMediator;

	private TituloRemessa titulo;
	private SolicitacaoDesistenciaCancelamento solicitacao;
	private FileUploadField fileUploadField;
	private RadioChoice<MotivoSolicitacaoDesistenciaCancelamento> radioMotivo;

	public EnviarSolicitacaoDesistenciaCancelamentoPage(TituloRemessa titulo) {
		this.solicitacao = new SolicitacaoDesistenciaCancelamento();
		this.titulo = titulo;

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		informacoesTitulo();
		criarRadioMotivo();
		criarCampoAnexoOficio();
		formularioSolicitacao();
	}

	private void formularioSolicitacao() {
		Form<SolicitacaoDesistenciaCancelamento> form = new Form<SolicitacaoDesistenciaCancelamento>("form", getModel()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				SolicitacaoDesistenciaCancelamento solicitacao = getModelObject();
				solicitacao.setDataSolicitacao(new Date());
				solicitacao.setHoraSolicitacao(new LocalTime());
				solicitacao.setUsuario(getUser());
				solicitacao.setTituloRemessa(titulo);
				solicitacao.setStatusLiberacao(false);

				try {
					MotivoSolicitacaoDesistenciaCancelamento motivo = radioMotivo.getModelObject();
					if (MotivoSolicitacaoDesistenciaCancelamento.PAGAMENTO_AO_CREDOR.equals(motivo)) {
						if (titulo.getSituacaoTitulo().equals("ABERTO")) {
							solicitacao.setTipoSolicitacao(TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO);
						} else if (titulo.getSituacaoTitulo().equals("PROTESTADO")) {
							solicitacao.setTipoSolicitacao(TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_AUTORIZACAO_CANCELAMENTO);
						}
					}

					if (MotivoSolicitacaoDesistenciaCancelamento.IRREGULARIDADE_NO_TITULO_APRESENTADO.equals(motivo)) {
						if (solicitacao.getCodigoIrregularidade().equals(CodigoIrregularidade.IRREGULARIDADE_0)
								|| solicitacao.getCodigoIrregularidade().equals(CodigoIrregularidade.IRREGULARIDADE_CONVENIO)) {
							throw new InfraException(
									"A irregularidade informada não pode ser aplicada. Por favor informe uma outra irregularidade!");
						}
						if (fileUploadField.getFileUpload() != null) {
							if (!fileUploadField.getFileUpload().getClientFileName().toUpperCase().contains(".ZIP")) {
								throw new InfraException("O anexo do documento devem estar compactados em formato ZIP.");
							}
							if (!Bytes.megabytes(5).greaterThan(fileUploadField.getFileUpload().getSize())) {
								throw new InfraException("Tamanho do arquivo anexo excedido. Limite máximo de 5 MB.");
							}
						}
						if (titulo.getSituacaoTitulo().equals("ABERTO")) {
							solicitacao.setTipoSolicitacao(TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_DESISTENCIA_PROTESTO_IRREGULARIDADE);
						} else if (titulo.getSituacaoTitulo().equals("PROTESTADO")) {
							solicitacao.setTipoSolicitacao(TipoSolicitacaoDesistenciaCancelamento.SOLICITACAO_CANCELAMENTO_PROTESTO);
						}
					}

					cancelamentoProtestoMediator.salvarSolicitacaoDesistenciaCancelamento(solicitacao, fileUploadField.getFileUpload());
					if (MotivoSolicitacaoDesistenciaCancelamento.PAGAMENTO_AO_CREDOR.equals(motivo)) {
						success("A solicitação por meio de Carta de Anuência Eletrônica foi enviada com sucesso! "
								+ "O devedor deverá comparecer em cartório para <span class=\"alert-link\">quitação das custas</span>! ");
					} else if (MotivoSolicitacaoDesistenciaCancelamento.IRREGULARIDADE_NO_TITULO_APRESENTADO.equals(motivo)) {
						success("A solicitação Desistência/Cancelamento pela Irregularidade "
								+ solicitacao.getCodigoIrregularidade().getMotivo() + ",foi enviada com sucesso!");
					}
				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar a solicitação de Desistência/Cancelamento de protesto. Favor entrar em contato com o IEPTB-TO...");
				}
			}
		};
		form.add(new EnviarDesistenciaCancelamentoInputPanel("solicitacaoInputPanel", getModel(), titulo, fileUploadField, radioMotivo));
		form.setMultiPart(true);
		add(form);
	}

	private void criarCampoAnexoOficio() {
		this.fileUploadField = new FileUploadField("anexoDesistencia", new ListModel<FileUpload>());
		this.fileUploadField.setLabel(new Model<String>("Anexo de Ofício"));
	}
	
	private void criarRadioMotivo() {
		IChoiceRenderer<MotivoSolicitacaoDesistenciaCancelamento> renderer = new ChoiceRenderer<MotivoSolicitacaoDesistenciaCancelamento>("label");
		this.radioMotivo = new RadioChoice<MotivoSolicitacaoDesistenciaCancelamento>("radioMotivoSolicitacao",
				new Model<MotivoSolicitacaoDesistenciaCancelamento>(), Arrays.asList(MotivoSolicitacaoDesistenciaCancelamento.values()), renderer);
		this.radioMotivo.setLabel(new Model<String>("Motivo da Solicitação"));
		this.radioMotivo.setRequired(true);
		this.radioMotivo.setOutputMarkupId(true);
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
		return new CompoundPropertyModel<SolicitacaoDesistenciaCancelamento>(solicitacao);
	}
}