package br.com.ieptbto.cra.page.arquivo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;

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
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Titulo;
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
@SuppressWarnings("rawtypes")
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
	private List<Titulo> titulos;

	public TitulosArquivoPage(Remessa remessa) {
		this.remessa = remessaMediator.carregarRemessaPorId(remessa);
		this.titulos = tituloMediator.carregarTitulosGenerico(remessa);
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

	private void listaTitulosArquivos() {
		ListView<Titulo> listView = new ListView<Titulo>("listViewTituloArquivo", getTitulos()) {

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

				if (tituloRemessa.getConfirmacao() != null) {
					tituloRemessa.setConfirmacao(tituloMediator.carregarTituloConfirmacao(tituloRemessa.getConfirmacao()));
				}
				if (tituloRemessa.getRetorno() != null) {
					tituloRemessa.setRetorno(tituloMediator.carregarTituloRetorno(tituloRemessa.getRetorno()));
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

				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						TituloRemessa tituloHistorico = new TituloRemessa();
						if (TituloRemessa.class.isInstance(titulo)) {
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
		add(listView);
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
						warn("Arquivo já bloqueado anteriormente!");
					} else {
						remessaMediator.alterarParaDevolvidoPelaCRA(remessa);
						getRemessa().setDevolvidoPelaCRA(true);
						info("Arquivo bloqueado com sucesso !");
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
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "CRA_RELATORIO_"
							+ remessa.getArquivo().getNomeArquivo().replace(".", "_") + ".pdf"));
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

					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
	}

	private List<Titulo> getTitulos() {
		return titulos;
	}

	public Remessa getRemessa() {
		return remessa;
	}

	@Override
	protected IModel<Remessa> getModel() {
		return new CompoundPropertyModel<Remessa>(remessa);
	}
}
