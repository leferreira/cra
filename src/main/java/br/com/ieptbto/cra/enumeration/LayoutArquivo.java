package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
public enum LayoutArquivo implements CraEnum {
	TXT("0", "Arquivo TXT"), XML("<", "Arquivo XML");

	private String constante;
	private String label;

	private LayoutArquivo(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	@Override
	public String getConstante() {
		return this.constante;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	/**
	 * retorna o layout do arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static LayoutArquivo get(String valor) {
		LayoutArquivo[] values = LayoutArquivo.values();
		for (LayoutArquivo layout : values) {
			if (valor.startsWith(layout.getConstante())) {
				return layout;
			}
		}
		throw new InfraException("O layout do arquivo é desconhecido : " + valor);
	}

}
