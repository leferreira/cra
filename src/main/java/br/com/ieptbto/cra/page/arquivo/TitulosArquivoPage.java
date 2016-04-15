package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
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
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.HistoricoPage;
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
public class TitulosArquivoPage extends BasePage<Remessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessaMediator;
	@SpringBean
	TituloMediator tituloMediator;

	private Remessa remessa;

	public TitulosArquivoPage(Remessa remessa) {
		this.remessa = remessaMediator.carregarRemessaPorId(remessa);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		divInformacoesArquivo();
		listaTitulosArquivos();
	}

	private void divInformacoesArquivo() {
		add(botaoBloquearRemessa());
		add(botaoGerarRelatorio());
		add(linkDownloadArquivoTXT());
		add(new Label("nomeArquivo", getRemessa().getArquivo().getNomeArquivo()));
		add(new Label("dataEnvio", DataUtil.localDateToString(getRemessa().getArquivo().getDataEnvio())));
		add(new Label("enviadoPor", getRemessa().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
		add(new Label("destino", getRemessa().getInstituicaoDestino().getNomeFantasia()));
	}

	private Link<Remessa> botaoBloquearRemessa() {
		final Link<Remessa> bloquearRemessa = new Link<Remessa>("bloquearRemessa") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				Remessa remessa = getRemessa();

				try {
					if (remessa.getDevolvidoPelaCRA().equals(true)) {
						info("Arquivo já bloqueado anteriormente!");
					} else {
						remessaMediator.alterarParaDevolvidoPelaCRA(remessa);
						getRemessa().setDevolvidoPelaCRA(true);
						success("Arquivo bloqueado com sucesso !");
					}
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível bloquear o arquivo ! Entre em contato com a CRA ");
				}
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);

				if (remessa.getDevolvidoPelaCRA()) {
					tag.put("class", "btn btn-danger btn-sm pull-right");
				} else {
					tag.put("class", "btn btn-warning btn-sm pull-right");
				}
			}
		};
		bloquearRemessa.setVisible(false);
		if (getRemessa().getArquivo().getTipoArquivo().getTipoArquivo().equals(TipoArquivoEnum.REMESSA)
				&& getUser().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			bloquearRemessa.setVisible(true);
		}
		if (remessa.getDevolvidoPelaCRA()) {
			bloquearRemessa.setEnabled(false);
			bloquearRemessa.add(new Label("labelBloqueado", "BLOQUEADO"));
		} else {
			bloquearRemessa.add(new Label("labelBloqueado", "BLOQUEAR"));
		}
		return bloquearRemessa;
	}

	private Link<Remessa> botaoGerarRelatorio() {
		return new Link<Remessa>("gerarRelatorio") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				try {
					JasperPrint jasperPrint = new RelatorioUtil().relatorioArquivoCartorio(getRemessa());
					File pdf = File.createTempFile("report", ".pdf");
					JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(pdf));
					IResourceStream resourceStream = new FileResourceStream(pdf);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
							"CRA_RELATORIO_" + remessa.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível gerar o relatório do arquivo ! Entre em contato com a CRA !");
					e.printStackTrace();
				}
			}
		};
	}

	private Link<Remessa> linkDownloadArquivoTXT() {
		return new Link<Remessa>("downloadArquivo") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					File file = remessaMediator.baixarRemessaTXT(getUser(), remessa);
					IResourceStream resourceStream = new FileResourceStream(file);

					getRequestCycle()
							.scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
	}

	private void listaTitulosArquivos() {
		ListView<TituloRemessa> listView = new ListView<TituloRemessa>("listViewTituloArquivo", tituloMediator.carregarTitulos(remessa)) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloRemessa = item.getModelObject();
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", tituloRemessa.getSaldoTitulo()));
				item.add(new Label("nossoNumero", tituloRemessa.getNossoNumero()));
				item.add(new Label("pracaProtesto", tituloRemessa.getPracaProtesto()));
				item.add(new Label("situacaoTitulo", tituloRemessa.getSituacaoTitulo()));
				if (tituloRemessa.getConfirmacao() == null) {
					item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					item.add(new Label("protocolo", StringUtils.EMPTY));
					item.add(new Label("dataSituacao", StringUtils.EMPTY));

				} else if (tituloRemessa.getConfirmacao() != null && tituloRemessa.getRetorno() == null) {
					item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
					item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
					item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataOcorrencia())));

				} else if (tituloRemessa.getRetorno() != null) {
					item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
					item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
					item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
				}

				Link<Arquivo> linkArquivoRemessa = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new TitulosArquivoPage(tituloRemessa.getRemessa()));
					}
				};
				linkArquivoRemessa.add(new Label("nomeRemessa", tituloRemessa.getRemessa().getArquivo().getNomeArquivo()));
				item.add(linkArquivoRemessa);

				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloRemessa));
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
						setResponsePage(new TitulosArquivoPage(tituloRemessa.getRetorno().getRemessa()));
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
		add(listView);
	}

	public Remessa getRemessa() {
		return remessa;
	}

	@Override
	protected IModel<Remessa> getModel() {
		return new CompoundPropertyModel<Remessa>(remessa);
	}
}
