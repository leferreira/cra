package br.com.ieptbto.cra.page.instrumentoProtesto;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.InstrumentoProtestoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.historico.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER })
public class GerarSlipPage extends BasePage<InstrumentoProtesto> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	InstrumentoProtestoMediator instrumentoMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	TituloMediator tituloMediator;
	private List<Retorno> retornos;
	private List<InstrumentoProtesto> instrumentosProtesto;
	private List<EnvelopeSLIP> envelopes;
	private List<EtiquetaSLIP> etiquetas;
	private boolean etiquetasNaoGeradas;

	public GerarSlipPage() {
		this.instrumentosProtesto = instrumentoMediator.buscarInstrumentosParaSlip();
		this.etiquetasNaoGeradas = instrumentoMediator.verificarEtiquetasGeradasNaoConfimadas();
		verificarEtiquetasGeradasNaoConfimadas();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		carregarListaSlips();
		botaoGerarEtiquetas();
		botaoGerarEnvelopes();
		botaoGerarListagem();
		botaoConfirmarGeracaoSlips();
	}

	private void verificarEtiquetasGeradasNaoConfimadas() {
		if (etiquetasNaoGeradas) {
			warn("Existem intrumentos já tiveram Slips geradas porém não foram confirmados!");
		}
	}

	private void carregarListaSlips() {
		add(new ListView<InstrumentoProtesto>("instrumentos", getInstrumentosProtesto()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<InstrumentoProtesto> item) {
				final InstrumentoProtesto instrumentoProtesto = item.getModelObject();
				final Retorno retorno = instrumentoProtesto.getTituloRetorno();

				item.add(new Label("numeroTitulo", retorno.getTitulo().getNumeroTitulo()));
				item.add(new Label("protocolo", retorno.getNumeroProtocoloCartorio()));

				String municipio = retorno.getRemessa().getInstituicaoOrigem().getMunicipio().getNomeMunicipio();
				if (municipio.length() > 20) {
					municipio = municipio.substring(0, 19);
				}
				item.add(new Label("pracaProtesto", municipio.toUpperCase()));
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(retorno.getTitulo()));
					}
				};
				linkHistorico.add(new Label("nomeDevedor", retorno.getTitulo().getNomeDevedor()));
				item.add(linkHistorico);
				item.add(new Label("portador", instituicaoMediator
						.buscarApresentantePorCodigoPortador(retorno.getTitulo().getCodigoPortador()).getNomeFantasia()));
				item.add(new Label("especie", retorno.getTitulo().getEspecieTitulo()));
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", retorno.getTitulo().getValorTitulo()));
				item.add(new Link<InstrumentoProtesto>("buttonRemoverInstrumento") {
					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							instrumentoMediator.removerInstrumento(instrumentoProtesto);
							getInstrumentosProtesto().remove(instrumentoProtesto);
							GerarSlipPage.this.success("Instrumento de protesto com o protocolo "
									+ instrumentoProtesto.getTituloRetorno().getNumeroProtocoloCartorio() + " de "
									+ retorno.getTitulo().getPracaProtesto() + " foi removido com sucesso!");
						} catch (Exception ex) {
							logger.error(ex.getMessage(), ex);
							GerarSlipPage.this
									.error("Não foi possível remover o registro do instrumento de protesto. Entre em contato com a CRA !");
						}
					}
				});
			}
		});
	}

	private void botaoGerarEtiquetas() {
		add(new Link<InstrumentoProtesto>("botaoSlip") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				SimpleDateFormat dataPadrao = new SimpleDateFormat("dd_MM_yy");

				try {
					InstrumentoProtestoMediator instrumento =
							instrumentoMediator.processarInstrumentos(getInstrumentosProtesto(), getRetornos());

					if (instrumento.getEtiquetas().isEmpty()) {
						throw new InfraException("Não foi possível gerar SLIPs. Não há entrada de títulos processados !");
					}
					getEnvelopes().addAll(instrumento.getEnvelopes());
					getEtiquetas().addAll(instrumento.getEtiquetas());

					HashMap<String, Object> parametros = new HashMap<String, Object>();
					JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(instrumento.getEtiquetas());
					JasperReport jasperReport =
							JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/SlipEtiqueta.jrxml"));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);

					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
							"CRA_SLIP_" + dataPadrao.format(new Date()).toString() + ".pdf"));

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					error("Não foi possível gerar as etiquetas ! Entre em contato com a CRA !");
				}
			}
		});
	}

	private void botaoGerarEnvelopes() {
		add(new Link<InstrumentoProtesto>("botaoEnvelope") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				SimpleDateFormat dataPadrao = new SimpleDateFormat("dd_MM_yy");

				try {
					if (getEnvelopes().isEmpty()) {
						throw new InfraException("Não foi possível gerar os envelopes. Não foram processados instrumentos !");
					}

					HashMap<String, Object> parametros = new HashMap<String, Object>();
					JRBeanCollectionDataSource beanCollection = new JRBeanCollectionDataSource(getEnvelopes());
					JasperReport jasperReport =
							JasperCompileManager.compileReport(getClass().getResourceAsStream("../../relatorio/SlipEnvelope.jrxml"));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, beanCollection);

					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
							"CRA_ENVELOPES_" + dataPadrao.format(new Date()).toString() + ".pdf"));

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) {
					error("Não foi possível gerar os envelopes ! Entre em contato com a CRA !");
					logger.error(ex.getMessage(), ex);
				}
			}
		});
	}

	private void botaoGerarListagem() {
		add(new Link<InstrumentoProtesto>("botaoListagem") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				HashMap<String, Object> parametros = new HashMap<String, Object>();
				SimpleDateFormat dataPadrao = new SimpleDateFormat("dd_MM_yy");
				Connection connection = null;
				JasperPrint jasperPrint = null;

				try {
					Class.forName("org.postgresql.Driver");
					connection = DriverManager.getConnection("jdbc:postgresql://192.168.254.233:5432/nova_cra", "postgres", "@dminB3g1n");

					parametros.put("SUBREPORT_DIR", ConfiguracaoBase.RELATORIOS_PATH);
					parametros.put("LOGO", ImageIO.read(getClass().getResource(ConfiguracaoBase.RELATORIOS_PATH + "ieptb.gif")));

					String urlJasper = "../../relatorio/ListagemInstrumentosBancos.jrxml";
					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(urlJasper));
					jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
							"CRA_LISTAGEM_" + dataPadrao.format(new Date()).toString() + ".pdf"));

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) {
					error("Não foi possível gerar a listagem ! Entre em contato com a CRA !");
					logger.error(ex.getMessage(), ex);
				}
			}
		});
	}

	private void botaoConfirmarGeracaoSlips() {
		add(new Link<InstrumentoProtesto>("botaoConfirmar") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				try {
					instrumentoMediator.alterarInstrumentosParaGerado(getInstrumentosProtesto());
					getInstrumentosProtesto().clear();
					success("Os intrumentos foram processados e as Slips foram geradas com sucesso!");

				} catch (InfraException ex) {
					logger.error(ex.getMessage(), ex);
					error(ex.getMessage());
				} catch (Exception ex) {
					error("Não foi possível gerar a listagem ! Entre em contato com a CRA !");
					logger.error(ex.getMessage(), ex);
				}
			}
		});
	}

	public List<InstrumentoProtesto> getInstrumentosProtesto() {
		if (instrumentosProtesto == null) {
			instrumentosProtesto = new ArrayList<InstrumentoProtesto>();
		}
		return instrumentosProtesto;
	}

	public List<EnvelopeSLIP> getEnvelopes() {
		if (envelopes == null) {
			envelopes = new ArrayList<EnvelopeSLIP>();
		}
		return envelopes;
	}

	public List<EtiquetaSLIP> getEtiquetas() {
		if (etiquetas == null) {
			etiquetas = new ArrayList<EtiquetaSLIP>();
		}
		return etiquetas;
	}

	public List<Retorno> getRetornos() {
		if (retornos == null) {
			retornos = new ArrayList<Retorno>();
		}
		return retornos;
	}

	@Override
	protected IModel<InstrumentoProtesto> getModel() {
		return null;
	}
}