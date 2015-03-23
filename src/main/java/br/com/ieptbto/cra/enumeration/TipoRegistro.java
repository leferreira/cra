package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

public enum TipoRegistro {

	TITULO("1", "Tírulo"), CABECALHO("0", "Cabeçalho"), RODAPE("9", "Rodapé");

	private String constante;
	private String label;

	TipoRegistro(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	public String getConstante() {
		return constante;
	}

	public String getLabel() {
		return label;
	}

	public static TipoRegistro get(String linha) {

		if (linha.startsWith(TipoRegistro.TITULO.getConstante())) {
			return TipoRegistro.TITULO;
		} else if (linha.startsWith(TipoRegistro.CABECALHO.getConstante())) {
			return TipoRegistro.CABECALHO;
		} else if (linha.startsWith(TipoRegistro.RODAPE.getConstante())) {
			return TipoRegistro.RODAPE;
		} else {
			new InfraException("Tipo de Registro desconhecido : " + linha);
			return null;
		}
	}

}
