package br.com.ieptbto.cra.page.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.RelatorioMediator;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
@SuppressWarnings("serial")
public class ListaCancelamentoDevolvidoPage extends BasePage<Arquivo> {

//	@SpringBean
//	RemessaDesistenciaMediator remessaDesistenciaMediator;
	@SpringBean
	RelatorioMediator relatorioMediator;
	private Arquivo arquivo;
	private List<RemessaDesistenciaProtesto> remessasCancelamentoDevolucao;

	public ListaCancelamentoDevolvidoPage(Arquivo arquivo, Municipio municipio, LocalDate dataInicio, LocalDate dataFim, ArrayList<TipoArquivoEnum> tiposArquivo) {
		this.arquivo = arquivo;
		this.remessasCancelamentoDevolucao = new ArrayList<RemessaDesistenciaProtesto>();
//		this.remessasCancelamentoDevolucao = remessaDesistenciaMediator.buscarRemessasCancelamentoDevolvido(arquivo, municipio,dataInicio, dataFim, tiposArquivo, getUser());
		add(carregarListaArquivos());
	}

	private ListView<RemessaDesistenciaProtesto> carregarListaArquivos() {
		return new ListView<RemessaDesistenciaProtesto>("dataTableRemessa", getRemessasCancelamentoDevolucao()) {

			@Override
			protected void populateItem(ListItem<RemessaDesistenciaProtesto> item) {
				final RemessaDesistenciaProtesto remessa = item.getModelObject();
				item.add(new Label("tipoArquivo", remessa.getArquivo().getTipoArquivo().getTipoArquivo().constante));
				item.add(new Label("nomeArquivo", remessa.getArquivo().getNomeArquivo()));
				item.add(new Label("dataEnvio", DataUtil.localDateToString(remessa.getArquivo().getDataEnvio())));
				item.add(new Label("instituicao", remessa.getArquivo().getInstituicaoEnvio().getNomeFantasia()));
				item.add(new Label("destino", StringUtils.EMPTY));
				item.add(new Label("valor", remessa.getRodape().getSomaTotalCancelamentoDesistencia()));
				item.add(downloadArquivoTXT(remessa));
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
	
	public List<RemessaDesistenciaProtesto> getRemessasCancelamentoDevolucao() {
		return remessasCancelamentoDevolucao;
	}

	@Override
	protected IModel<Arquivo> getModel() {
		return new CompoundPropertyModel<Arquivo>(arquivo);
	}
}
