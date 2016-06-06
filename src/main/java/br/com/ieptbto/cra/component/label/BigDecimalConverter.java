package br.com.ieptbto.cra.component.label;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

@SuppressWarnings({ "serial", "rawtypes" })
public class BigDecimalConverter implements IConverter {

	private static final int QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_INTEIRA = 1;
	private static final int QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_DECIMAL = 2;

	private static final String PADRAO_FORMATACAO_BIGDECIMAL = "#,##0.00##";

	private static final String MSG_ENTRADA_ILEGAL = "Conversor de BigDecimal não pode ser chamado com valor null.";

	/** Converter. */
	protected DecimalFormat decimalFormatter;

	private int qtCasasDecimaisParaArredondamento;
	private ValorUtil valorUtil;

	/**
	 * Cria uma nova instância do tipo {@link BigDecimalConverter}, com todos os
	 * parâmetros de formatação por padrão.
	 */
	public BigDecimalConverter() {
		this(PADRAO_FORMATACAO_BIGDECIMAL, QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_DECIMAL, QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_INTEIRA,
		        QT_MINIMA_DIGITOS_EXIBIDOS_PARTE_DECIMAL);
	}

	/**
	 * Cria uma nova instância do tipo {@link BigDecimalConverter}, passando
	 * todos os parâmetros de formatação.
	 * 
	 * @param padraoFormatacaoBigDecimal
	 *            padrão de formatação do {@link BigDecimal}
	 * @param qtCasasDecimaisParaArredondamento
	 *            quantidade de casas decimais máximas
	 * @param qtMinimaDigitosExibidosParteInteira
	 *            quantidade mínima de dígitos exibidos na parte inteira do
	 *            número formatado
	 * @param qtMinimaDigitosExibidosParteDecimal
	 *            quantidade mínima de dígitos exibidos na parte decimal do
	 *            número formatado
	 */
	public BigDecimalConverter(String padraoFormatacaoBigDecimal, int qtCasasDecimaisParaArredondamento,
	        int qtMinimaDigitosExibidosParteInteira, int qtMinimaDigitosExibidosParteDecimal) {
		this.valorUtil = ValorUtil.get();
		decimalFormatter = new DecimalFormat(padraoFormatacaoBigDecimal, new DecimalFormatSymbols(DataUtil.LOCALE));
		decimalFormatter.setParseBigDecimal(true);
		decimalFormatter.setMinimumIntegerDigits(qtMinimaDigitosExibidosParteInteira);
		decimalFormatter.setMinimumFractionDigits(qtMinimaDigitosExibidosParteDecimal);

		this.qtCasasDecimaisParaArredondamento = qtCasasDecimaisParaArredondamento;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object convertToObject(String value, Locale locale) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		if (!value.matches("((\\-)?[0-9]{1,3}(\\.[0-9]{3})*(,[0-9]{1,})?)|((\\-)?[0-9]+(,[0-9]{1,})?)")) {
			throw getConversionException(value, null);
		}

		try {
			return decimalFormatter.parse(value);
		} catch (ParseException e) {
			throw getConversionException(value, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String convertToString(Object value, Locale locale) {
		if (value == null) {
			throw new IllegalArgumentException(MSG_ENTRADA_ILEGAL);
		}
		BigDecimal valor = (BigDecimal) value;
		return decimalFormatter.format(valorUtil.arredondar(valor, qtCasasDecimaisParaArredondamento));
	}

	/**
	 * Converte um valor BigDecimal de acordo com a quantidade de casas decimais
	 * informada.
	 * 
	 * @param value
	 *            valor a ser convertido
	 * @param locale
	 *            locale para utilização na conversão
	 * @param qtCasasDecimais
	 *            quantidade de casas decimais para a conversão
	 * @return valor convertido
	 */
	public String convertToString(Object value, Locale locale, int qtCasasDecimais) {
		if (value == null) {
			throw new IllegalArgumentException(MSG_ENTRADA_ILEGAL);
		}
		BigDecimal valor = (BigDecimal) value;
		return decimalFormatter.format(valorUtil.arredondar(valor, qtCasasDecimais));
	}

	/**
	 * Convete o valor para String, caso o parâmetro exibirZero seja false não
	 * exibirá o valor zero.
	 * 
	 * @param value
	 *            valor
	 * @param locale
	 *            locale
	 * @param exibirZero
	 *            false caso queira exibir '-' no lugar do zero
	 * @return o valor convertido
	 */
	public String convertToString(Object value, Locale locale, boolean exibirZero) {
		String retorno = convertToString(value, locale);
		if (!exibirZero && BigDecimal.ZERO.equals(value)) {
			retorno = "-";
		}
		return retorno;
	}

	/**
	 * Converte o {@link BigDecimal} em {@link String} com {@link Locale}
	 * <code>null</code>.
	 * 
	 * @param valor
	 *            {@link BigDecimal}
	 * @param qtCasasDecimais
	 *            quantidade de casas decimais para o valor formatado
	 * @return valor {@link String}
	 */
	public String convertToString(BigDecimal valor, int qtCasasDecimais) {
		return convertToString(valor, null, qtCasasDecimais);
	}

	private ConversionException getConversionException(String valor, Exception e) {
		String mensagem = "Não é possível converter o valor '" + valor + "' para um BigDecimal.";
		ConversionException conversionException = new ConversionException(mensagem, e);
		conversionException.setSourceValue(valor);
		conversionException.setTargetType(BigDecimal.class);
		conversionException.setConverter(this);
		conversionException.setLocale(DataUtil.LOCALE);
		conversionException.setResourceKey("DecimalValidator");
		return conversionException;
	}

}