package br.com.ieptbto.cra.page.base;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.LogCra;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoInstituicaoCRA;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.AutorizacaoCancelamentoMediator;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.DesistenciaProtestoMediator;
import br.com.ieptbto.cra.mediator.DownloadMediator;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.LoggerMediator;
import br.com.ieptbto.cra.mediator.MunicipioMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.arquivo.ListaArquivoPendentePage;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.centralDeAcoes.LogCraPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosAutorizacaoCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosCancelamentoPage;
import br.com.ieptbto.cra.page.desistenciaCancelamento.TitulosDesistenciaPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.PeriodoDataUtil;

/**
 * 
 * @author Lefer
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class HomePage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ArquivoMediator arquivoMediator;
	@SpringBean
	private DownloadMediator downloadMediator;
	@SpringBean
	private RemessaMediator remessaMediator;
	@SpringBean
	private MunicipioMediator municipioMediator;
	@SpringBean
	private DesistenciaProtestoMediator desistenciaMediator;
	@SpringBean
	private CancelamentoProtestoMediator cancelamentoMediator;
	@SpringBean
	private AutorizacaoCancelamentoMediator autorizacaoMediator;
	@SpringBean
	private InstituicaoMediator instituicaoMediator;
	@SpringBean
	private LoggerMediator loggerMediator;

	private Arquivo arquivo;
	private Usuario usuario;

	public HomePage() {
		super();
		this.usuario = getUser();
		this.arquivo = arquivoMediator.arquivosPendentes(getUsuario().getInstituicao());

		adicionarComponentes();
	}

	public HomePage(PageParameters parameters) {
		this.usuario = getUser();
		this.arquivo = arquivoMediator.arquivosPendentes(getUsuario().getInstituicao());

		error(parameters.get("error"));
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		divInformacoes();
		divCentralAcoes();
		labelQtdRemessasPendentes();
		labelQtdDesistenciasCancelamentosPendentes();
		linkArquivosPendentes();
		listaConfirmacoesPendentes();
		listaDesistenciaPendentes();
		listaCancelamentoPendentes();
		listaAutorizacaoCancelamentoPendentes();
	}

	private void divInformacoes() {
		WebMarkupContainer divInformacoes = new WebMarkupContainer("informacoes");
		divInformacoes.setOutputMarkupId(true);
		divInformacoes.setVisible(false);
		if (!usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			divInformacoes.setVisible(true);
		}
		add(divInformacoes);
	}

	private void divCentralAcoes() {
		WebMarkupContainer divCentralAcoes = new WebMarkupContainer("centralAcoes");
		divCentralAcoes.setOutputMarkupId(true);
		divCentralAcoes.setVisible(false);
		if (usuario.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
			divCentralAcoes.setVisible(true);
		}
		divCentralAcoes.add(new ListView<LogCra>("listUltimosErrosLog", loggerMediator.buscarUltimosLogDeErros()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<LogCra> item) {
				final LogCra log = item.getModelObject();
				item.add(new Label("instituicao", log.getInstituicao()));
				item.add(new Label("tipoLog", log.getTipoLog().getLabel()).setOutputMarkupId(true).setMarkupId(log.getTipoLog().getIdHtml()));
				item.add(new Label("descricao", log.getDescricao()));
				item.add(new Link<LogCra>("descricaoGeral") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new LogCraPage(log));
					}
				});
			}
		});
		add(divCentralAcoes);
	}

	private void labelQtdRemessasPendentes() {
		int quantidade = 0;
		if (getConfirmacoesPendentes() != null) {
			quantidade = getConfirmacoesPendentes().size();
		}
		add(new Label("qtdRemessas", quantidade));
	}

	private void labelQtdDesistenciasCancelamentosPendentes() {
		int quantidade = 0;
		if (getDesistenciaPendentes() != null) {
			quantidade = quantidade + getDesistenciaPendentes().size();
		}
		if (getCancelamentoPendentes() != null) {
			quantidade = quantidade + getCancelamentoPendentes().size();
		}
		if (getAutorizacaoCancelamentoPendentes() != null) {
			quantidade = quantidade + getAutorizacaoCancelamentoPendentes().size();
		}
		add(new Label("qtdCancelamentos", quantidade));
	}

	private void linkArquivosPendentes() {
		add(new Link<Remessa>("arquivosPendentes") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new ListaArquivoPendentePage(getArquivo()));
			}
		});
	}

	private void listaConfirmacoesPendentes() {
		add(new ListView<Remessa>("listConfirmacoes", getConfirmacoesPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						setResponsePage(new TitulosArquivoPage(remessa));
					}
				};
				linkArquivo.add(new Label("arquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					item.add(new Label("instituicao", remessa.getInstituicaoOrigem().getNomeFantasia().toUpperCase()));
					item.add(downloadAnexos(remessa));
				} else if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA)) {
					String municipio = remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio();
					if (municipio.length() > 20) {
						municipio = municipio.substring(0, 19);
					}
					item.add(new Label("instituicao", municipio.toUpperCase()));
					item.add(downloadAnexos(remessa));
				} else {
					String municipio = remessa.getInstituicaoDestino().getMunicipio().getNomeMunicipio();
					if (municipio.length() > 20) {
						municipio = municipio.substring(0, 19);
					}
					item.add(new Label("instituicao", municipio.toUpperCase()));
					item.add(new Label("downloadAnexos", StringUtils.EMPTY));
				}
				item.add(new Label("pendente", PeriodoDataUtil.diferencaDeDiasEntreData(remessa.getArquivo().getDataRecebimento(), new Date())));
				item.add(downloadArquivoTXT(remessa));
			}

			private Link<Remessa> downloadArquivoTXT(final Remessa remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							File file = downloadMediator.baixarRemessaTXT(getUsuario(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);

							getRequestCycle().scheduleRequestHandlerAfterCurrent(
									new ResourceStreamRequestHandler(resourceStream, remessa.getArquivo().getNomeArquivo()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							e.printStackTrace();
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};
			}

			private Link<Remessa> downloadAnexos(final Remessa remessa) {
				List<Anexo> anexos = remessaMediator.verificarAnexosRemessa(remessa);
				Link<Remessa> linkAnexos = new Link<Remessa>("downloadAnexos") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							File file = remessaMediator.processarArquivosAnexos(getUsuario(), remessa);
							IResourceStream resourceStream = new FileResourceStream(file);

							getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, file.getName()));
						} catch (InfraException ex) {
							getFeedbackPanel().error(ex.getMessage());
						} catch (Exception e) {
							e.printStackTrace();
							getFeedbackPanel().error("Não foi possível baixar o arquivo ! \n Entre em contato com a CRA ");
						}
					}
				};

				if (anexos != null) {
					if (anexos.isEmpty()) {
						linkAnexos.setOutputMarkupId(false);
						linkAnexos.setVisible(false);
					}
				}
				return linkAnexos;
			}
		});
	}

	private void listaCancelamentoPendentes() {
		add(new ListView<CancelamentoProtesto>("listaCancelamentos", getCancelamentoPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<CancelamentoProtesto> item) {
				final CancelamentoProtesto cancelamento = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosCancelamentoPage(cancelamento));
					}
				};
				linkArquivo.add(new Label("desistencia", cancelamento.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA) || getUsuario()
						.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					item.add(new Label("banco", municipioMediator
							.buscaMunicipioPorCodigoIBGE(cancelamento.getCabecalhoCartorio().getCodigoMunicipio()).getNomeMunicipio().toUpperCase()));
				} else if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = cancelamento.getRemessaCancelamentoProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia();
					item.add(new Label("banco", nomeFantasia.toUpperCase()));
				}
				item.add(new Label("dias", PeriodoDataUtil
						.diferencaDeDiasEntreData(cancelamento.getRemessaCancelamentoProtesto().getArquivo().getDataEnvio().toDate(), new Date())));
				item.add(downloadArquivoTXT(cancelamento));
			}

			private Link<Remessa> downloadArquivoTXT(final CancelamentoProtesto remessa) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = downloadMediator.baixarCancelamentoTXT(getUsuario(), remessa);
						IResourceStream resourceStream = new FileResourceStream(file);

						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
								remessa.getRemessaCancelamentoProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}
		});
	}

	private void listaDesistenciaPendentes() {
		add(new ListView<DesistenciaProtesto>("listaDesistencias", getDesistenciaPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<DesistenciaProtesto> item) {
				final DesistenciaProtesto dp = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosDesistenciaPage(dp));
					}
				};
				linkArquivo.add(new Label("desistencia", dp.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA) || getUsuario()
						.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					item.add(new Label("banco", municipioMediator.buscaMunicipioPorCodigoIBGE(dp.getCabecalhoCartorio().getCodigoMunicipio())
							.getNomeMunicipio().toUpperCase()));
				} else if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = dp.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio().getNomeFantasia();
					item.add(new Label("banco", nomeFantasia.toUpperCase()));
				}
				item.add(new Label("dias", PeriodoDataUtil
						.diferencaDeDiasEntreData(dp.getRemessaDesistenciaProtesto().getArquivo().getDataEnvio().toDate(), new Date())));
				item.add(downloadArquivoTXT(dp));
			}

			private Link<Remessa> downloadArquivoTXT(final DesistenciaProtesto desistenciaProtesto) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = downloadMediator.baixarDesistenciaTXT(getUsuario(), desistenciaProtesto);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
								desistenciaProtesto.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
					}
				};
			}
		});
	}

	private void listaAutorizacaoCancelamentoPendentes() {
		add(new ListView<AutorizacaoCancelamento>("listaAutorizacao", getAutorizacaoCancelamentoPendentes()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<AutorizacaoCancelamento> item) {
				final AutorizacaoCancelamento ac = item.getModelObject();
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosAutorizacaoCancelamentoPage(ac));
					}
				};
				linkArquivo.add(new Label("desistencia", ac.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);
				if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CRA) || getUsuario()
						.getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.INSTITUICAO_FINANCEIRA)) {
					item.add(new Label("banco", municipioMediator.buscaMunicipioPorCodigoIBGE(ac.getCabecalhoCartorio().getCodigoMunicipio())
							.getNomeMunicipio().toUpperCase()));
				} else if (getUsuario().getInstituicao().getTipoInstituicao().getTipoInstituicao().equals(TipoInstituicaoCRA.CARTORIO)) {
					String nomeFantasia = ac.getRemessaAutorizacaoCancelamento().getArquivo().getInstituicaoEnvio().getNomeFantasia();
					item.add(new Label("banco", nomeFantasia.toUpperCase()));
				}
				item.add(new Label("dias", PeriodoDataUtil
						.diferencaDeDiasEntreData(ac.getRemessaAutorizacaoCancelamento().getArquivo().getDataEnvio().toDate(), new Date())));
				item.add(downloadArquivoTXT(ac));
			}

			private Link<Remessa> downloadArquivoTXT(final AutorizacaoCancelamento ac) {
				return new Link<Remessa>("downloadArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						File file = downloadMediator.baixarAutorizacaoTXT(getUsuario(), ac);
						IResourceStream resourceStream = new FileResourceStream(file);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream,
								ac.getRemessaAutorizacaoCancelamento().getArquivo().getNomeArquivo()));
					}
				};
			}
		});
	}

	private List<DesistenciaProtesto> getDesistenciaPendentes() {
		return arquivo.getRemessaDesistenciaProtesto().getDesistenciaProtesto();
	}

	private List<CancelamentoProtesto> getCancelamentoPendentes() {
		return arquivo.getRemessaCancelamentoProtesto().getCancelamentoProtesto();
	}

	private List<AutorizacaoCancelamento> getAutorizacaoCancelamentoPendentes() {
		return arquivo.getRemessaAutorizacao().getAutorizacaoCancelamento();
	}

	private List<Remessa> getConfirmacoesPendentes() {
		return arquivo.getRemessas();
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitulo() {
		return "CRA - Central de Remessa de Arquivos";
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}