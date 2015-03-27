package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

public enum TipoRegistro implements CraEnum {

	TITULO("1", "Título"), CABECALHO("0", "Cabeçalho"), RODAPE("9", "Rodapé");

	private String constante;
	private String label;

	TipoRegistro(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	@Override
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
		for (TipoRegistro tipoRegistro : values) {
			if (valor.startsWith(tipoRegistro.getConstante())) {
				return tipoRegistro;
			}
		}
		throw new InfraException("Tipo de Registro desconhecido : " + valor);
	}

}
