package br.com.ieptbto.cra.page.arquivo;

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
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class ListaCancelamentoDevolvidoPage extends BasePage<Arquivo> {

	// @SpringBean
	// RemessaDesistenciaMediator remessaDesistenciaMediator;
	@SpringBean
	RemessaMediator remessaMediator;
	private Arquivo arquivo;
	private List<DesistenciaProtesto> desistenciaProtesto;

	public ListaCancelamentoDevolvidoPage(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim,
	        ArrayList<TipoArquivoEnum> tiposArquivo) {
		this.arquivo = arquivo;
		add(carregarListaArquivos());
	}

	public ListaCancelamentoDevolvidoPage(Arquivo arquivo, LocalDate dataInicio, LocalDate dataFim,
	        ArrayList<TipoArquivoEnum> tiposArquivo, Instituicao portador, Usuario usuario) {
		this.desistenciaProtesto = remessaMediator.buscarRemessaDesistenciaProtesto(arquivo, dataInicio, dataFim, tiposArquivo, portador,
		        usuario);
		add(carregarListaArquivos());
	}

	private ListView<DesistenciaProtesto> carregarListaArquivos() {
		return new ListView<DesistenciaProtesto>("dataTableRemessa", getRemessasCancelamentoDevolucao()) {

			@Override
			protected void populateItem(ListItem<DesistenciaProtesto> item) {
				final DesistenciaProtesto desistencia = item.getModelObject();
				item.add(new Label("tipoArquivo", desistencia.getRemessaDesistenciaProtesto().getArquivo().getTipoArquivo()
				        .getTipoArquivo().constante));
				item.add(new Label("nomeArquivo", desistencia.getRemessaDesistenciaProtesto().getArquivo().getNomeArquivo()));
				item.add(new Label("dataEnvio", DataUtil.localDateToString(desistencia.getRemessaDesistenciaProtesto().getArquivo()
				        .getDataEnvio())));
				item.add(new Label("instituicao", desistencia.getRemessaDesistenciaProtesto().getArquivo().getInstituicaoEnvio()
				        .getNomeFantasia()));
				item.add(new Label("valor", desistencia.getRemessaDesistenciaProtesto().getRodape().getSomatorioValorTitulo()));
				item.add(downloadArquivoTXT(desistencia.getRemessaDesistenciaProtesto()));
			}

			private Link<Remessa> downloadArquivoTXT(final RemessaDesistenciaProtesto remessa) {
				return new Link<Remessa>("downloadArquivo") {

					@Override
					public void onClick() {
					}
				};
			}
		};
	}

	public List<DesistenciaProtesto> getRemessasCancelamentoDevolucao() {
		return desistenciaProtesto;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
