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
import br.com.ieptbto.cra.enumeration.CraServices;
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
	private CheckBox envioCnp5anosService;
	private CheckBox consultaProtestoCnpService;

	public WebServiceConfiguracaoPage() {
		this.services = craServiceMediator.carregarServicos();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(formServicos());
	}

	private Form<CraServiceConfig> formServicos() {
		Form<CraServiceConfig> form = new Form<CraServiceConfig>("form") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					getCraService(CraServices.ENVIO_ARQUIVO_REMESSA).setStatus(envioRemessaService.getModelObject());
					getCraService(CraServices.DOWNLOAD_ARQUIVO_CONFIRMACAO).setStatus(downloadConfirmacaoService.getModelObject());
					getCraService(CraServices.DOWNLOAD_ARQUIVO_RETORNO).setStatus(downloadRetornoService.getModelObject());
					getCraService(CraServices.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO).setStatus(envioDesistenciaService.getModelObject());
					getCraService(CraServices.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO).setStatus(envioCancelamentoService.getModelObject());
					getCraService(CraServices.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO).setStatus(envioAutorizacaoService.getModelObject());
					getCraService(CraServices.DOWNLOAD_ARQUIVO_REMESSA).setStatus(downloadRemessaService.getModelObject());
					getCraService(CraServices.ENVIO_ARQUIVO_CONFIRMACAO).setStatus(envioConfirmacaoService.getModelObject());
					getCraService(CraServices.ENVIO_ARQUIVO_RETORNO).setStatus(envioRetornoService.getModelObject());
					getCraService(CraServices.DOWNLOAD_ARQUIVO_DESISTENCIA_CANCELAMENTO).setStatus(downloadDesistenciaCancelamentoService.getModelObject());
					getCraService(CraServices.CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO).setStatus(confirmarDesistenciaCancelamentoService.getModelObject());
					getCraService(CraServices.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO).setStatus(downloadCnpService.getModelObject());
					getCraService(CraServices.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO).setStatus(envioCnpService.getModelObject());
					getCraService(CraServices.CONSULTA_PROTESTO).setStatus(consultaProtestoCnpService.getModelObject());
					getCraService(CraServices.ENVIO_CNP_5_ANOS).setStatus(envioCnp5anosService.getModelObject());

					craServiceMediator.salvarServicos(services);
					success("A disponibilidade dos serviços foram atualizados com sucesso!");
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível atualizar a disponibilidade dos serviços! Favor entrar em contato com a CRA...");
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
		form.add(checkEnvioCNP5Anos());
		form.add(checkConsultaCnp());
		return form;
	}

	private CheckBox checkEnvioDeRemessas() {
		CraServiceConfig craService = getCraService(CraServices.ENVIO_ARQUIVO_REMESSA);
		return envioRemessaService = new CheckBox("checkEnvioRemessa", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkDownloadConfirmacao() {
		CraServiceConfig craService = getCraService(CraServices.DOWNLOAD_ARQUIVO_CONFIRMACAO);
		return downloadConfirmacaoService = new CheckBox("checkDownloadConfirmacao", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkDownloadRetorno() {
		CraServiceConfig craService = getCraService(CraServices.DOWNLOAD_ARQUIVO_RETORNO);
		return downloadRetornoService = new CheckBox("checkDownloadRetorno", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkEnvioDeDesistencia() {
		CraServiceConfig craService = getCraService(CraServices.ENVIO_ARQUIVO_DESISTENCIA_PROTESTO);
		return envioDesistenciaService = new CheckBox("checkEnvioDeDesistencia", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkEnvioDeCancelamento() {
		CraServiceConfig craService = getCraService(CraServices.ENVIO_ARQUIVO_CANCELAMENTO_PROTESTO);
		return envioCancelamentoService = new CheckBox("checkEnvioDeCancelamento", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkEnvioDeAutorizacao() {
		CraServiceConfig craService = getCraService(CraServices.ENVIO_ARQUIVO_AUTORIZACAO_CANCELAMENTO);
		return envioAutorizacaoService = new CheckBox("checkEnvioDeAutorizacao", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkDownloadRemessas() {
		CraServiceConfig craService = getCraService(CraServices.DOWNLOAD_ARQUIVO_REMESSA);
		return downloadRemessaService = new CheckBox("checkDownloadRemessas", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkEnvioConfirmacao() {
		CraServiceConfig craService = getCraService(CraServices.ENVIO_ARQUIVO_CONFIRMACAO);
		return envioConfirmacaoService = new CheckBox("checkEnvioConfirmacao", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkEnvioRetorno() {
		CraServiceConfig craService = getCraService(CraServices.ENVIO_ARQUIVO_RETORNO);
		return envioRetornoService = new CheckBox("checkEnvioRetorno", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkDownloadDesistenciaCancelamentos() {
		CraServiceConfig craService = getCraService(CraServices.DOWNLOAD_ARQUIVO_DESISTENCIA_CANCELAMENTO);
		return downloadDesistenciaCancelamentoService =
				new CheckBox("checkDownloadDesistenciaCancelamentos", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkConfirmarDesistenciaCancelamentos() {
		CraServiceConfig craService = getCraService(CraServices.CONFIRMAR_RECEBIMENTO_DESISTENCIA_CANCELAMENTO);
		return confirmarDesistenciaCancelamentoService =
				new CheckBox("checkConfirmarDesistenciaCancelamentos", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkDownloadCNP() {
		CraServiceConfig craService = getCraService(CraServices.DOWNLOAD_ARQUIVO_CENTRAL_NACIONAL_PROTESTO);
		return downloadCnpService = new CheckBox("checkDownloadCnp", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkEnvioCNPCartorio() {
		CraServiceConfig craService = getCraService(CraServices.ENVIO_ARQUIVO_CENTRAL_NACIONAL_PROTESTO);
		return envioCnpService = new CheckBox("checkEnvioCnp", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkEnvioCNP5Anos() {
		CraServiceConfig craService = getCraService(CraServices.ENVIO_CNP_5_ANOS);
		return envioCnp5anosService = new CheckBox("checkEnvioCnp5anos", new Model<Boolean>(craService.getStatus()));
	}

	private CheckBox checkConsultaCnp() {
		CraServiceConfig craService = getCraService(CraServices.CONSULTA_PROTESTO);
		return consultaProtestoCnpService = new CheckBox("checkConsultaCnp", new Model<Boolean>(craService.getStatus()));
	}

	private CraServiceConfig getCraService(CraServices service) {
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