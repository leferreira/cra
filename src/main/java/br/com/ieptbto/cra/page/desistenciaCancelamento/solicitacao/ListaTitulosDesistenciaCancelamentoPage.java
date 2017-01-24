package br.com.ieptbto.cra.page.desistenciaCancelamento.solicitacao;

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

import br.com.ieptbto.cra.beans.TituloBean;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.CancelamentoProtestoMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
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
public class ListaTitulosDesistenciaCancelamentoPage extends BasePage<TituloRemessa> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	private CancelamentoProtestoMediator cancelamentoProtestoMediator;
	@SpringBean
	private TituloMediator tituloMediator;

	private TituloBean tituloBean;
	private Usuario usuario;

	public ListaTitulosDesistenciaCancelamentoPage(TituloBean tituloBean) {
		this.tituloBean = tituloBean;
		this.usuario = getUser();

		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(listaTitulosCancelamento());
	}

	private ListView<TituloRemessa> listaTitulosCancelamento() {
		return new ListView<TituloRemessa>("listViewTitulos", buscarTitulos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa titulo = item.getModelObject();

				String protocolo = "";
				if (titulo.getConfirmacao() != null) {
					if (!StringUtils.isBlank(titulo.getConfirmacao().getNumeroProtocoloCartorio())) {
						protocolo = titulo.getConfirmacao().getNumeroProtocoloCartorio();
					}
				}
				item.add(new Label("protocolo", protocolo));
				item.add(new Label("numeroTitulo", titulo.getNumeroTitulo()));

				String municipio = titulo.getRemessa().getInstituicaoDestino().getMunicipio().getNomeMunicipio();
				if (municipio.length() > 20) {
					municipio = municipio.substring(0, 19);
				}
				item.add(new Label("municipio", municipio.toUpperCase()));
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", titulo.getValorTitulo()));
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					/***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(titulo));
					}
				};
				if (titulo.getNomeDevedor().length() > 25) {
					linkHistorico.add(new Label("nomeDevedor", titulo.getNomeDevedor().substring(0, 24)));
				} else {
					linkHistorico.add(new Label("nomeDevedor", titulo.getNomeDevedor()));
				}
				item.add(linkHistorico);

				if (titulo.getRetorno() != null) {
					item.add(new Label("dataSituacao", DataUtil.localDateToString(titulo.getRetorno().getDataOcorrencia())));
				} else {
					item.add(new Label("dataSituacao", DataUtil.localDateToString(titulo.getDataOcorrencia())));
				}

				String situacaoTitulo = titulo.getSituacaoTitulo();
				item.add(new Label("situacaoTitulo", situacaoTitulo));
				Link<TituloRemessa> linkSolicitarCancelamento = new Link<TituloRemessa>("linkSolicitarCancelamento") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new EnviarSolicitacaoDesistenciaCancelamentoPage(titulo));
					}
				};
				linkSolicitarCancelamento.setVisible(false);
				if (situacaoTitulo.equals("ABERTO") || situacaoTitulo.equals("PROTESTADO")) {
					linkSolicitarCancelamento.setVisible(true);
				}
				item.add(linkSolicitarCancelamento);
			}
		};

	}

	public IModel<List<TituloRemessa>> buscarTitulos() {
		return new LoadableDetachableModel<List<TituloRemessa>>() {

			/**/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TituloRemessa> load() {
				TituloBean bean = tituloBean;
				LocalDate dataInicio = null;
				LocalDate dataFim = null;

				if (bean.getDataInicio() != null) {
					dataInicio = new LocalDate(bean.getDataInicio());
				}
				if (bean.getDataFim() != null) {
					dataFim = new LocalDate(bean.getDataFim());
				}
				return tituloMediator.consultarTitulos(usuario, dataInicio, dataFim, bean.getTipoInstituicao(), bean.getBancoConvenio(),
						bean.getCartorio(), bean);
			}
		};
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return null;
	}
}
