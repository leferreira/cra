package br.com.ieptbto.cra.page.titulo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.relatorio.RelatorioUtil;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("rawtypes")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class TitulosArquivoInstituicaoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	ArquivoMediator arquivoMediator;
	@SpringBean
	RemessaMediator remessaMediator;

	private Arquivo arquivo;
	private List<Titulo> titulos;

	public TitulosArquivoInstituicaoPage(Arquivo arquivo) {
		this.arquivo = arquivoMediator.carregarArquivoPorId(arquivo);
		this.titulos = tituloMediator.carregarTitulosGenerico(arquivo);

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(nomeArquivo());
		add(tipoArquivo());
		add(instituicaoEnvio());
		add(instituicaoDestino());
		add(dataEnvio());
		add(usuarioEnvio());
		add(carregarListaTitulos());
		add(botaoGerarRelatorio());
		add(downloadArquivoTXT(getArquivo()));
	}

	private ListView<Titulo> carregarListaTitulos() {
		return new ListView<Titulo>("listViewTituloArquivo", getTitulos()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Titulo> item) {
				final Titulo titulo = item.getModelObject();
				TituloRemessa tituloRemessa = null;

				if (TituloRemessa.class.isInstance(titulo)) {
					tituloRemessa = TituloRemessa.class.cast(titulo);
				} else if (Confirmacao.class.isInstance(titulo)) {
					tituloRemessa = Confirmacao.class.cast(titulo).getTitulo();
				} else if (Retorno.class.isInstance(titulo)) {
					tituloRemessa = Retorno.class.cast(titulo).getTitulo();
				}

				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", titulo.getSaldoTitulo()));
				item.add(new Label("nossoNumero", titulo.getNossoNumero()));
				item.add(new Label("pracaProtesto", tituloRemessa.getPracaProtesto()));
				item.add(new Label("situacaoTitulo", tituloRemessa.getSituacaoTitulo()));
				if (tituloRemessa.getConfirmacao() == null) {
					item.add(new Label("numeroTitulo", titulo.getNumeroTitulo()));
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					item.add(new Label("protocolo", StringUtils.EMPTY));
					item.add(new Label("dataSituacao", StringUtils.EMPTY));

				} else if (tituloRemessa.getConfirmacao() != null && tituloRemessa.getRetorno() == null) {
					item.add(new Label("numeroTitulo", titulo.getNumeroTitulo()));
					item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getDataRecebimento())));
					item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataOcorrencia())));

				} else if (tituloRemessa.getRetorno() != null) {
					item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
					item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getDataRecebimento())));
					item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
				}

				Link<Arquivo> linkArquivoRemessa = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						TituloRemessa t = new TituloRemessa();
						if (TituloRemessa.class.isInstance(titulo)) {
							t = TituloRemessa.class.cast(titulo);
						} else if (Confirmacao.class.isInstance(titulo)) {
							t = Confirmacao.class.cast(titulo).getTitulo();
						} else if (Retorno.class.isInstance(titulo)) {
							t = Retorno.class.cast(titulo).getTitulo();
						}
						setResponsePage(new TitulosArquivoPage(t.getRemessa()));
					}
				};
				linkArquivoRemessa.add(new Label("nomeRemessa", tituloRemessa.getRemessa().getArquivo().getNomeArquivo()));
				item.add(linkArquivoRemessa);

				Link<T> linkHistorico = new Link<T>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						TituloRemessa tituloHistorico = new TituloRemessa();
						if (T.class.isInstance(titulo)) {
							tituloHistorico = TituloRemessa.class.cast(titulo);
						} else if (Confirmacao.class.isInstance(titulo)) {
							tituloHistorico = Confirmacao.class.cast(titulo).getTitulo();
						} else if (Retorno.class.isInstance(titulo)) {
							tituloHistorico = Retorno.class.cast(titulo).getTitulo();
						}
						setResponsePage(new HistoricoPage(tituloHistorico));
					}
				};
				if (tituloRemessa.getNomeDevedor().length() > 25) {
					linkHistorico.add(new Label("nomeDevedor", tituloRemessa.getNomeDevedor().substring(0, 24)));
				} else {
					linkHistorico.add(new Label("nomeDevedor", tituloRemessa.getNomeDevedor()));
				}
				item.add(linkHistorico);

				Link<Retorno> linkRetorno = new Link<Retorno>("linkRetorno") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						Remessa retorno = new Remessa();
						if (TituloRemessa.class.isInstance(titulo)) {
							retorno = TituloRemessa.class.cast(titulo).getRetorno().getRemessa();
						} else if (Confirmacao.class.isInstance(titulo)) {
							retorno = Confirmacao.class.cast(titulo).getTitulo().getRetorno().getRemessa();
						} else if (Retorno.class.isInstance(titulo)) {
							retorno = Retorno.class.cast(titulo).getTitulo().getRetorno().getRemessa();
						}
						setResponsePage(new TitulosArquivoPage(retorno));
					}
				};
				if (tituloRemessa.getRetorno() != null) {
					linkRetorno.add(new Label("retorno", tituloRemessa.getRetorno().getRemessa().getArquivo().getNomeArquivo()));
				} else {
					linkRetorno.add(new Label("retorno", StringUtils.EMPTY));
				}
				item.add(linkRetorno);
			}
		};
	}

	private Link<Arquivo> botaoGerarRelatorio() {
		return new Link<Arquivo>("gerarRelatorio") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				try {
					JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoInstiuicao(getArquivo());
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_"
							+ arquivo.getNomeArquivo().replace(".", "_") + ".pdf"));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
	}

	private Link<Arquivo> downloadArquivoTXT(final Arquivo arquivo) {
		return new Link<Arquivo>("downloadArquivo") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				File file = arquivoMediator.baixarArquivoTXT(getUser().getInstituicao(), arquivo);
				IResourceStream resourceStream = new FileResourceStream(file);

				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo()));
			}
		};
	}

	private Label nomeArquivo() {
		return new Label("nomeArquivo", getArquivo().getNomeArquivo());
	}

	private Label tipoArquivo() {
		return new Label("tipo", getArquivo().getTipoArquivo().getTipoArquivo().getLabel());
	}

	private Label instituicaoEnvio() {
		return new Label("instituicaoEnvio", getArquivo().getInstituicaoEnvio().getNomeFantasia());
	}

	private Label instituicaoDestino() {
		return new Label("instituicaoDestino", getArquivo().getInstituicaoRecebe().getNomeFantasia());
	}

	private Label usuarioEnvio() {
		return new Label("usuario", getArquivo().getUsuarioEnvio().getNome());
	}

	private Label dataEnvio() {
		return new Label("dataEnvio", DataUtil.localDateToString(getArquivo().getDataEnvio()));
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	private List<Titulo> getTitulos() {
		return titulos;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
