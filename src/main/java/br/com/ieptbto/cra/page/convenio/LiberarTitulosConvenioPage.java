package br.com.ieptbto.cra.page.convenio;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConvenioMediator;
import br.com.ieptbto.cra.mediator.TituloFiliadoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class LiberarTitulosConvenioPage extends BasePage<TituloFiliado>{

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LiberarTitulosConvenioPage.class);

	private TituloFiliado titulo;
	private List<TituloFiliado> listaTitulosConvenios;
	
	@SpringBean
	ConvenioMediator convenioMediator;
	@SpringBean
	TituloFiliadoMediator tituloFiliadoMediator;
	
	public LiberarTitulosConvenioPage() {
		this.titulo = new TituloFiliado();
		
		Form<TituloFiliado> form = new Form<TituloFiliado>("form", getModel()){
			/***/
			private static final long serialVersionUID = 1L;
			@Override
            protected void onSubmit(){
				try {
				
					convenioMediator.gerarRemessas(getUser() ,getListaTitulosConvenios());
					info("Remessas processados e encaminhadas com sucesso na CRA !");
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível enviar o arquivo ! \n Entre em contato com a CRA ");
				}
			}
		};
		add(form);
		setListaTitulosConvenios(convenioMediator.buscarTitulosConvenios());
		add(carregarListaTitulos());
		
	}
	
	private ListView<TituloFiliado> carregarListaTitulos() {
		return new ListView<TituloFiliado>("listViewTitulos", getListaTitulosConvenios()) {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<TituloFiliado> item) {
				final TituloFiliado tituloLista = item.getModelObject();
				
				item.add(new Label("numeroTitulo", tituloLista.getNumeroTitulo()));
				item.add(new Label("pracaProtesto", tituloLista.getPracaProtesto().getNomeMunicipio()));
				item.add(new Label("convenio", tituloLista.getFiliado().getInstituicaoConvenio().getRazaoSocial()));
				item.add(new Label("credor", tituloLista.getFiliado().getRazaoSocial()));
				item.add(new Label("devedor", tituloLista.getNomeDevedor()));
				item.add(new Label("dataEmissao", DataUtil.localDateToString(tituloLista.getDataEmissao())));
				item.add(new Label("dataVencimento", DataUtil.localDateToString(tituloLista.getDataVencimento())));
				item.add(new LabelValorMonetario<String>("valor", tituloLista.getValorTitulo()));
			}
		};
	}

	@Override
	protected IModel<TituloFiliado> getModel() {
		return new CompoundPropertyModel<TituloFiliado>(titulo);
	}

	public List<TituloFiliado> getListaTitulosConvenios() {
		return listaTitulosConvenios;
	}

	public void setListaTitulosConvenios(List<TituloFiliado> listaTitulosConvenios) {
		this.listaTitulosConvenios = listaTitulosConvenios;
	}

}
