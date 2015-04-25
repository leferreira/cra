package br.com.ieptbto.cra.page.batimento;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.util.DataUtil;

public class ConfirmarBatimentoPage extends BasePage<Batimento> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BatimentoPage.class);
	
	@SpringBean
	private BatimentoMediator batimentoMediator;
	private Batimento batimento;
	private List<Remessa> retornosParaConfirmar;
	private Form<Batimento> form;
	private ListView<Remessa> listView;

	public ConfirmarBatimentoPage(List<Remessa> retornos){
		this.batimento = new Batimento();
		this.retornosParaConfirmar = retornos;
		form = new Form<Batimento>("form", getModel()) {
			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				try {
					batimentoMediator.realizarBatimento(retornosParaConfirmar);
					info("Batimento realizado com sucesso!");
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Entre em contato com a CRA ");
				}
			}
		};
		listaRetornos();
	    listView.setReuseItems(true);
	    form.add(listView);
	    form.add(new Button("button"));
		add(form);
	}
	
	@SuppressWarnings("rawtypes")
	private ListView<Remessa> listaRetornos(){
		return listView = new ListView<Remessa>("listaRetornos", retornosParaConfirmar){
			/***/
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa retorno = item.getModelObject();
				
				item.add(new CheckBox("retorno", new PropertyModel(retorno, "arquivo.nomeArquivo")));
				item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
				item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
				item.add(new Label("valorPagos", StringUtils.EMPTY));
				Link linkArquivo = new Link("linkArquivo") {
		            /***/
					private static final long serialVersionUID = 1L;

					public void onClick() {
		            	setResponsePage(new TitulosDoArquivoPage(retorno));  
		            }
		        };
		        linkArquivo.add(new Label("arquivo.nomeArquivo", retorno.getArquivo().getNomeArquivo()));
		        item.add(linkArquivo);
			}
		};
	}
	
	@Override
	protected IModel<Batimento> getModel() {
		return new CompoundPropertyModel<Batimento>(batimento);
	}

}
