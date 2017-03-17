package br.com.ieptbto.cra.component;

import java.nio.charset.StandardCharsets;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.CSVDataExporter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.ExportToolbar;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Thasso Araújo
 *
 */
public class CustomExportToolbar extends ExportToolbar {

	/***/
	private static final long serialVersionUID = 1L;
	private static final String EXPORTER_MESSAGE_MODEL = "Exportar:";

	public CustomExportToolbar(DataTable<?, ?> table, IModel<String> fileNameModel) {
		super(table, fileNameModel);
		setFileNameModel(fileNameModel);
		setMessageModel(getMessageModel());
		addDataExporter(getCSVDataExporter());
	}

	private CSVDataExporter getCSVDataExporter() {
		CSVDataExporter csvDataExporter = new CSVDataExporter();
		csvDataExporter.setCharacterSet(StandardCharsets.ISO_8859_1.name());
		return csvDataExporter;
	}

	@Override
	public IModel<String> getMessageModel() {
		return new Model<String>(EXPORTER_MESSAGE_MODEL);
	}
}