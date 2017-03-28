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
import br.com.ieptbto.cra.page.administracao.IncluirTaxaCraPage;
import br.com.ieptbto.cra.page.administracao.ListaRemoverArquivoPage;
import br.com.ieptbto.cra.page.administracao.RemoverArquivoPage;
import br.com.ieptbto.cra.page.administracao.WebServiceConfiguracaoPage;
import br.com.ieptbto.cra.page.arquivo.BuscarArquivoPage;
import br.com.ieptbto.cra.page.arquivo.EnviarArquivoPage;
import br.com.ieptbto.cra.page.arquivo.ListaArquivoCartorioPage;
import br.com.ieptbto.cra.page.arquivo.ListaArquivoInstituicaoPage;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoInstituicaoPage;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.NotFoundPage;
import br.com.ieptbto.cra.page.base.SobreCraPage;
import br.com.ieptbto.cra.page.batimento.BatimentoPage;
import br.com.ieptbto.cra.page.batimento.BuscarDepositoPage;
import br.com.ieptbto.cra.page.batimento.ConflitoDepositosArquivoRetornoPage;
import br.com.ieptbto.cra.page.batimento.ImportarExtratoPage;
import br.com.ieptbto.cra.page.batimento.IncluirDepositoPage;
import br.com.ieptbto.cra.page.batimento.LiberarRetornoPage;
import br.com.ieptbto.cra.page.batimento.ListaDepositoPage;
import br.com.ieptbto.cra.page.batimento.RetornosLiberadosPage;
import br.com.ieptbto.cra.page.cartorio.IncluirCartorioPage;
import br.com.ieptbto.cra.page.cartorio.ListaCartorioPage;
import br.com.ieptbto.cra.page.centralDeAcoes.CentralDeAcoesPage;
import br.com.ieptbto.cra.page.cnp.CentralNacionalProtestoPage;
import br.com.ieptbto.cra.page.cnp.Importar5AnosCartorioPage;
import br.com.ieptbto.cra.page.convenio.GerarRemessaConvenioPage;
import br.com.ieptbto.cra.page.cra.GerarConfirmacaoPage;
import br.com.ieptbto.cra.page.cra.GerarDesistenciasCancelamentosPage;
import br.com.ieptbto.cra.page.cra.GerarRetornoPage;
import br.com.ieptbto.cra.page.cra.RelatorioRetornoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.BuscarDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.ListaDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao.DownloadOficioIrregularidadePage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao.EnviarSolicitacaoDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao.SolicitarDesistenciaCancelamentoPage;
import br.com.ieptbto.cra.page.filiado.AtualizarEmpresasFiliadasPage;
import br.com.ieptbto.cra.page.filiado.IncluirFiliadoPage;
import br.com.ieptbto.cra.page.filiado.ListaFiliadoPage;
import br.com.ieptbto.cra.page.home.HomePage;
import br.com.ieptbto.cra.page.instituicao.IncluirInstituicaoPage;
import br.com.ieptbto.cra.page.instituicao.ListaInstituicaoPage;
import br.com.ieptbto.cra.page.instrumentoProtesto.BuscarInstrumentoProtestoPage;
import br.com.ieptbto.cra.page.instrumentoProtesto.GerarSlipPage;
import br.com.ieptbto.cra.page.instrumentoProtesto.ImportarArquivoDeParaPage;
import br.com.ieptbto.cra.page.instrumentoProtesto.InstrumentoProtestoPage;
import br.com.ieptbto.cra.page.layoutPersonalizado.IncluirLayoutEmpresaPage;
import br.com.ieptbto.cra.page.layoutPersonalizado.ListaLayoutEmpresaPage;
import br.com.ieptbto.cra.page.login.LoginPage;
import br.com.ieptbto.cra.page.municipio.IncluirMunicipioPage;
import br.com.ieptbto.cra.page.municipio.ListaMunicipioPage;
import br.com.ieptbto.cra.page.relatorio.RelatorioArquivosPage;
import br.com.ieptbto.cra.page.relatorio.titulo.RelatorioTitulosPage;
import br.com.ieptbto.cra.page.tipoArquivo.IncluirTipoArquivoPage;
import br.com.ieptbto.cra.page.tipoArquivo.ListaTipoArquivoPage;
import br.com.ieptbto.cra.page.tipoInstituicao.IncluirTipoInstituicaoPage;
import br.com.ieptbto.cra.page.tipoInstituicao.ListaTipoInstituicaoPage;
import br.com.ieptbto.cra.page.titulo.BuscarTitulosPage;
import br.com.ieptbto.cra.page.titulo.ListaTitulosPage;
import br.com.ieptbto.cra.page.titulo.historico.HistoricoPage;
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
		getRequestCycleSettings().setGatherExtendedBrowserInfo(true);

		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

		getRequestCycleSettings().setTimeout(Duration.minutes(30));

		// don't throw exceptions for missing translations
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		getApplicationSettings().setPageExpiredErrorPage(LoginPage.class);
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
		mountPage("NotFoundPage", NotFoundPage.class);
		mountPage("LoginPage", LoginPage.class);
		mountPage("HomePage", HomePage.class);
		mountPage("SobreCra", SobreCraPage.class);
		mountPage("CentralDeAcoes", CentralDeAcoesPage.class);
		mountPage("CentralNacionalProtesto", CentralNacionalProtestoPage.class);
		mountPage("CargaInicial", CargaInicialPage.class);
		mountPage("RemoverArquivo", RemoverArquivoPage.class);
		mountPage("ListaArquivosRemover", ListaRemoverArquivoPage.class);
		mountPage("WebServiceConfiguracao", WebServiceConfiguracaoPage.class);
		mountPage("ImportarArquivo5Anos", Importar5AnosCartorioPage.class);
		mountPage("IncluirTaxaCra", IncluirTaxaCraPage.class);
		mountPage("Batimento", BatimentoPage.class);
		mountPage("ImportarExtrato", ImportarExtratoPage.class);
		mountPage("ConflitoDepositosArquivoRetorno", ConflitoDepositosArquivoRetornoPage.class);
		mountPage("BuscarDepositos", BuscarDepositoPage.class);
		mountPage("LiberarRetornos", LiberarRetornoPage.class);
		mountPage("RetornosLiberados", RetornosLiberadosPage.class);
		mountPage("ListaDepositos", ListaDepositoPage.class);
		mountPage("Deposito", IncluirDepositoPage.class);
		mountPage("GerarRemessasConvenio", GerarRemessaConvenioPage.class);
		mountPage("GerarConfirmacao", GerarConfirmacaoPage.class);
		mountPage("GerarRetorno", GerarRetornoPage.class);
		mountPage("GerarDesistenciasCancelamentos", GerarDesistenciasCancelamentosPage.class);
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
		mountPage("TipoArquivos", ListaTipoArquivoPage.class);
		mountPage("IncluirTipoArquivo", IncluirTipoArquivoPage.class);
		mountPage("Usuarios", ListaUsuarioPage.class);
		mountPage("IncluirUsuario", IncluirUsuarioPage.class);
		mountPage("PerfilUsuario", PerfilUsuarioPage.class);
		mountPage("EmpresasFiliadas", ListaFiliadoPage.class);
		mountPage("IncluirEmpresaFiliada", IncluirFiliadoPage.class);
		mountPage("AtualizarEmpresasFiliadas", AtualizarEmpresasFiliadasPage.class);
		mountPage("EnviarArquivo", EnviarArquivoPage.class);
		mountPage("BuscarArquivo", BuscarArquivoPage.class);
		mountPage("ListaArquivo", ListaArquivoCartorioPage.class);
		mountPage("TitulosArquivo", TitulosArquivoPage.class);
		mountPage("ListaArquivoInstituicao", ListaArquivoInstituicaoPage.class);
		mountPage("TitulosArquivoInstituicao", TitulosArquivoInstituicaoPage.class);
		mountPage("BuscarDesistenciaCancelamento", BuscarDesistenciaCancelamentoPage.class);
		mountPage("ListaDesistenciaCancelamento", ListaDesistenciaCancelamentoPage.class);
		mountPage("TitulosDesistencia", TitulosDesistenciaPage.class);
		mountPage("TitulosCancelamento", TitulosCancelamentoPage.class);
		mountPage("TitulosAutorizacaoCancelamento", TitulosAutorizacaoCancelamentoPage.class);
		mountPage("SolicitarDesistenciaCancelamento", SolicitarDesistenciaCancelamentoPage.class);
		mountPage("EnviarSolicitacaoDesistenciaCancelamento", EnviarSolicitacaoDesistenciaCancelamentoPage.class);
		mountPage("DownloadOficioIrregularidade", DownloadOficioIrregularidadePage.class);
		mountPage("MonitorarTitulos", BuscarTitulosPage.class);
		mountPage("ListaTitulos", ListaTitulosPage.class);
		mountPage("Historico", HistoricoPage.class);
		mountPage("Relatorio", RelatorioArquivosPage.class);
		mountPage("RelatorioTitulos", RelatorioTitulosPage.class);
		mountPage("RelatorioRetorno", RelatorioRetornoPage.class);
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