package br.com.ieptbto.cra.entidade.vo;

import java.util.List;

/**
 * 
 * @author Lefer
 *
 */
public class ArquivoVO extends AbstractArquivoVO {

	private List<CabecalhoVO> cabecalhos;
	private List<TituloVO> titulos;
	private List<RodapeVO> rodapes;

	public List<CabecalhoVO> getCabecalhos() {
		return cabecalhos;
	}

	public List<TituloVO> getTitulos() {
		return titulos;
	}

	public List<RodapeVO> getRodapes() {
		return rodapes;
	}

	public void setCabecalhos(List<CabecalhoVO> cabecalhos) {
		this.cabecalhos = cabecalhos;
	}

	public void setTitulos(List<TituloVO> titulos) {
		this.titulos = titulos;
	}

	public void setRodapes(List<RodapeVO> rodapes) {
		this.rodapes = rodapes;
	}

}
