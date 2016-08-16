package br.com.ieptbto.cra.page.administracao;

import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.CraServiceConfig;
import br.com.ieptbto.cra.enumeration.CraServiceEnum;
import br.com.ieptbto.cra.enumeration.EnumerationSimNao;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CraServiceMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.SUPER })
public class WebServiceConfiguracaoPage extends BasePage<CraServiceConfig> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	CraServiceMediator craServiceMediator;

	private List<CraServiceConfig> services;
	private CheckBox envioRemessaService;
	private CheckBox downloadConfirmacaoService;
	private CheckBox downloadRetornoService;
	private CheckBox envioDesistenciaService;
	private CheckBox envioCancelamentoService;
	private CheckBox envioAutorizacaoService;
	private CheckBox downloadRemessaService;
	private CheckBox envioConfirmacaoService;
	private CheckBox envioRetornoService;
	private CheckBox downloadDesistenciaCancelamentoService;
	private CheckBox confirmarDesistenciaCancelamentoService;
	private CheckBox downloadCnpService;
	private CheckBox envioCnpService;
	private CheckBox consultaProtestoCnpService;

	public WebServiceConfiguracaoPage() {
		this.services = craServiceMediator.carregarServicos();

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		Form<CraServiceConfig> form = new Form<CraServiceConfig>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					getCraService(CraServiceEnum.ENVIO_ARQUIVO_REMESSA).setAtivo(EnumerationSimNao.getSimNao(envioRemessaService.getModelObject()));
					getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_CONFIRMACAO)
							.setAtivo(EnumerationSimNao.getSimNao(downloadConfirmacaoService.getModelObject()));
					getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_RETORNO)
							.setAtivo(EnumerationSimNao.getSimNao(downloadRetornoService.getModelObject()));
					getCraService(CraServiceEnum.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO)
							.setAtivo(EnumerationSimNao.getSimNao(envioDesistenciaService.getModelObject()));
					getCraService(CraServiceEnum.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO)
							.setAtivo(EnumerationSimNao.getSimNao(envioCancelamentoService.getModelObject()));
					getCraService(CraServiceEnum.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO)
							.setAtivo(EnumerationSimNao.getSimNao(envioAutorizacaoService.getModelObject()));
					getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_REMESSA)
							.setAtivo(EnumerationSimNao.getSimNao(downloadRemessaService.getModelObject()));
					getCraService(CraServiceEnum.ENVIO_ARQUIVO_CONFIRMACAO)
							.setAtivo(EnumerationSimNao.getSimNao(envioConfirmacaoService.getModelObject()));
					getCraService(CraServiceEnum.ENVIO_ARQUIVO_RETORNO).setAtivo(EnumerationSimNao.getSimNao(envioRetornoService.getModelObject()));
					getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_DESISTENCIA_CANCELAMENTO)
							.setAtivo(EnumerationSimNao.getSimNao(downloadDesistenciaCancelamentoService.getModelObject()));
					getCraService(CraServiceEnum.CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO)
							.setAtivo(EnumerationSimNao.getSimNao(confirmarDesistenciaCancelamentoService.getModelObject()));
					getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO)
							.setAtivo(EnumerationSimNao.getSimNao(downloadCnpService.getModelObject()));
					getCraService(CraServiceEnum.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO)
							.setAtivo(EnumerationSimNao.getSimNao(envioCnpService.getModelObject()));
					getCraService(CraServiceEnum.CONSULTA_PROTESTO)
							.setAtivo(EnumerationSimNao.getSimNao(consultaProtestoCnpService.getModelObject()));

					craServiceMediator.salvarServicos(services);
					success("Os serviços foram atualizados com sucesso !");
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
				}
			}
		};
		form.add(checkEnvioDeRemessas());
		form.add(checkDownloadConfirmacao());
		form.add(checkDownloadRetorno());
		form.add(checkEnvioDeDesistencia());
		form.add(checkEnvioDeCancelamento());
		form.add(checkEnvioDeAutorizacao());
		form.add(checkDownloadRemessas());
		form.add(checkEnvioConfirmacao());
		form.add(checkEnvioRetorno());
		form.add(checkDownloadDesistenciaCancelamentos());
		form.add(checkConfirmarDesistenciaCancelamentos());
		form.add(checkDownloadCNP());
		form.add(checkEnvioCNPCartorio());
		form.add(checkConsultaCnp());
		add(form);
	}

	private CheckBox checkEnvioDeRemessas() {
		CraServiceConfig craService = getCraService(CraServiceEnum.ENVIO_ARQUIVO_REMESSA);
		return envioRemessaService = new CheckBox("checkEnvioRemessa", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkDownloadConfirmacao() {
		CraServiceConfig craService = getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_CONFIRMACAO);
		return downloadConfirmacaoService = new CheckBox("checkDownloadConfirmacao", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkDownloadRetorno() {
		CraServiceConfig craService = getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_RETORNO);
		return downloadRetornoService = new CheckBox("checkDownloadRetorno", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkEnvioDeDesistencia() {
		CraServiceConfig craService = getCraService(CraServiceEnum.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO);
		return envioDesistenciaService = new CheckBox("checkEnvioDeDesistencia", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkEnvioDeCancelamento() {
		CraServiceConfig craService = getCraService(CraServiceEnum.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO);
		return envioCancelamentoService = new CheckBox("checkEnvioDeCancelamento", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkEnvioDeAutorizacao() {
		CraServiceConfig craService = getCraService(CraServiceEnum.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO);
		return envioAutorizacaoService = new CheckBox("checkEnvioDeAutorizacao", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkDownloadRemessas() {
		CraServiceConfig craService = getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_REMESSA);
		return downloadRemessaService = new CheckBox("checkDownloadRemessas", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkEnvioConfirmacao() {
		CraServiceConfig craService = getCraService(CraServiceEnum.ENVIO_ARQUIVO_CONFIRMACAO);
		return envioConfirmacaoService = new CheckBox("checkEnvioConfirmacao", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkEnvioRetorno() {
		CraServiceConfig craService = getCraService(CraServiceEnum.ENVIO_ARQUIVO_RETORNO);
		return envioRetornoService = new CheckBox("checkEnvioRetorno", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkDownloadDesistenciaCancelamentos() {
		CraServiceConfig craService = getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_DESISTENCIA_CANCELAMENTO);
		return downloadDesistenciaCancelamentoService =
				new CheckBox("checkDownloadDesistenciaCancelamentos", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkConfirmarDesistenciaCancelamentos() {
		CraServiceConfig craService = getCraService(CraServiceEnum.CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO);
		return confirmarDesistenciaCancelamentoService =
				new CheckBox("checkConfirmarDesistenciaCancelamentos", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkDownloadCNP() {
		CraServiceConfig craService = getCraService(CraServiceEnum.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO);
		return downloadCnpService = new CheckBox("checkDownloadCnp", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkEnvioCNPCartorio() {
		CraServiceConfig craService = getCraService(CraServiceEnum.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO);
		return envioCnpService = new CheckBox("checkEnvioCnp", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CheckBox checkConsultaCnp() {
		CraServiceConfig craService = getCraService(CraServiceEnum.CONSULTA_PROTESTO);
		return consultaProtestoCnpService = new CheckBox("checkConsultaCnp", new Model<Boolean>(craService.getAtivo().getStatus()));
	}

	private CraServiceConfig getCraService(CraServiceEnum service) {
		for (CraServiceConfig craServiceConfig : services) {
			if (craServiceConfig.getCraService() == service) {
				return craServiceConfig;
			}
		}
		return null;
	}

	@Override
	protected IModel<CraServiceConfig> getModel() {
		return null;
	}
}