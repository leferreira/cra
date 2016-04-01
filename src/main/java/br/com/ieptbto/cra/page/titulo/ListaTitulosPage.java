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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
@AuthorizeInstantiation(value = "USER")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class ListaTitulosPage extends BasePage<TituloRemessa> {

	@SpringBean
	TituloMediator tituloMediator;

	private TituloRemessa tituloRemessa;
	private List<TituloRemessa> titulos;
	private Municipio municipio;

	public ListaTitulosPage(LocalDate dataInicio, LocalDate dataFim, TituloRemessa titulo, Municipio pracaProtesto) {
		this.tituloRemessa = titulo;
		this.municipio = pracaProtesto;
		this.titulos = tituloMediator.buscarListaTitulos(dataInicio, dataFim, titulo, pracaProtesto, getUser());

		add(carregarListaTitulos());
	}

	private ListView<TituloRemessa> carregarListaTitulos() {
		return new ListView<TituloRemessa>("listViewTitulos", getTitulos()) {

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();

				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					public void onClick() {
						setResponsePage(new TitulosArquivoPage(tituloLista.getRemessa()));
					}
				};
				linkArquivo.add(new Label("nomeRemessa", tituloLista.getRemessa().getArquivo().getNomeArquivo()));
				item.add(linkArquivo);

				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));

				String municipio = tituloLista.getRemessa().getInstituicaoDestino().getMunicipio().getNomeMunicipio();
				if (municipio.length() > 20) {
					municipio = municipio.substring(0, 19);
				}
				item.add(new Label("municipio", municipio.toUpperCase()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("dataConfirmacao", DataUtil.localDateToString(tituloLista.getConfirmacao().getRemessa().getArquivo().getDataEnvio())));
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				} else {
					item.add(new Label("dataConfirmacao", StringUtils.EMPTY));
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(new LabelValorMonetario<BigDecimal>("valorTitulo", tituloLista.getValorTitulo()));
				Link<TituloRemessa> linkHistorico = new Link<TituloRemessa>("linkHistorico") {

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
					}
				};
				if (tituloLista.getNomeDevedor().length() > 25) {
					linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor().substring(0, 24)));
				} else {
					linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
				}
				item.add(linkHistorico);
				Link<Retorno> linkRetorno = new Link<Retorno>("linkRetorno") {

					public void onClick() {
						setResponsePage(new TitulosArquivoPage(tituloLista.getRetorno().getRemessa()));
					}
				};
				if (tituloLista.getRetorno() != null) {
					linkRetorno.add(new Label("retorno", tituloLista.getRetorno().getRemessa().getArquivo().getNomeArquivo()));
					item.add(linkRetorno);
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getRetorno().getDataOcorrencia())));
				} else {
					linkRetorno.add(new Label("retorno", StringUtils.EMPTY));
					item.add(linkRetorno);
					item.add(new Label("dataSituacao", DataUtil.localDateToString(tituloLista.getDataOcorrencia())));
				}
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}

	public TituloRemessa getTituloRemessa() {
		return tituloRemessa;
	}

	@Override
	protected IModel<TituloRemessa> getModel() {
		return new CompoundPropertyModel<TituloRemessa>(tituloRemessa);
	}

	public List<TituloRemessa> getTitulos() {
		return titulos;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setTitulos(List<TituloRemessa> titulos) {
		this.titulos = titulos;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

}
