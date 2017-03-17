package br.com.ieptbto.cra.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.component.dataTable.CraLinksPanel;
import br.com.ieptbto.cra.component.label.LabelValorMonetario;
import br.com.ieptbto.cra.entidade.view.ViewTitulo;
import br.com.ieptbto.cra.util.DataUtil;

public class TituloViewColumns {

	public static List<IColumn<ViewTitulo, String>> generateDataTableColumnsFromTituloView() {
		List<IColumn<ViewTitulo, String>> columns = new ArrayList<>();
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("NOSSO N."), "nossoNumero_TituloRemessa") {

			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("TÍTULO"), "numeroTitulo_TituloRemessa") {

			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("PROTOC."), "numeroProtocoloCartorio_Confirmacao") {

			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("MUNICÍPIO"), "nomeMunicipio_Municipio") {

			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewTitulo>> item, String id, IModel<ViewTitulo> model) {
				item.add(new Label(id, model.getObject().getNomeMunicipio_Municipio().toUpperCase()));
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("Nº DEV"), "numeroControleDevedor_TituloRemessa") {

			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("DEVEDOR"), "nomeDevedor_TituloRemessa") {

			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewTitulo>> item, String id, IModel<ViewTitulo> model) {
				String nomeDevedor = model.getObject().getNomeDevedor_TituloRemessa().trim();
				if (nomeDevedor.length() > 25) {
					nomeDevedor = nomeDevedor.substring(0, 24);
				}
				item.add(new CraLinksPanel(id, model.getObject().getId_TituloRemessa(), nomeDevedor));
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("VALOR"), "saldoTitulo_TituloRemessa") {

			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewTitulo>> item, String id, IModel<ViewTitulo> model) {
				item.add(new LabelValorMonetario<BigDecimal>(id, model.getObject().getSaldoTitulo_TituloRemessa()));
			}

			@Override
			public String getCssClass() {
				return "text-right valor";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("REMESSA"), "nomeArquivo_Arquivo_Remessa") {

			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewTitulo>> item, String id, IModel<ViewTitulo> model) {
				item.add(new CraLinksPanel(id, model.getObject().getNomeArquivo_Arquivo_Remessa(),
								model.getObject().getId_Remessa_Remessa()));
			}

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("CONFIR."), "dataRecebimento_Arquivo_Confirmacao") {

			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewTitulo>> item, String id, IModel<ViewTitulo> model) {
				Date dataConfirmacao = model.getObject().getDataRecebimento_Arquivo_Confirmacao();
				String data = "";
				if (dataConfirmacao != null) {
					data = DataUtil.localDateToString(new LocalDate(dataConfirmacao));
				}
				item.add(new Label(id, data));
			}

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("RETORNO"), "nomeArquivo_Arquivo_Retorno") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewTitulo>> item, String id, IModel<ViewTitulo> model) {
				item.add(new CraLinksPanel(id, model.getObject().getNomeArquivo_Arquivo_Retorno(),
								model.getObject().getId_Remessa_Retorno()));
			}

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("DATA OC."), "dataOcorrencia_ConfirmacaoRetorno") {

			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewTitulo>> item, String id, IModel<ViewTitulo> model) {
				LocalDate dataOcorrencia = model.getObject().getDataOcorrencia_ConfirmacaoRetorno();
				String data = "";
				if (dataOcorrencia != null) {
					data = DataUtil.localDateToString(new LocalDate(dataOcorrencia));
				}
				item.add(new Label(id, data));
			}

			@Override
			public String getCssClass() {
				return "text-center";
			}
		});
		columns.add(new PropertyColumn<ViewTitulo, String>(new Model<String>("SITUAÇÃO"), "situacaoTitulo") {

			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<ViewTitulo>> item, String id, IModel<ViewTitulo> model) {
				Label labelSituacao = new Label(id, model.getObject().getSituacaoTitulo());
				labelSituacao.add(new AttributeAppender("class", "info-titulo"));
				item.add(labelSituacao);
			}

			@Override
			public String getCssClass() {
				return "text-center col-info-titulo";
			}
		});
		return columns;
	}
}