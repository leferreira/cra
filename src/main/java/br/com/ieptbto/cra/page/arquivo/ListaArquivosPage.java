package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

public class ListaArquivosPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private RemessaMediator remessaMediator;
	
	private final Arquivo arquivo;
	private List<Remessa> remessas;
	
	public ListaArquivosPage(Arquivo arquivo, Municipio municipio,Instituicao portador, LocalDate dataInicio, LocalDate dataFim, ArrayList<String> tiposArquivo) {
		super();
		this.arquivo = arquivo;
		this.remessas = remessaMediator.buscarArquivos(arquivo, municipio, portador, dataInicio, dataFim, tiposArquivo, getUser());

		add(new ListView<Remessa>("dataTableRemessa", remessas) {
			/***/
			private static final long serialVersionUID = 1L;

			protected void populateItem(ListItem<Remessa> item) {
				Remessa remessa = item.getModelObject();
				item.add(new Label("tipoArquivo", remessa.getArquivo().getTipoArquivo().getTipoArquivo().constante));
				item.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
				item.add(new Label("instituicao", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", remessa.getInstituicaoDestino().getNomeFantasia()));
				item.add(new Label("status", remessa.getArquivo().getStatusArquivo().getStatus()));
				item.add(downloadArquivo(remessa.getArquivo()));
			}
			
			private Component downloadArquivo(final Arquivo file) {
				return new Link<Arquivo>("downloadArquivo") {
					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
					}
				};
			}
		 });
	}
	
	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

}
