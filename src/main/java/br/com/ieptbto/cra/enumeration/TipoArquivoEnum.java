package br.com.ieptbto.cra.enumeration;

import javax.persistence.Entity;

@Entity
public enum TipoArquivoEnum {

	Remessa("B"),
	Confirmação("C"),
	Retorno("R"),
	Cancelamento_de_Protesto("CP"),
	Devolução_de_Protesto("DP"),
	Autorização_de_Cancelamento("AC");
	
	public String constante;
	
	TipoArquivoEnum(String constante){
		this.constante=constante;
	}
}
