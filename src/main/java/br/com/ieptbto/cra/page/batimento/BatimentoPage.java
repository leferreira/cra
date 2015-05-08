package br.com.ieptbto.cra.page.batimento;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.BatimentoMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosDoArquivoPage;
import br.com.ieptbto.cra.security.CraRoles;
import br.com.ieptbto.cra.util.DataUtil;

@SuppressWarnings("rawtypes")
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER})
public class BatimentoPage extends BasePage<Batimento> {

	/***/
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BatimentoPage.class);
	
	@SpringBean
	private BatimentoMediator batimentoMediator;
	private Batimento batimento;
	private Form<Batimento> form;
	private ListView<Remessa> remessas;
	
	public BatimentoPage() {
		super();
		final CheckGroup<Remessa> group = new CheckGroup<Remessa>("group", new ArrayList<Remessa>());
        form = new Form<Batimento>("form"){
            /***/
			private static final long serialVersionUID = 1L;
			@Override
            protected void onSubmit(){
				List<Remessa> retornosParaConfirmar = new ArrayList<Remessa>();
				
				try{
					if (group.getModelObject().isEmpty()){
						error("Ao menos um retorno deve ser selecionado!");
					} else {
						retornosParaConfirmar = (List<Remessa>) group.getModelObject();
						batimentoMediator.confirmarBatimentos(retornosParaConfirmar);
						setResponsePage(new ConfirmarBatimentoPage());
					}
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar o batimento! Entre em contato com a CRA.");
				}
            }
        };
        form.add(new Button("confirmarBotao"));
        form.add(group);
        add(carregarListaRetornos());
        remessas.setReuseItems(true);
        group.add(remessas);
        add(form);
	}
	
	private ListView<Remessa> carregarListaRetornos(){
		return remessas = new ListView<Remessa>("retornos", batimentoMediator.buscarRetornosParaBatimento()){
			/***/
        	private static final long serialVersionUID = 1L;
				@Override
	            protected void populateItem(ListItem<Remessa> item){
					final Remessa retorno = item.getModelObject();
	                item.add(new Check<Remessa>("checkbox", item.getModel()));
	                item.add(new Label("arquivo.dataEnvio", DataUtil.localDateToString(retorno.getArquivo().getDataEnvio())));
					item.add(new Label("instituicaoOrigem.nomeFantasia", retorno.getInstituicaoOrigem().getNomeFantasia()));
					item.add(new Label("valorPagos", batimentoMediator.buscarValorDeTitulosPagos(retorno)));
					Link linkArquivo = new Link("linkArquivo") {
			            /***/
						private static final long serialVersionUID = 1L;

						@Override
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
