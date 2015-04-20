package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;


public enum TipoOcorrencia implements CraEnum{
	
	PAGO("1", "Pago"), 
	PROTESTADO("2", "Protestado"), 
	RETIRADO("3", "Retirado"), 
	SUSTADO("4", "Sustado"), 
	DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS("5", "Devolvido por irregularidade.Sem custas"), 
	DEVOLVIDO_POR_IRREGULARIDADE_COM_CUSTAS("6", "Devolvido por irregularidade.Com custas"),
	LIQUIDACAO_EM_CONDICIONAL("7", "Liquidação em Condicional"), 
	TITULO_ACEITO("8", "Título Aceito"), 
	EDITAL_APENAS_BAHIA_E_RIO_DE_JANEIRO("9","Edital. Apenas para Bahia e Rio de Janeiro"),
	PROTESTO_DO_BANCO_CANCELADO("A", "Protesto do banco cancelado"), 
	PROTESTO_JA_EFETUADO("B", "Protesto já efetuado"), 
	PROTESTO_POR_EDITAL("C", "Protesto por edital"), 
	RETIRADO_POR_EDITAL("D", "Retirado por edital"),
	PROTESTO_DE_TERCEIRO_CANCELADO("E", "Protesto de terceiro cancelado"), 
	DESISTENCIA_DO_PROTESTO("F", "Desistência do Protesto"), 
	SUSTADO_DEFINITIVO("G", "Sustado definitivo"), 
	EMISSAO_2_VIA_PROTESTO("I", "Emissão da 2ª via"), 
	CANCELAMENTO_EFETUADO_ANTERIORMENTE("J", "Cancelamento efetuado anteriormente"), 
	CANCELAMENTO_NAO_EFETUADO("X", "Cancelamento não efetuado");

	TipoOcorrencia(String constante, String label){
		this.constante = constante;
		this.label = label;
	}
	
	public String constante;
	public String label;
	
	public String getConstante() {
		return constante;
	}

	public String getLabel() {
		return label;
	}
	
	/**
	 * retorna o tipo da ocorrencia dependendo do tipo informado
	 * 
	 * @param valor
	 * @return tipo ocorrencia
	 */
	public static TipoOcorrencia getTipoOcorrencia(String valor) {
		TipoOcorrencia[] values = TipoOcorrencia.values();
		for (TipoOcorrencia tipoOcorrencia : values) {
			if (valor.startsWith(tipoOcorrencia.getConstante())) {
				return tipoOcorrencia;
			}
		}
		throw new InfraException("Tipo de Ocorrência não encontrado: " + valor);
	}
}
