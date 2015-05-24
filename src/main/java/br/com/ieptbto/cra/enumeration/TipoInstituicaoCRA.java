package br.com.ieptbto.cra.enumeration;

public enum TipoInstituicaoCRA implements CraEnum {
	
	CRA("1","Central de Remessa de Arquivos"), 
	CARTORIO("2","Cartório de Protesto"), 
	INSTITUICAO_FINANCEIRA("3","Instituicão Financeira ou outros");

	private String constante;
	private String label;
	
	private TipoInstituicaoCRA(String constante, String label ) {
		this.constante=constante;
		this.label=label;
	}
	
	@Override
	public String getConstante() {
		return constante;
	}

	@Override
	public String getLabel() {
		return label;
	}

}
