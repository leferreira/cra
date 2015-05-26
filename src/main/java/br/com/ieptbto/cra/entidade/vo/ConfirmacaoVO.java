package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "confirmacao")
@XmlAccessorType(XmlAccessType.NONE)
public class ConfirmacaoVO extends AbstractArquivoVO {

	@XmlElement(name = "hd")
	private CabecalhoVO cabecalho;
	@XmlElement(name = "tr")
	private List<TituloVO> titulos;
	@XmlElement(name = "tl")
	private RodapeVO rodape;

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

	public CabecalhoVO getCabecalho() {
		return cabecalho;
	}

	public List<TituloVO> getTitulos() {
		return titulos;
	}

	public RodapeVO getRodape() {
		return rodape;
	}

	public void setCabecalho(CabecalhoVO cabecalho) {
		this.cabecalho = cabecalho;
	}

	public void setTitulos(List<TituloVO> titulos) {
		this.titulos = titulos;
	}

	public void setRodapes(RodapeVO rodape) {
		this.rodape = rodape;
	}

}
