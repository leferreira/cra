package br.com.ieptbto.cra.page.cra;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioRetornoPage extends BasePage<Retorno> {

	/***/
	private static final long serialVersionUID = 1L;

	private Arquivo arquivo;
	private Retorno retorno;
	private String pageName;

	public RelatorioRetornoPage(String message, String pageName) {
		this.retorno = new Retorno();
		this.pageName = pageName;

		info(message);
		adicionarComponentes();
	}

	public RelatorioRetornoPage(String message, Arquivo arquivo, String pageName) {
		this.retorno = new Retorno();
		this.arquivo = arquivo;

		info(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		labelNomeDaPagina();
		linkRelatorioRetornoLiberado();
		linkDownloadRelatorioRetornoCartorio();
	}

	private void labelNomeDaPagina() {
		add(new Label("pageName", pageName));
	}

	private void linkRelatorioRetornoLiberado() {
		Link<Retorno> linkRelatorioRetornoLiberado = new Link<Retorno>("relatorioRetornoLiberado") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				Connection connection = null;
				JasperPrint jasperPrint = null;
				HashMap<String, Object> parametros = new HashMap<String, Object>();

				try {
					Class.forName("org.postgresql.Driver");
					connection = DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra", "postgres", "@dminB3g1n");

					parametros.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_PATH);
					parametros.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_PATH + "ieptb.gif")));
					parametros.put("DATA_GERACAO", new LocalDate().toDate());

					String urlJasper = "../../relatorio/RelatorioRetornoLiberado.jrxml";
					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(urlJasper));
					jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_RETORNO_GERADO_"
							+ DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));

				} catch (InfraException ex) {
					ex.printStackTrace();
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
		linkRelatorioRetornoLiberado.setOutputMarkupId(true);
		if (getArquivo() != null) {
			linkRelatorioRetornoLiberado.setVisible(false);
		}
		add(linkRelatorioRetornoLiberado);
	}

	private void linkDownloadRelatorioRetornoCartorio() {
		Link<Arquivo> linkDownloadRelatorioRetornoCartorio = new Link<Arquivo>("relatorioRetornoCartorio") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				Remessa retorno = null;
				for (Remessa r : getArquivo().getRemessas()) {
					retorno = r;
				}

				try {
					JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoCartorio(retorno);
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_"
							+ getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));

				} catch (InfraException ex) {
					error(ex.getMessage());
					ex.printStackTrace();
				} catch (Exception e) {
					error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
		linkDownloadRelatorioRetornoCartorio.setOutputMarkupId(true);
		if (getArquivo() == null) {
			linkDownloadRelatorioRetornoCartorio.setVisible(false);
		}
		add(linkDownloadRelatorioRetornoCartorio);
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	@Override
	protected IModel<Retorno> getModel() {
		return new CompoundPropertyModel<Retorno>(retorno);
	}
}
