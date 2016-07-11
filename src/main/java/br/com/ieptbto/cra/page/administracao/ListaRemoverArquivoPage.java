package br.com.ieptbto.cra.page.administracao;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.AdministracaoMediator;
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
	AdministracaoMediator administracaoMediator;

	private List<Arquivo> arquivos;

	public ListaRemoverArquivoPage(List<Arquivo> arquivos) {
		this.arquivos = arquivos;
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
				WebMarkupContainer divInfo = new WebMarkupContainer("divInfo");
				divInfo.add(new AttributeAppender("id", arquivo.getStatusArquivo().getSituacaoArquivo().getLabel()));
				divInfo.add(new Label("status", arquivo.getStatusArquivo().getSituacaoArquivo().getLabel().toUpperCase()));
				item.add(divInfo);
				item.add(remover(arquivo));
			}

			private Link<Arquivo> remover(final Arquivo arquivo) {
				return new Link<Arquivo>("removerArquivo") {

					/***/
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {

						try {
							administracaoMediator.removerArquivo(arquivo);
							arquivos.remove(arquivo);
							ListaRemoverArquivoPage.this.success("O arquivo " + arquivo.getNomeArquivo() + " enviado por "
									+ arquivo.getInstituicaoEnvio().getNomeFantasia() + " foi removido com sucesso!");

						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							error("Não foi possível remover o arquivo! ");
						}
					}
				};
			}
		});
	}

	public List<Arquivo> getArquivos() {
		if (arquivos == null) {
			return new ArrayList<Arquivo>();
		}
		return arquivos;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return null;
	}
}
