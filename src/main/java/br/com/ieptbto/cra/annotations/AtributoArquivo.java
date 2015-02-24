package br.com.ieptbto.cra.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classe responsável por mapear os atributos de um arquivo
 * 
 * @author Lefer
 *
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AtributoArquivo {

	/**
	 * Define o tamanho do campo
	 * 
	 * @return
	 */
	int tamanho();

	/**
	 * Defina a posição inicial dos campos
	 * 
	 * @return
	 */
	int posicao();

	/**
	 * Definição das validações dos campos
	 * 
	 * @return
	 */
	String validacao() default "";

	/**
	 * Formato do dado, se existir.
	 * 
	 * @return
	 */
	String formato() default "";

	/**
	 * Espaços em branco após o campo.
	 * 
	 * @return
	 */
	int filler() default 0;

	/**
	 * Define se o campo é obrigatório
	 * 
	 * @return
	 */
	boolean obrigatoriedade() default false;

	/**
	 * Descrição do campo.
	 * 
	 * @return
	 */
	String descricao();

	/**
	 * Definite o tipo do campo
	 * 
	 * @return
	 */
	Class<?> tipo() default Object.class;
}
