package br.com.ieptbto.cra.component.label;

import java.math.BigDecimal;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;

/**
 * Label para valor monetário.
 * 
 */
@SuppressWarnings({ "unchecked", "hiding", "serial" })
public class LabelValorMonetario<C> extends Label {

	private static final String MASCARA_VALOR_MONETARIO = "R$ #,##0.00";
	private static final int QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_INTEIRA = 1;
	private static final int QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_DECIMAL = 2;

	/**
	 * @param id
	 * @param model
	 */
	public LabelValorMonetario(String id, IModel<?> model) {
		super(id, model);
	}

	/**
	 * @param id
	 * @param BigDecimal
	 */
	public LabelValorMonetario(String id, BigDecimal valor) {
		super(id, Model.of(valor));
	}

	/**
	 * @param id
	 */
	public LabelValorMonetario(String id) {
		super(id);
	}

	@Override
	public <C> IConverter<C> getConverter(Class<C> type) {
		return new BigDecimalConverter(MASCARA_VALOR_MONETARIO, QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_DECIMAL,
		        QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_INTEIRA, QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_DECIMAL);
	}

}
