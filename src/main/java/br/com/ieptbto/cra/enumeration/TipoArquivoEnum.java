package br.com.ieptbto.cra.enumeration;

import javax.persistence.Entity;

import br.com.ieptbto.cra.exception.InfraException;

@Entity
public enum TipoArquivoEnum {

	REMESSA("B", "Remessa"), //
	CONFIRMACAO("C", "Confirmação"), //
	RETORNO("R", "Retorno"), //
	CANCELAMENTO_DE_PROTESTO("CP", "Cancelamento de Protesto"), //
	DEVOLUCAO_DE_PROTESTO("DP", "Devolução de Protesto"), //
	AUTORIZACAO_DE_CANCELAMENTO("AC", "Autorização de Cancelamento");

	public String constante;
	public String label;

	TipoArquivoEnum(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	public String getConstante() {
		return constante;
	}

	/**
	 * retorna o tipo de arquivo dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo arquivo
	 */
	public static TipoArquivoEnum getTipoArquivoEnum(String valor) {
		TipoArquivoEnum[] values = TipoArquivoEnum.values();
		for (TipoArquivoEnum tipoArquivo : values) {
			if (tipoArquivo.getConstante().startsWith(valor)) {
				return tipoArquivo;
			}
		}
		throw new InfraException("Tipo de Arquivo não encontrado: " + valor);
	}

}
