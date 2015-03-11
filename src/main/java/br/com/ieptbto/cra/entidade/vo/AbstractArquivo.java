package br.com.ieptbto.cra.entidade.vo;

import javax.xml.bind.annotation.XmlAttribute;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;

/**
 * 
 * @author Lefer
 *
 */
public abstract class AbstractArquivo {

	@XmlAttribute(name = "t01")
	@IAtributoArquivo(ordem = 1, posicao = 1, tamanho = 1, descricao = "Identificar o registro header no arquivo. Constante 0.", obrigatoriedade = true, validacao = "0", tipo = Integer.class)
	private String identificacaoRegistro;

	private TipoArquivo tipoArquivo;

	public String getIdentificacaoRegistro() {
		return identificacaoRegistro;
	}

	public TipoArquivo getTipoArquivo() {
		return tipoArquivo;
	}

	public void setIdentificacaoRegistro(String identificacaoRegistro) {
		this.identificacaoRegistro = identificacaoRegistro;
	}

	public void setTipoArquivo(TipoArquivo tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

}
