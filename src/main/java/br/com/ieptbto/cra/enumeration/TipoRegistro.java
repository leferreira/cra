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

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoRegistro get(String valor) {
		TipoRegistro[] values = TipoRegistro.values();
		for (TipoRegistro tipoArquivo : values) {
			if (tipoArquivo.getConstante().startsWith(valor)) {
				return tipoArquivo;
			}
		}
		throw new InfraException("Tipo de Registro desconhecido : " + valor);
	}
}
