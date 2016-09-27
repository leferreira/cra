package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.DownloadMediator;
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
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class TitulosArquivoInstituicaoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ArquivoMediator arquivoMediator;
	@SpringBean
	private DownloadMediator downloadMediator;

	private Arquivo arquivo;

	public TitulosArquivoInstituicaoPage(Arquivo arquivo) {
		this.arquivo = arquivoMediator.carregarArquivoPorId(arquivo);

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(labelNomeArquivo());
		add(labelTipoArquivo());
		add(labelInstituicaoEnvio());
		add(labelInstituicaoDestino());
		add(labelDataEnvio());
		add(labelUsuarioEnvio());
		add(carregarListaTitulos());
		add(botaoGerarRelatorio());
		add(downloadArquivoTXT(arquivo));
	}

	private ListView<TituloRemessa> carregarListaTitulos() {
		return new ListView<TituloRemessa>("listViewTituloArquivo", buscarTituloArquivo()) {
			/***/
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloRemessa = item.getModelObject();
				// item.add(new LabelValorMonetario<BigDecimal>("valorTitulo",
				// tituloRemessa.getSaldoTitulo()));
				// item.add(new Label("nossoNumero",
				// tituloRemessa.getNossoNumero()));
				// String municipio = tituloRemessa.getPracaProtesto();
				// if (municipio.length() > 20) {
				// municipio = municipio.substring(0, 19);
				// }
				// item.add(new Label("pracaProtesto",
				// municipio.toUpperCase()));
				// if (tituloRemessa.getConfirmacao() == null) {
				// item.add(new Label("numeroTitulo",
				// tituloRemessa.getNumeroTitulo()));
				// item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
				// item.add(new Label("protocolo", StringUtils.EMPTY));
				// item.add(new Label("dataSituacao", StringUtils.EMPTY));
				//
				// } else if (tituloRemessa.getConfirmacao() != null &&
				// tituloRemessa.getRetorno() == null) {
				// item.add(new Label("numeroTitulo",
				// tituloRemessa.getNumeroTitulo()));
				// item.add(new Label("dataConfirmacao",
				// DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().arquivo.getDataEnvio())));
				// item.add(new Label("protocolo",
				// tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
				// item.add(new Label("dataSituacao",
				// DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataOcorrencia())));
				//
				// } else if (tituloRemessa.getRetorno() != null) {
				// item.add(new Label("numeroTitulo",
				// tituloRemessa.getNumeroTitulo()));
				// item.add(new Label("dataConfirmacao",
				// DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().arquivo.getDataEnvio())));
				// item.add(new Label("protocolo",
				// tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
				// item.add(new Label("dataSituacao",
				// DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
				// }
				// item.add(new Label("situacaoTitulo",
				// tituloRemessa.getSituacaoTitulo()));
				//
				// Link<Arquivo> linkArquivoRemessa = new
				// Link<Arquivo>("linkArquivo") {
				//
				// /***/
				// private static final long serialVersionUID = 1L;
				//
				// public void onClick() {
				// setResponsePage(new
				// TitulosArquivoPage(tituloRemessa.getRemessa()));
				// }
				// };
				// linkArquivoRemessa.add(new Label("nomeRemessa",
				// tituloRemessa.getRemessa().arquivo.getNomeArquivo()));
				// item.add(linkArquivoRemessa);
				//
				// Link<TituloRemessa> linkHistorico = new
				// Link<TituloRemessa>("linkHistorico") {
				//
				// /***/
				// private static final long serialVersionUID = 1L;
				//
				// public void onClick() {
				// setResponsePage(new HistoricoPage(tituloRemessa));
				// }
				// };
				// if (tituloRemessa.getNomeDevedor().length() > 25) {
				// linkHistorico.add(new Label("nomeDevedor",
				// tituloRemessa.getNomeDevedor().substring(0, 24)));
				// } else {
				// linkHistorico.add(new Label("nomeDevedor",
				// tituloRemessa.getNomeDevedor()));
				// }
				// item.add(linkHistorico);
				//
				// Link<Retorno> linkRetorno = new Link<Retorno>("linkRetorno")
				// {
				//
				// /***/
				// private static final long serialVersionUID = 1L;
				//
				// public void onClick() {
				// setResponsePage(new
				// TitulosArquivoPage(tituloRemessa.getRetorno().getRemessa()));
				// }
				// };
				// if (tituloRemessa.getRetorno() != null) {
				// linkRetorno.add(new Label("retorno",
				// tituloRemessa.getRetorno().getRemessa().arquivo.getNomeArquivo()));
				// } else {
				// linkRetorno.add(new Label("retorno", StringUtils.EMPTY));
				// }
				// item.add(linkRetorno);
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
					JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoInstiuicao(arquivo);
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
							new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_" + arquivo.getNomeArquivo().replace(".", "_") + ".pdf"));
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
				File file = downloadMediator.baixarArquivoTXT(getUser().getInstituicao(), arquivo);
				IResourceStream resourceStream = new FileResourceStream(file);

				getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, arquivo.getNomeArquivo()));
			}
		};
	}

	private Label labelNomeArquivo() {
		return new Label("nomeArquivo", arquivo.getNomeArquivo());
	}

	private Label labelTipoArquivo() {
		return new Label("tipo", arquivo.getTipoArquivo().getTipoArquivo().getLabel());
	}

	private Label labelInstituicaoEnvio() {
		return new Label("instituicaoEnvio", arquivo.getInstituicaoEnvio().getNomeFantasia());
	}

	private Label labelInstituicaoDestino() {
		return new Label("instituicaoDestino", arquivo.getInstituicaoRecebe().getNomeFantasia());
	}

	private Label labelUsuarioEnvio() {
		return new Label("usuario", arquivo.getUsuarioEnvio().getNome());
	}

	private Label labelDataEnvio() {
		return new Label("dataEnvio", DataUtil.localDateToString(arquivo.getDataEnvio()));
	}

	public IModel<List<TituloRemessa>> buscarTituloArquivo() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				return null;
			}
		};
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}