package br.com.ieptbto.cra.enumeration;

import javax.persistence.Entity;

@Entity
public enum TipoArquivoEnum {

	Remessa("B"), Confirmação("C"), Retorno("R"), Cancelamento_de_Protesto("CP"), Devolução_de_Protesto(
			"DP"), Autorização_de_Cancelamento("AC");

	public String constante;

	TipoArquivoEnum(String constante) {
		this.constante = constante;
	}

	public String getConstante() {
		return constante;
	}

	@SuppressWarnings("unused")
	private TipoArquivoEnum getTipoArquivo(String tipo) {

		if (tipo.equals("B")) {
			return TipoArquivoEnum.Remessa;
		} else {
			if (tipo.equals("C")) {
				return TipoArquivoEnum.Confirmação;
			} else {
				if (tipo.equals("R")) {
					return TipoArquivoEnum.Retorno;
				} else {
					if (tipo.equals("CP")) {
						return TipoArquivoEnum.Cancelamento_de_Protesto;
					} else {
						if (tipo.endsWith("DP")) {
							return TipoArquivoEnum.Devolução_de_Protesto;
						} else {
							return TipoArquivoEnum.Autorização_de_Cancelamento;
						}
					}
				}
			}
		}
	}
}
