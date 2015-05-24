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
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.util.DataUtil;


/**
 * @author Thasso Araújo
 *
 */
@AuthorizeInstantiation(value = "USER")
public class ArquivosInstituicaoPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	RemessaMediator remessasMediator;

	private Instituicao instituicao;
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private TextField<LocalDate> dataEnvioInicio;
	private TextField<LocalDate> dataEnvioFinal;
	private ArrayList<String> tiposSelect = new ArrayList<String>();
	private ArrayList<String> statusSelect = new ArrayList<String>();

	public ArquivosInstituicaoPanel(String id, IModel<?> model, Instituicao instituicao) {
		super(id, model);
		this.instituicao = instituicao;
		add(new Button("botaoEnviar"){
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				try {
					if (dataEnvioInicio.getDefaultModelObject() != null){
						if (dataEnvioFinal.getDefaultModelObject() != null){
							dataInicio = DataUtil.stringToLocalDate(dataEnvioInicio.getDefaultModelObject().toString());
							dataFim = DataUtil.stringToLocalDate(dataEnvioFinal.getDefaultModelObject().toString());
							if (!dataInicio.isBefore(dataFim))
								if (!dataInicio.isEqual(dataFim))
									throw new InfraException("A data de início deve ser antes da data fim.");
						}else
							throw new InfraException("As duas datas devem ser preenchidas.");
					} 
				} catch (Exception e) {
					error("Não foi possível realizar a busca ! \n Entre em contato com a CRA ");
				}
			}
		});
		add(listViewArquivos());
		add(dataEnvioInicio());
		add(dataEnvioFinal());
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
				item.add(new Label("valor", "R$ " + remessa.getArquivo().getRemessas().get(0).getRodape().getSomatorioValorRemessa()));
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
				return remessasMediator.buscarRemessaSimples(instituicao, tiposSelect, statusSelect,dataInicio, dataFim);
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
		List<String> choices = new ArrayList<String>(Arrays.asList(new String[] {"Enviado","Recebido","Aguardando" }));
		return new CheckBoxMultipleChoice<String>("statusArquivos",	new Model<ArrayList<String>>(statusSelect), choices);
	}
	
	private TextField<LocalDate> dataEnvioInicio() {
		dataEnvioInicio = new TextField<LocalDate>("dataEnvioInicio", new Model<LocalDate>());
		dataEnvioInicio.setRequired(true);
		dataEnvioInicio.setLabel(new Model<String>("intervalo da data do envio"));
		return dataEnvioInicio;
	}
	
	private TextField<LocalDate> dataEnvioFinal() {
		return dataEnvioFinal = new TextField<LocalDate>("dataEnvioFinal", new Model<LocalDate>());
	}
}
