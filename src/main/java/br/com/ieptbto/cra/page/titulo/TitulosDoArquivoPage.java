package br.com.ieptbto.cra.page.titulo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.mediator.InstituicaoMediator;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;
@SuppressWarnings("rawtypes")
public class TitulosDoArquivoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	TituloMediator tituloMediator;
	@SpringBean
	InstituicaoMediator instituicaoMediator;
	@SpringBean
	RemessaMediator remessaMediator;

	private Remessa remessa;
	private List<TituloRemessa> titulos;
	
	public TitulosDoArquivoPage(Remessa remessa) {
		this.titulos = tituloMediator.buscarTitulosPorRemessa(remessa, getUser().getInstituicao());
		this.remessa = remessa;
		carregarInformacoes();
		add(carregarListaTitulos());
	}

	public TitulosDoArquivoPage(Arquivo arquivo) {
//		this.remessa = remessaMediator.buscarRemessa(arquivo.getRemessas().get(0));
//		this.titulos = (List<TituloRemessa>)remessa.getTitulos();
		carregarInformacoes();
		add(carregarListaTitulos());
	}
	
	private ListView<TituloRemessa> carregarListaTitulos() {
		return new ListView<TituloRemessa>("listViewTituloArquivo", getTitulos()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloRemessa> item) {
				final TituloRemessa tituloLista = item.getModelObject();
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("nossoNumero", tituloLista.getNossoNumero()));
				if (tituloLista.getConfirmacao() != null) {
					item.add(new Label("protocolo", tituloLista.getConfirmacao().getNumeroProtocoloCartorio()));
				} else { 
					item.add(new Label("protocolo", StringUtils.EMPTY));
				}
				item.add(new Label("portador", tituloLista.getRemessa().getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				
				Link linkHistorico = new Link("linkHistorico") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
						setResponsePage(new HistoricoPage(tituloLista));
		            }
		        };
		        linkHistorico.add(new Label("nomeDevedor", tituloLista.getNomeDevedor()));
		        item.add(linkHistorico);
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto()));
				item.add(new Label("situacaoTitulo", tituloLista.getSituacaoTitulo()));
			}
		};
	}
	
	private void carregarInformacoes(){
		add(nomeArquivo());
		add(portador());
		add(dataEnvio());
		add(usuarioEnvio());
		add(tipoArquivo());
	}
	
	public TextField<String> nomeArquivo(){
		return new TextField<String>("nomeArquivo", new Model<String>(remessa.getArquivo().getNomeArquivo()));
	}
	
	public TextField<String> portador(){
		return new TextField<String>("nomePortador", new Model<String>(remessa.getCabecalho().getNomePortador()));
	}
	
	public TextField<String> dataEnvio(){
		return new TextField<String>("dataEnvio", new Model<String>(DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
	}
	
	public TextField<String> usuarioEnvio(){
		return new TextField<String>("usuarioEnvio", new Model<String>(remessa.getArquivo().getUsuarioEnvio().getNome()));
	}
	
	public TextField<String> tipoArquivo(){
		return new TextField<String>("tipo", new Model<String>(remessa.getArquivo().getTipoArquivo().getTipoArquivo().getLabel()));
	}
	
	
	public List<TituloRemessa> getTitulos() {
		return titulos;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(remessa.getArquivo());
	}
}
