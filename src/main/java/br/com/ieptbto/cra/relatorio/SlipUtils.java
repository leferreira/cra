package br.com.ieptbto.cra.relatorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * @author Thasso Araújo
 *
 */
public class SlipUtils {

	private Map<String, Object> parametros = new HashMap<String, Object>();

	public JasperPrint gerarSlipLista(List<InstrumentoProtesto> listaSlip) throws JRException {
		parametros.put("DATA", DataUtil.localDateToString(new LocalDate()));
		
		JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("teste.jrxml"));
		return JasperFillManager.fillReport(jasperReport, parametros);
	}
}
