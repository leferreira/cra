package br.com.ieptbto.cra.page.admin;

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
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.AdministracaoMediator;
import br.com.ieptbto.cra.mediator.ArquivoMediator;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.titulo.TitulosArquivoInstituicaoPage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class ListaArquivosRemoverPage extends BasePage<Arquivo> {

	@SpringBean
	ArquivoMediator arquivoMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	@SpringBean
	AdministracaoMediator administracaoMediator;
	private Arquivo arquivo;
	private List<Arquivo> arquivos;

	public ListaArquivosRemoverPage(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim, ArrayList<TipoArquivoEnum> tiposArquivo) {
		this.arquivo = arquivo;
		ArrayList<SituacaoArquivo> situacaoArquivos = new ArrayList<SituacaoArquivo>();
		this.arquivos = arquivoMediator.buscarArquivosAvancado(arquivo, getUser(), tiposArquivo, municipio, dataInicio, dataFim, situacaoArquivos);
		add(carregarListaArquivos());
	}

	private ListView<Arquivo> carregarListaArquivos() {
		return new ListView<Arquivo>("dataTableArquivo", getArquivos()) {

			@Override
			protected void populateItem(ListItem<Arquivo> item) {
				final Arquivo arquivo = item.getModelObject();
				item.add(new Label("tipoArquivo", arquivo.getTipoArquivo().getTipoArquivo().constante));
				Link<Arquivo> linkArquivo = new Link<Arquivo>("linkArquivo") {

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

					@Override
					public void onClick() {
						try {
							administracaoMediator.removerArquivo(arquivo, getUser().getInstituicao()).getArquivo();
							info("O arquivo "+ arquivo.getNomeArquivo() +" foi removido com sucesso !");
						} catch (InfraException ex) {
							error(ex.getMessage());
						} catch (Exception e) {
							error("Não foi possível remover o arquivo !");
						}
					}
				};
			}		
		};
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}

	public List<Arquivo> getArquivos() {
		return arquivos;
	}
}
