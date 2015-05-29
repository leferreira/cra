package br.com.ieptbto.cra.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;

/**
 * Classe responsável por mapear os atributos de um arquivo
 * 
 * @author Lefer
 *
 */
@Target(value = { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IAtributoArquivo {

	/**
	 * Define o tamanho do campo
	 * 
	 * @return
	 */
	int tamanho();

	/**
	 * Define a posição inicial dos campos
	 * 
	 * @return
	 */
	int posicao();

	/**
	 * Define qual o lado dos campo vazio (se esquerdo ou direito do conteudo)
	 * 
	 * @return
	 */
	PosicaoCampoVazio posicaoCampoVazio() default PosicaoCampoVazio.DIREITO;

	/**
	 * 
	 * Define a ordem do campo
	 * 
	 * @return
	 */
	int ordem();

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
	String formato() default " ";

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
	 * Quando se tratar de um campo multivalorado (lista), deverá ser informado
	 * a quantidade máxima de itens.
	 * 
	 * @return
	 */
	int quantidadeItens() default 1;

	/**
	 * Definite o tipo do campo
	 * 
	 * @return
	 */
	Class<?> tipo() default Object.class;
}
