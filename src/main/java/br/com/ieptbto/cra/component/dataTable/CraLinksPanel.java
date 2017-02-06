package br.com.ieptbto.cra.component.dataTable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.page.arquivo.TitulosArquivoPage;
import br.com.ieptbto.cra.page.titulo.historico.HistoricoPage;

/**
 * @author Thasso Araújo
 *
 */
public class CraLinksPanel extends Panel {

	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * Link para Histórico do Título passando a PK do título remessa e 
	 * o nome do devedor a ser exibido
	 * @param id
	 * @param idTituloRemessa
	 * @param nomeDevedor
	 */
	public CraLinksPanel(String id, final Integer idTituloRemessa, final String nomeDevedor) {
		super(id);
		Link<Remessa> linkGenerate = new Link<Remessa>("link") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new HistoricoPage(idTituloRemessa));
			}
		};
		linkGenerate.add(new Label("nomeLink", nomeDevedor));
		this.add(linkGenerate);
	}
	
	/**
	 * Link para Histórico do Título passando a entidade título remessa e 
	 * o nome do devedor a ser exibido
	 * @param id
	 * @param tituloRemessa
	 * @param nomeDevedor
	 */
	public CraLinksPanel(String id, final TituloRemessa tituloRemessa, final String nomeDevedor) {
		super(id);
		Link<Remessa> linkGenerate = new Link<Remessa>("link") {	

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new HistoricoPage(tituloRemessa));
			}
		};
		linkGenerate.add(new Label("nomeLink", nomeDevedor));
		this.add(linkGenerate);
	}

	/**
	 * Link para Titulos do Arquivo passando o id da Remessa e 
	 * o nome do arquivo a ser exibido
	 * @param id
	 * @param nomeArquivo
	 * @param idRemessa
	 */
	public CraLinksPanel(String id, final String nomeArquivo, final Integer idRemessa) {
		super(id);
		Link<Remessa> linkGenerate = new Link<Remessa>("link") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new TitulosArquivoPage(idRemessa));
			}
		};
		linkGenerate.add(new Label("nomeLink", nomeArquivo));
		this.add(linkGenerate);
	}
	
	/**
	 * Link para Titulos do Arquivo da entidade Remessa e 
	 * o nome do arquivo a ser exibido
	 * @param id
	 * @param nomeArquivo
	 * @param remessa
	 */
	public CraLinksPanel(String id, final String nomeArquivo, final Remessa remessa) {
		super(id);
		Link<Remessa> linkGenerate = new Link<Remessa>("link") {

			/***/
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(new TitulosArquivoPage(remessa));
			}
		};
		linkGenerate.add(new Label("nomeLink", nomeArquivo));
		this.add(linkGenerate);
	}
}
