/**
 * 
 */
package br.com.ieptbto.cra.app;

import java.util.Locale;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.joda.time.DateTimeZone;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioAnonimo;
import br.com.ieptbto.cra.menu.CraMenu;
import br.com.ieptbto.cra.page.administracao.ListaRemoverArquivoPage;
import br.com.ieptbto.cra.page.administracao.RemoverArquivoPage;
import br.com.ieptbto.cra.page.administracao.SobreCraPage;
import br.com.ieptbto.cra.page.arquivo.BuscarArquivoCraInstituicaoPage;
import br.com.ieptbto.cra.page.arquivo.BuscarArquivoPage;
import br.com.ieptbto.cra.page.arquivo.EnviarArquivoPage;
import br.com.ieptbto.cra.page.arquivo.ListaArquivoInstituicaoPage;
import br.com.ieptbto.cra.page.arquivo.ListaArquivoPage;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoInstituicaoPage;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.HomePage;
import br.com.ieptbto.cra.page.base.NotFoundPage;
import br.com.ieptbto.cra.page.batimento.BatimentoPage;
import br.com.ieptbto.cra.page.batimento.BuscarDepositoPage;
import br.com.ieptbto.cra.page.batimento.ImportarExtratoPage;
import br.com.ieptbto.cra.page.batimento.IncluirDepositoPage;
import br.com.ieptbto.cra.page.batimento.LiberarRetornoPage;
import br.com.ieptbto.cra.page.batimento.ListaDepositoPage;
import br.com.ieptbto.cra.page.batimento.RetornosLiberadosPage;
import br.com.ieptbto.cra.page.cartorio.IncluirCartorioPage;
import br.com.ieptbto.cra.page.cartorio.ListaCartorioPage;
import br.com.ieptbto.cra.page.centralDeAcoes.CentralDeAcoesPage;
import br.com.ieptbto.cra.page.cnp.CentralNacionalProtestoPage;
import br.com.ieptbto.cra.page.convenio.GerarRemessaConvenioPage;
import br.com.ieptbto.cra.page.cra.GerarCancelamentosPage;
import br.com.ieptbto.cra.page.cra.GerarConfirmacaoPage;
import br.com.ieptbto.cra.page.cra.GerarRetornoPage;
import br.com.ieptbto.cra.page.cra.RelatorioRetornoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.BuscarDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.BuscarTituloSolicitacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.ListaDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.ListaTituloSolicitacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TituloSolicitacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.page.filiado.IncluirFiliadoPage;
import br.com.ieptbto.cra.page.filiado.ListaFiliadoPage;
import br.com.ieptbto.cra.page.instituicao.IncluirInstituicaoPage;
import br.com.ieptbto.cra.page.instituicao.ListaInstituicaoPage;
import br.com.ieptbto.cra.page.instrumentoProtesto.BuscarInstrumentoProtestoPage;
import br.com.ieptbto.cra.page.instrumentoProtesto.GerarSlipPage;
import br.com.ieptbto.cra.page.instrumentoProtesto.ImportarArquivoDeParaPage;
import br.com.ieptbto.cra.page.instrumentoProtesto.InstrumentoProtestoPage;
import br.com.ieptbto.cra.page.layoutPersonalizado.EnviarArquivoEmpresaPage;
import br.com.ieptbto.cra.page.layoutPersonalizado.IncluirLayoutEmpresaPage;
import br.com.ieptbto.cra.page.layoutPersonalizado.ListaLayoutEmpresaPage;
import br.com.ieptbto.cra.page.login.LoginPage;
import br.com.ieptbto.cra.page.municipio.IncluirMunicipioPage;
import br.com.ieptbto.cra.page.municipio.ListaMunicipioPage;
import br.com.ieptbto.cra.page.relatorio.RelatorioArquivosPage;
import br.com.ieptbto.cra.page.relatorio.RelatorioCustasCraPage;
import br.com.ieptbto.cra.page.relatorio.RelatorioInstituicoesCartoriosPage;
import br.com.ieptbto.cra.page.relatorio.titulo.RelatorioTitulosPage;
import br.com.ieptbto.cra.page.tipoArquivo.IncluirTipoArquivoPage;
import br.com.ieptbto.cra.page.tipoArquivo.ListaTipoArquivoPage;
import br.com.ieptbto.cra.page.tipoInstituicao.IncluirTipoInstituicaoPage;
import br.com.ieptbto.cra.page.tipoInstituicao.ListaTipoInstituicaoPage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
import br.com.ieptbto.cra.page.titulo.ListaTitulosPage;
import br.com.ieptbto.cra.page.titulo.MonitorarTitulosPage;
import br.com.ieptbto.cra.page.usuario.IncluirUsuarioPage;
import br.com.ieptbto.cra.page.usuario.ListaUsuarioPage;
import br.com.ieptbto.cra.page.usuario.PerfilUsuarioPage;
import br.com.ieptbto.cra.security.ISecureApplication;
import br.com.ieptbto.cra.security.UserRoleAuthorizationStrategy;
import br.com.ieptbto.cra.security.UserRolesAuthorizer;
import br.com.ieptbto.cra.security.UserSession;
import br.com.ieptbto.cra.util.CargaInicialPage;
import br.com.ieptbto.cra.util.DataUtil;
import br.com.ieptbto.cra.webpage.AbstractWebPage;

/**
 * @author Lefer
 *
 */
public class CraApplication extends WebApplication implements ISecureApplication, IWebApplication {

	public CraApplication() {
	}

	@Override
	public void init() {
		super.init();
		initSpring();
		initConfig();
		initAtributoDeDatas();
		montaPaginas();

	}

	// start page
	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	// page for auth
	@Override
	public Class<? extends Page> getLoginPage() {
		return LoginPage.class;
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new UserSession<Usuario>(request, new UsuarioAnonimo());
	}

	protected void initSpring() {
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
	}

	private void initConfig() {
		getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);

		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

		getRequestCycleSettings().setTimeout(Duration.minutes(10));

		// don't throw exceptions for missing translations
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		getApplicationSettings().setPageExpiredErrorPage(NotFoundPage.class);
		getApplicationSettings().setAccessDeniedPage(NotFoundPage.class);
		getApplicationSettings().setInternalErrorPage(NotFoundPage.class);

		// customized auth strategy
		getSecuritySettings().setAuthorizationStrategy(new UserRoleAuthorizationStrategy(new UserRolesAuthorizer()));

		// make markup friendly as in deployment-mode
		getMarkupSettings().setStripWicketTags(true);

		if (isDevelopmentMode()) {
			// enable ajax debug etc.
			getDebugSettings().setDevelopmentUtilitiesEnabled(true);
			getDebugSettings().setAjaxDebugModeEnabled(true);
			System.out.println(RuntimeConfigurationType.DEVELOPMENT);
		}
	}

	private void montaPaginas() {
		mountPage("LoginPage", LoginPage.class);
		mountPage("HomePage", HomePage.class);
		mountPage("SobreCra", SobreCraPage.class);
		mountPage("CentralDeAcoes", CentralDeAcoesPage.class);
		mountPage("CentralNacionalProtesto", CentralNacionalProtestoPage.class);
		mountPage("CargaInicial", CargaInicialPage.class);

		/** Administracao */
		mountPage("Batimento", BatimentoPage.class);
		mountPage("ImportarExtrato", ImportarExtratoPage.class);
		mountPage("BuscarDepositos", BuscarDepositoPage.class);
		mountPage("LiberarRetornos", LiberarRetornoPage.class);
		mountPage("RetornosLiberados", RetornosLiberadosPage.class);
		mountPage("ListaDepositos", ListaDepositoPage.class);
		mountPage("Deposito", IncluirDepositoPage.class);

		mountPage("GerarRemessasConvenio", GerarRemessaConvenioPage.class);
		mountPage("GerarConfirmacao", GerarConfirmacaoPage.class);
		mountPage("GerarRetorno", GerarRetornoPage.class);
		mountPage("GerarCancelamentos", GerarCancelamentosPage.class);

		mountPage("Instituicoes", ListaInstituicaoPage.class);
		mountPage("IncluirInstituicao", IncluirInstituicaoPage.class);
		mountPage("Cartorios", ListaCartorioPage.class);
		mountPage("IncluirCartorio", IncluirCartorioPage.class);
		mountPage("IncluirLayoutEmpresa", IncluirLayoutEmpresaPage.class);
		mountPage("LayoutsPersonalizados", ListaLayoutEmpresaPage.class);
		mountPage("Municipios", ListaMunicipioPage.class);
		mountPage("IncluirMunicipio", IncluirMunicipioPage.class);
		mountPage("TipoInstituicoes", ListaTipoInstituicaoPage.class);
		mountPage("IncluirTipoInstituicao", IncluirTipoInstituicaoPage.class);
		mountPage("TipoArquivo", ListaTipoArquivoPage.class);
		mountPage("IncluirTipoArquivo", IncluirTipoArquivoPage.class);
		mountPage("Usuarios", ListaUsuarioPage.class);
		mountPage("IncluirUsuario", IncluirUsuarioPage.class);
		mountPage("PerfilUsuario", PerfilUsuarioPage.class);
		mountPage("EmpresasFiliadas", ListaFiliadoPage.class);
		mountPage("IncluirEmpresaFiliada", IncluirFiliadoPage.class);

		/** Arquivo */
		mountPage("EnviarArquivo", EnviarArquivoPage.class);
		mountPage("EnviarArquivoEmpresa", EnviarArquivoEmpresaPage.class);

		mountPage("BuscarArquivo", BuscarArquivoPage.class);
		mountPage("BuscarArquivoInstituicao", BuscarArquivoCraInstituicaoPage.class);
		mountPage("ListaArquivo", ListaArquivoPage.class);
		mountPage("TitulosArquivo", TitulosArquivoPage.class);

		mountPage("ListaArquivoInstituicao", ListaArquivoInstituicaoPage.class);
		mountPage("TitulosArquivoInstituicao", TitulosArquivoInstituicaoPage.class);

		mountPage("BuscarDesistenciaCancelamento", BuscarDesistenciaCancelamentoPage.class);
		mountPage("ListaDesistenciaCancelamento", ListaDesistenciaCancelamentoPage.class);
		mountPage("TitulosDesistencia", TitulosDesistenciaPage.class);
		mountPage("TitulosCancelamento", TitulosCancelamentoPage.class);
		mountPage("BuscarTituloSolicitacaoCancelamento", BuscarTituloSolicitacaoCancelamentoPage.class);
		mountPage("ListaTituloSolicitacaoCancelamento", ListaTituloSolicitacaoCancelamentoPage.class);
		mountPage("SolicitacaoCancelamento", TituloSolicitacaoCancelamentoPage.class);
		mountPage("TitulosAutorizacaoCancelamento", TitulosAutorizacaoCancelamentoPage.class);

		mountPage("RemoverArquivo", RemoverArquivoPage.class);
		mountPage("ListaArquivosRemover", ListaRemoverArquivoPage.class);

		mountPage("MonitorarTitulos", MonitorarTitulosPage.class);
		mountPage("ListaTitulos", ListaTitulosPage.class);
		mountPage("HistoricoDoTitulo", HistoricoPage.class);

		/** Relatorios Padrão */
		mountPage("RelatorioArquivosInstituicoesCartorios", RelatorioInstituicoesCartoriosPage.class);

		/** Relatorios CRA */
		mountPage("Relatorio", RelatorioArquivosPage.class);
		mountPage("RelatorioCustasCra", RelatorioCustasCraPage.class);
		mountPage("RelatorioTitulos", RelatorioTitulosPage.class);
		mountPage("RelatorioRetorno", RelatorioRetornoPage.class);

		/** Slip */
		mountPage("InstrumentoDeProtesto", InstrumentoProtestoPage.class);
		mountPage("GerarSlip", GerarSlipPage.class);
		mountPage("BuscarInstrumentoProtesto", BuscarInstrumentoProtestoPage.class);
		mountPage("ImportarArquivoDePara", ImportarArquivoDeParaPage.class);
	}

	/**
	 * Configura as data para a aplicação.
	 */
	private void initAtributoDeDatas() {
		Locale.setDefault(DataUtil.LOCALE);
		DateTimeZone.setDefault(DataUtil.ZONE);
	}

	public boolean isDevelopmentMode() {
		return (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT);
	}

	@Override
	public Component createMenuSistema(AbstractWebPage<?> page, String containerId, Usuario usuario) {
		return new CraMenu("menu", usuario);
	}

	@Override
	public String getTituloSistema(AbstractWebPage<?> page) {
		return "CRA - Central de Remessa de Arquivos";
	}

	/**
	 * Metodo utilitario para obter a aplicacao corrente.
	 */
	public static CraApplication get() {
		return (CraApplication) Application.get();
	}

}
