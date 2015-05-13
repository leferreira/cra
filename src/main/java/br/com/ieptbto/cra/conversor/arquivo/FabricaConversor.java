package br.com.ieptbto.cra.conversor.arquivo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.enumeration.CraEnum;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer Fábrica de conversores
 * 
 */
public class FabricaConversor {
	/**
	 * Conversores registrados para converter os campos de cada registro.
	 */
	private static final Map<Class<?>, Class<? extends AbstractConversor<?>>> CONVERSORES;
	static {
		HashMap<Class<?>, Class<? extends AbstractConversor<?>>> map = new HashMap<Class<?>, Class<? extends AbstractConversor<?>>>();
		map.put(Integer.class, IntegerConversor.class);
		map.put(String.class, StringConversor.class);
		map.put(LocalDate.class, DateConversor.class);
		map.put(LocalDateTime.class, DateTimeConversor.class);
		map.put(List.class, ListConversor.class);
		map.put(BigDecimal.class, BigDecimalConversor.class);
		map.put(Enum.class, EnumConversor.class);
		CONVERSORES = Collections.unmodifiableMap(map);
	}

	/**
	 * Recupera um conversor registrado.
	 * 
	 * @param propertyType
	 * @return
	 */
	public static AbstractConversor<?> getConversor(Class<?> propertyType) {
		Class<? extends AbstractConversor<?>> conversorClass = CONVERSORES.get(propertyType);
		if (CraEnum.class.isAssignableFrom(propertyType)) {
			return new EnumConversor();
		}
		try {
			return conversorClass.newInstance();
		} catch (InstantiationException e) {
			throw new InfraException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new InfraException(e.getMessage(), e);
		}
	}

	/**
	 * Recupera um conversor registrado.
	 * 
	 * @param campoArquivo
	 * @param registro
	 * 
	 * @return
	 */
	public static AbstractConversor<?> getConversor(CampoArquivo campoArquivo, AbstractArquivoVO arquivo) {

		Class<?> propertyType = campoArquivo.getType();
		return getConversor(campoArquivo, arquivo, propertyType);
	}

	/**
	 * Recupera um conversor registrado.
	 * 
	 * @param campoArquivo
	 * @param arquivo
	 * @param propertyType
	 * @return
	 */
	public static AbstractConversor<?> getConversor(CampoArquivo campoArquivo, AbstractArquivoVO arquivo, Class<?> propertyType) {
		AbstractConversor<?> conversor = FabricaConversor.getConversor(propertyType);
		conversor.setCampoArquivo(campoArquivo);
		conversor.setArquivo(arquivo);

		return conversor;
	}

	public static String getValorConvertidoParaString(CampoArquivo campoArquivo, Class<?> propertyType, Object valor) {
		AbstractConversor<?> conversor = FabricaConversor.getConversor(propertyType);
		conversor.setCampoArquivo(campoArquivo);
		return conversor.getValorConvertidoString(valor);
	}

	/**
	 * Retorna o valor convertido utilizando o conversor apropriado.
	 * 
	 * @param propertyType
	 * @param valor
	 * @return
	 */
	public static Object getValorConvertido(Class<?> propertyType, String valor, AbstractArquivoVO entidade, CampoArquivo campoArquivo) {
		AbstractConversor<?> conversor = getConversor(propertyType);
		conversor.setArquivo(entidade);
		conversor.setCampoArquivo(campoArquivo);
		return conversor.getValorConvertido(valor, propertyType);
	}

}
