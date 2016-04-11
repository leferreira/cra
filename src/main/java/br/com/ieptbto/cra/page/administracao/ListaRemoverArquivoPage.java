package br.com.ieptbto.cra.page.administracao;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.AdministracaoMediator;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoInstituicaoPage;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class ListaRemoverArquivoPage extends BasePage<Arquivo> {

	/***/
	private static final long serialVersionUID = 1L;

	@SpringBean
	ArquivoMediator arquivoMediator;
	@SpringBean
	AdministracaoMediator administracaoMediator;

	private Arquivo arquivo;
	private List<Arquivo> arquivos;

	public ListaRemoverArquivoPage(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
			ArrayList<TipoArquivoEnum> tiposArquivo) {
		this.arquivo = arquivo;
		this.arquivos = new ArrayList<Arquivo>();
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		listaArquivosRemover();
	}

	private void listaArquivosRemover() {
		add(new ListView<Arquivo>("dataTableArquivo", getArquivos()) {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Arquivo> item) {
				final Arquivo arquivo = item.getModelObject();
				item.add(new Label("tipoArquivo", arquivo.getTipoArquivo().getTipoArquivo().constante));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						setResponsePage(new TitulosArquivoInstituicaoPage(arquivo));
					}
				};
				linkArquivo.add(new Label("nomeArquivo", arquivo.getNomeArquivo()));
				item.add(linkArquivo);
				item.add(new Label("dataEnvio", DataUtil.localDateToString(arquivo.getDataEnvio())));
				item.add(new Label("instituicao", arquivo.getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", arquivo.getInstituicaoRecebe().getNomeFantasia()));
				item.add(new Label("status", arquivo.getStatusArquivo().getSituacaoArquivo().getLabel().toUpperCase()).setMarkupId(arquivo.getStatusArquivo().getSituacaoArquivo().getLabel()));
				item.add(remover(arquivo));
			}

			private Link<Arquivo> remover(final Arquivo arquivo) {
				return new Link<Arquivo>("removerArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							administracaoMediator.removerArquivo();

						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							error("Não foi possível remover o arquivo !");
						}
					}
				};
			}
		});
	}

	public List<Arquivo> getArquivos() {
		return arquivos;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
