package br.com.ieptbto.cra.conversor.arquivo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

import br.com.ieptbto.cra.entidade.vo.AbstractArquivo;
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
		map.put(List.class, ListConversor.class);
		map.put(BigDecimal.class, BigDecimalConversor.class);
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
		if (Enum.class.isAssignableFrom(propertyType)) {
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
	public static AbstractConversor<?> getConversor(CampoArquivo campoArquivo, AbstractArquivo arquivo) {

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
	public static AbstractConversor<?> getConversor(CampoArquivo campoArquivo, AbstractArquivo arquivo, Class<?> propertyType) {
		AbstractConversor<?> conversor = FabricaConversor.getConversor(propertyType);
		conversor.setCampoArquivo(campoArquivo);
		conversor.setArquivo(arquivo);

		return conversor;
	}

	/**
	 * Retorna o valor convertido utilizando o conversor apropriado.
	 * 
	 * @param propertyType
	 * @param valor
	 * @return
	 */
	public static Object getValorConvertido(Class<?> propertyType, String valor) {
		AbstractConversor<?> conversor = getConversor(propertyType);
		return conversor.getValorConvertido(valor);
	}
}
