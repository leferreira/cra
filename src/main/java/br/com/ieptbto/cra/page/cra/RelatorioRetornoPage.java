package br.com.ieptbto.cra.page.cra;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.report.RelatorioUtil;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class RelatorioRetornoPage extends BasePage<Arquivo> {

	private static final long serialVersionUID = 1L;
	private Arquivo arquivo;
	private String pageName;

	public RelatorioRetornoPage(String message, String pageName) {
		this.pageName = pageName;
		success(message);
		adicionarComponentes();
	}

	public RelatorioRetornoPage(String message, Arquivo arquivo, String pageName) {
        this.pageName = pageName;
	    this.arquivo = arquivo;
		success(message);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(new Label("pageName", pageName));
		add(downladoRelatorioRetornoLiberado());
        add(downladRelatorioRetornoEnviadoCartorio());
	}

	private Link<Void> downladoRelatorioRetornoLiberado() {
		Link<Void> link = new Link<Void>("relatorioRetornoLiberado") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				try {
                    JasperPrint jasperPrint = new RelatorioUtil().relatorioRetornoLiberadoGeral(new LocalDate());
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_RETORNO_LIBERADO_"
							+ DataUtil.localDateToString(new LocalDate()).replaceAll("/", "_") + ".pdf"));

				} catch (InfraException ex) {
					logger.info(ex.getMessage(), ex);
				    error(ex.getMessage());
				} catch (Exception e) {
                    logger.info(e.getMessage(), e);
                    error("Não foi possível gerar o relatório ! Favor entrar em contato com a CRA...");
				}
			}
		};
        link.setOutputMarkupId(true);
		if (getArquivo() != null) {
            link.setVisible(false);
		}
		return link;
	}

	private Link<Arquivo> downladRelatorioRetornoEnviadoCartorio() {
		Link<Arquivo> link = new Link<Arquivo>("relatorioRetornoCartorio") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

                try {
                    Remessa retorno = getArquivo().getRemessas().get(0);
                    JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoCartorio(retorno);
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_"
							+ getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));

				} catch (InfraException ex) {
                    logger.info(ex.getMessage(), ex);
                    error(ex.getMessage());
				} catch (Exception e) {
					logger.info(e.getMessage(), e);
                    error("Não foi possível gerar o relatório do arquivo ! Favor entrar em contato com a CRA...");
                }
			}
		};
		link.setOutputMarkupId(true);
		if (getArquivo() == null) {
			link.setVisible(false);
		}
		return link;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return null;
	}
}
