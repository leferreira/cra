package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.util.DataUtil;


/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
public class ArquivosBancoPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessasMediator;

	private Instituicao instituicao;
	private LocalDate dataBusca = new LocalDate();
	private TextField<String> dataEnvio;
	private ArrayList<String> tiposSelect = new ArrayList<String>();
	private ArrayList<String> statusSelect = new ArrayList<String>();

	public ArquivosBancoPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao = instituicao;
		add(new Button("botaoEnviar"){
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				try {
					if (dataEnvio.getDefaultModelObject() != null)
						dataBusca = DataUtil.stringToLocalDate(dataEnvio.getDefaultModelObject().toString());
				} catch (Exception e) {
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		});
		add(listViewArquivos());
		add(campoDataEnvio());
		add(comboTipoArquivos());
		add(comboStatus());
	}

	@SuppressWarnings("rawtypes")
	private ListView<Remessa> listViewArquivos(){
		return new ListView<Remessa>("listView", buscarRemessas()) {
			/** */
			private static final long serialVersionUID = -3365063971696545653L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa remessa = item.getModelObject();
				item.add(new Label("tipoArquivo", remessa.getArquivo().getTipoArquivo().getTipoArquivo().constante));
				
				Link linkArquivo = new Link("linkArquivo") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
		            	setResponsePage(new TitulosDoArquivoPage(remessa));  
		            }
		        };
		        linkArquivo.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
		        item.add(linkArquivo);
		        
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
				item.add(new Label("instituicao", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
				item.add(new Label("status", remessa.getArquivo().getStatusArquivo().getStatus()));
				item.add(downloadArquivo(remessa.getArquivo()));
			}
			
			@SuppressWarnings("serial")
			private Component downloadArquivo(final Arquivo file) {
				return new Link<Arquivo>("downloadArquivo") {
					
					@Override
					public void onClick() {
					}
				};
			}
		};
	}

	private IModel<List<Remessa>> buscarRemessas() {
		return new LoadableDetachableModel<List<Remessa>>() {
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Remessa> load() {
				return remessasMediator.buscarArquivos(instituicao, tiposSelect, statusSelect,dataBusca);
			}
		};
	}
	
	private Component comboTipoArquivos() {
		List<String> choices = new ArrayList<String>();
		List<TipoArquivoEnum> enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choices.add(tipo.constante);
		}
		return new CheckBoxMultipleChoice<String>("tipoArquivos",new Model<ArrayList<String>>(tiposSelect), choices);
	}

	private Component comboStatus() {
		List<String> choices = new ArrayList<String>(Arrays.asList(new String[] {"Enviados","Recebidos" }));
		return new CheckBoxMultipleChoice<String>("statusArquivos",	new Model<ArrayList<String>>(statusSelect), choices);
	}
	
	private TextField<String> campoDataEnvio() {
		return dataEnvio = new TextField<String>("dataEnvio", new Model<String>(DataUtil.localDateToString(new LocalDate())));
	}
}
