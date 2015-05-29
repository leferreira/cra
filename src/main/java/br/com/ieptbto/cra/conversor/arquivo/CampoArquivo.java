package br.com.ieptbto.cra.conversor.arquivo;

import java.lang.reflect.Field;

import org.apache.commons.lang.reflect.FieldUtils;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;

/**
 * 
 * @author Lefer
 *
 */
public class CampoArquivo {

	private Field field;
	private IAtributoArquivo anotacaoAtributo;

	/**
	 * Único construtor.
	 * 
	 * @param field
	 */
	public CampoArquivo(Field field) {
		this.field = field;
		this.anotacaoAtributo = field.getAnnotation(IAtributoArquivo.class);
	}

	public CampoArquivo(String field, Class<?> cls) {
		this(FieldUtils.getField(cls, field, true));
	}

	/**
	 * Tipo da propriedade.
	 * 
	 * @return
	 */
	public Class<?> getType() {
		return field.getType();
	}

	/**
	 * @see IAtributoArquivo#tamanho()
	 * @return
	 */
	public int getTamanho() {
		return anotacaoAtributo.tamanho();
	}

	/**
	 * @see IAtributoArquivo#filler()
	 * @return
	 */
	public Integer getFiller() {
		return anotacaoAtributo.filler();
	}

	/**
	 * @see Field#getName()
	 * @return
	 */
	public String getName() {
		return field.getName();
	}

	/**
	 * @see IAtributoArquivo
	 * @return
	 */
	public IAtributoArquivo getAnotacaoAtributo() {
		return anotacaoAtributo;
	}

	/**
	 * @see IAtributoArquivo#posicao()
	 * @return
	 */
	public int getPosicaoInicio() {
		return anotacaoAtributo.posicao();
	}

	/**
	 * @see IAtributoArquivo#ordem()
	 * @return
	 */
	public int getOrdem() {
		return anotacaoAtributo.ordem();
	}

	/**
	 * Define em qual lado ficará o campo vazio
	 * 
	 * @return
	 */
	public PosicaoCampoVazio getPosicaoCampoVazio() {
		return anotacaoAtributo.posicaoCampoVazio();
	}

	/**
	 * Método responsável por retornar field .
	 * 
	 * @return field
	 */
	public Field getField() {
		return field;
	}

	public String getFormato() {
		return anotacaoAtributo.formato();
	}

	/**
	 * Método responsável por definir o campo field.
	 * 
	 * @param field
	 *            valor atribuído a field
	 */

	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.field.getName();
	}
}
