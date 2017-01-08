package br.com.ieptbto.cra.page.titulo;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.bean.TituloFormBean;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
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
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaTitulosPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private TituloMediator tituloMediator;

	private TituloFormBean tituloBean;
	private Usuario usuario;

	public ListaTitulosPage(TituloFormBean tituloBean) {
		this.tituloBean = tituloBean;
		this.usuario = getUser();

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(listaTitulos());
	}

	private ListView<TituloRemessa> listaTitulos() {
		return new ListView<TituloRemessa>("listViewTitulos", buscarTitulos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloRemessa = item.getModelObject();

				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", tituloRemessa.getSaldoTitulo()));
				item.add(new Label("nossoNumero", tituloRemessa.getNossoNumero()));

				String municipio = tituloRemessa.getRemessa().getInstituicaoDestino().getMunicipio().getNomeMunicipio();
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
	}

	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				TituloFormBean bean = getTituloBean();

				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				if (bean.getDataInicio() != null) {
					dataInicio = new LocalDate(bean.getDataInicio());
				}
				if (bean.getDataFim() != null) {
					dataFim = new LocalDate(bean.getDataFim());
				}
				return tituloMediator.buscarTitulos(usuario, dataInicio, dataFim, bean.getTipoInstituicao(), bean.getBancoConvenio(), bean.getCartorio(), bean);
			}
		};
	}

	public TituloFormBean getTituloBean() {
		return tituloBean;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return null;
	}
}
