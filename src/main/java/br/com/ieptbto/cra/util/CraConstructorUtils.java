package br.com.ieptbto.cra.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.lang.reflect.ConstructorUtils;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 *         Utilitário para execução de construtores por reflexão.
 *
 */
@SuppressWarnings("unchecked")
public class CraConstructorUtils {
	/**
	 * Invoca o construtor da classe passada como parâmetro com os argumentos
	 * passados como parâmetro
	 * 
	 * @param <T>
	 * 
	 * @param clazz
	 *            classe a ter seu construtor executado
	 * @param args
	 *            argumentos para o construtor
	 * @return instância da classe
	 */
	public static <T> T invokeConstructor(Class<T> clazz, Object... args) throws NoSuchMethodException {
		try {
			return clazz.cast(ConstructorUtils.invokeConstructor(clazz, args));
		} catch (IllegalAccessException e) {
			throw new InfraException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new InfraException(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new InfraException(e.getMessage(), e);
		} catch (ClassCastException e) {
			throw new InfraException(e.getMessage(), e);
		}
	}

	/**
	 * Instanciar uma classe.
	 * 
	 * @param <T>
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T newInstance(Class<T> clazz) {
		Factory factory = InstantiateFactory.getInstance(clazz, null, null);
		return (T) factory.create();
	}
}
