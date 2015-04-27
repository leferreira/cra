package br.com.ieptbto.cra.enumeration;

public enum SituacaoBatimento implements CraEnum {

	RECEBIDO("1","Recebido"),
	CONFIRMADO("2","Confirmado"),
	GERADO("3","Retorno Gerado");
	
	SituacaoBatimento(String constante, String label){
		this.constante=constante;
		this.label=label;
	}
	
	private String label;
	private String constante;
	
	@Override
	public String getConstante() {
		return constante;
	}
	@Override
	public String getLabel() {
		return label;
	}
}
