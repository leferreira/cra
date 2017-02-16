package br.com.ieptbto.cra.page.desistenciaCancelamento;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.ConfirmacaoMediator;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RetornoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.historico.HistoricoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class TitulosCancelamentoPage extends BasePage<CancelamentoProtesto> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	DownloadMediator downloadMediator;
	@SpringBean
	CancelamentoProtestoMediator cancelamentoMediator;
	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	ConfirmacaoMediator confirmacaoMediator;
	@SpringBean
	RetornoMediator retornoMediator;

	private CancelamentoProtesto cancelamentoProtesto;
	private Instituicao instituicaoDestino;

	public TitulosCancelamentoPage(CancelamentoProtesto cancelamentoProtesto) {
		this.cancelamentoProtesto = cancelamentoProtesto;
		adicionarComponentes();
	}
	
	public TitulosCancelamentoPage(Integer idCancelamentoProtesto) {
		this.cancelamentoProtesto = cancelamentoMediator.buscarCancelamentoPorPK(idCancelamentoProtesto);
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		linkDownloadArquivo();
		informacoesArquivoCancelamento();
		listaPedidosCancelamentos();
	}

	private void linkDownloadArquivo() {
		add(new Link<CancelamentoProtesto>("downloadArquivo") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
					
				try {
					File file = downloadMediator.baixarCancelamentoTXT(getUser(), cancelamentoProtesto);
					IResourceStream resourceStream = new FileResourceStream(file);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
							cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
				} catch (InfraException ex) {
					error(ex.getMessage());
				} catch (Exception e) {
					logger.info(e.getMessage(), e);
					error("Não foi possível baixar o arquivo! Favor entrar em contato com a CRA...");
				}
			}
		});
	}

	private void informacoesArquivoCancelamento() {
		add(new Label("nomeArquivo", cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
		add(new Label("dataEnvio", DataUtil.localDateToString(cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getDataEnvio())));
		add(new Label("enviadoPor", cancelamentoProtesto.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
		add(new Label("destino", getInstituicaoDestino().getNomeFantasia()));
	}

	private void listaPedidosCancelamentos() {
		add(new ListView<PedidoCancelamento>("pedidosCancelamento", carregarPedidosCancelamento()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<PedidoCancelamento> item) {
				final PedidoCancelamento pedidoCancelamento = item.getModelObject();

				if (pedidoCancelamento.getTitulo() == null) {
					item.add(new Label("nossoNumerno", pedidoCancelamento.getCarteiraNossoNumero()));
					item.add(new Label("numeroTitulo", pedidoCancelamento.getNumeroTitulo()));
					item.add(new Label("protocolo", pedidoCancelamento.getNumeroProtocolo()));
					item.add(new Label("pracaProtesto", instituicaoDestino.getMunicipio().getNomeMunicipio().toUpperCase()));
					item.add(new Label("numeroControleDevedor", 1));
					item.add(new LabelValorMonetario<BigDecimal>("valor", pedidoCancelamento.getValorTitulo()));

					Button botaoHistorico = new Button("linkHistorico");
					botaoHistorico.add(new Label("nomeDevedor", pedidoCancelamento.getNomePrimeiroDevedor()));
					item.add(botaoHistorico);
					Button botaoRemessa = new Button("linkRemessa");
					botaoRemessa.add(new Label("nomeRemessa", StringUtils.EMPTY));
					item.add(botaoRemessa);
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					Button botaoRetorno = new Button("linkRetorno");
					botaoRetorno.add(new Label("retorno", StringUtils.EMPTY));
					item.add(botaoRetorno);
					item.add(new Label("dataSituacao", StringUtils.EMPTY));
					item.add(new Label("situacaoTitulo", "TÍTULO ANTIGO"));

				} else {
					pedidoCancelamento.getTitulo().setConfirmacao(confirmacaoMediator.buscarConfirmacaoPorTitulo(pedidoCancelamento.getTitulo()));
					pedidoCancelamento.getTitulo().setRetorno(retornoMediator.buscarRetornoPorTitulo(pedidoCancelamento.getTitulo()));

					final TituloRemessa tituloRemessa = pedidoCancelamento.getTitulo();
					item.add(new LabelValorMonetario<BigDecimal>("valor", tituloRemessa.getSaldoTitulo()));
					item.add(new Label("nossoNumerno", tituloRemessa.getNossoNumero()));

					String municipio = tituloRemessa.getPracaProtesto();
					if (municipio.length() > 20) {
						municipio = municipio.substring(0, 19);
					}
					item.add(new Label("pracaProtesto", municipio.toUpperCase()));
					item.add(new Label("numeroControleDevedor", tituloRemessa.getNumeroControleDevedor()));

					if (tituloRemessa.isDevedorPrincipal()) {
						if (tituloRemessa.getConfirmacao() == null) {
							item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
							item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
							item.add(new Label("protocolo", StringUtils.EMPTY));
							item.add(new Label("dataSituacao", StringUtils.EMPTY));

						} else if (tituloRemessa.getConfirmacao() != null && tituloRemessa.getRetorno() == null) {
							item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
							item.add(new Label("dataConfirmacao",
									DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
							item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
							item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataOcorrencia())));

						} else if (tituloRemessa.getRetorno() != null) {
							item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
							item.add(new Label("dataConfirmacao",
									DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
							item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
							item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
						}

					} else {
						if (tituloRemessa.getConfirmacao() == null) {
							item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
							item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
							item.add(new Label("protocolo", StringUtils.EMPTY));
							item.add(new Label("dataSituacao", StringUtils.EMPTY));

						} else if (tituloRemessa.getConfirmacao() != null && tituloRemessa.getRetorno() != null) {
							item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
							item.add(new Label("dataConfirmacao",
									DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
							item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
							item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));
						} else if (tituloRemessa.getConfirmacao() != null && tituloRemessa.getRetorno() == null) {
							Retorno retornoDevedorPrincipal = tituloMediator.buscarRetornoTituloDevedorPrincipal(tituloRemessa.getConfirmacao());

							if (retornoDevedorPrincipal == null) {
								item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
								item.add(new Label("dataConfirmacao",
										DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
								item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
								item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getConfirmacao().getDataOcorrencia())));
							} else {
								tituloRemessa.setRetorno(retornoDevedorPrincipal);

								item.add(new Label("numeroTitulo", tituloRemessa.getNumeroTitulo()));
								item.add(new Label("dataConfirmacao",
										DataUtil.localDateToString(tituloRemessa.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
								item.add(new Label("protocolo", tituloRemessa.getConfirmacao().getNumeroProtocoloCartorio()));
								item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloRemessa.getRetorno().getDataOcorrencia())));

							}
						}
					}
					item.add(new Label("situacaoTitulo", tituloRemessa.getSituacaoTitulo()));

					Link<Arquivo> linkArquivoRemessa = new Link<Arquivo>("linkRemessa") {

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
			}
		});
	}

	private IModel<List<PedidoCancelamento>> carregarPedidosCancelamento() {
		return new LoadableDetachableModel<List<PedidoCancelamento>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PedidoCancelamento> load() {
				return cancelamentoMediator.buscarPedidosCancelamentoProtesto(cancelamentoProtesto);
			}
		};
	}

	public Instituicao getInstituicaoDestino() {
		if (instituicaoDestino == null) {
			this.instituicaoDestino = instituicaoMediator.getCartorioPorCodigoIBGE(cancelamentoProtesto.getCabecalhoCartorio().getCodigoMunicipio());
		}
		return instituicaoDestino;
	}
	
	@Override
	protected IModel<CancelamentoProtesto> getModel() {
		return new CompoundPropertyModel<CancelamentoProtesto>(cancelamentoProtesto);
	}

}
