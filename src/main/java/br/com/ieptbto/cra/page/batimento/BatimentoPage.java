package br.com.ieptbto.cra.page.batimento;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import br.com.ieptbto.cra.mediator.TituloMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

@AuthorizeInstantiation(value = { CraRoles.ADMIN, CraRoles.SUPER })
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, })
public class BatimentoPage extends BasePage<Batimento> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BatimentoPage.class);
	
	@SpringBean
	private BatimentoMediator batimentoMediator;
	@SpringBean
	private TituloMediator tituloMediator;
	private Batimento batimento;
	private Form<Batimento> form;
	private ListView<Remessa> listView;
	private List<Remessa> listaRetornos;
	
	public BatimentoPage() {
		super();
		form = new Form<Batimento>("form", getModel()) {
			/****/
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				List<Remessa> listaBatimento = new ArrayList<Remessa>();
				try {
					for (Remessa r: listaRetornos){
						if (r.getArquivo().getComentario().equals("true"))
							listaBatimento.add(r);
					}
					if (listaBatimento.isEmpty())
						error("Um retorno deve ser selecionado!");
					else
						setResponsePage(new ConfirmarBatimentoPage(listaBatimento));
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Entre em contato com a CRA ");
				}
			}
		};
		listaRetornos = batimentoMediator.buscarRetornosParaBatimento();
		listaRetornos();
	    listView.setReuseItems(true);
	    form.add(listView);
	    form.add(new Button("button"));
		add(form);
	}
	
	@SuppressWarnings("rawtypes")
	private ListView<Remessa> listaRetornos(){
		return listView = new ListView<Remessa>("listaRetornos", listaRetornos){
			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Remessa> item) {
				final Remessa retorno = item.getModelObject();
				
				item.add(new CheckBox("retorno", new PropertyModel<Boolean>(retorno, "arquivo.comentario")));
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
