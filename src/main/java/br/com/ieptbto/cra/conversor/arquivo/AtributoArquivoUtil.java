package br.com.ieptbto.cra.conversor.arquivo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;

/**
 * 
 * @author Lefer
 *
 */
public class AtributoArquivoUtil {
	/**
	 * Recupera os campos anotados com {@link AtributoRegistro}.
	 * 
	 * @param registro
	 * @return os campos anotados do registro.
	 */
	public static List<CampoArquivo> getAnnotatedFields(AbstractArquivoVO registro) {
		Class<? extends AbstractArquivoVO> thisClass = registro.getClass();
		@SuppressWarnings("unchecked")
		List<Class<? extends AbstractArquivoVO>> allSuperclasses = ClassUtils.getAllSuperclasses(thisClass);
		allSuperclasses.remove(Object.class);
		allSuperclasses.add(thisClass);

		List<CampoArquivo> camposAnotados = new ArrayList<CampoArquivo>();
		for (Class<? extends AbstractArquivoVO> clazz : allSuperclasses) {
			camposAnotados.addAll(getAnnotatedFields(clazz));
		}

		return camposAnotados;
	}

	private static List<CampoArquivo> getAnnotatedFields(Class<? extends AbstractArquivoVO> class1) {
		List<CampoArquivo> camposAnotados = new ArrayList<CampoArquivo>();
		Field[] declaredFields = class1.getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.isAnnotationPresent(IAtributoArquivo.class)) {
				camposAnotados.add(new CampoArquivo(field));
			}
		}
		return camposAnotados;
	}
}
