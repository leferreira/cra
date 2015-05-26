package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;


/**
 * @author Thasso Araújo
 *
 */
public enum TipoOcorrencia implements CraEnum{
	
	PAGO("1", "PAGO"), 
	PROTESTADO("2", "PROTESTADO"), 
	RETIRADO("3", "RETIRADO"), 
	SUSTADO("4", "SUSTADO"), 
	DEVOLVIDO_POR_IRREGULARIDADE_SEM_CUSTAS("5", "DEVOLVIDO S/C"), 
	DEVOLVIDO_POR_IRREGULARIDADE_COM_CUSTAS("6", "DEVOLVIDO C/C"),
	LIQUIDACAO_EM_CONDICIONAL("7", "LIQUIDAÇÃO EM CONDICIONAL"), 
	TITULO_ACEITO("8", "TÍTULO ACEITO"), 
	EDITAL_APENAS_BAHIA_E_RIO_DE_JANEIRO("9","EDITAL. APENAS PARA BAHIA E RIO DE JANEIRO"),
	PROTESTO_DO_BANCO_CANCELADO("A", "PROTESTO DO BANCO CANCELADO"), 
	PROTESTO_JA_EFETUADO("B", "PROTESTO EFETUADO"), 
	PROTESTO_POR_EDITAL("C", "PROTESTO POR EDITAL"), 
	RETIRADO_POR_EDITAL("D", "RETIRADO POR EDITAL"),
	PROTESTO_DE_TERCEIRO_CANCELADO("E", "PROTESTO DE TERCEIRO CANCELADO"), 
	DESISTENCIA_DO_PROTESTO("F", "DESISTÊNCIA"), 
	SUSTADO_DEFINITIVO("G", "SUSTADO DEFINITIVO"), 
	EMISSAO_2_VIA_PROTESTO("I", "EMISSÃO DA 2ª VIA"), 
	CANCELAMENTO_EFETUADO_ANTERIORMENTE("J", "CANCELAMENTO EFETUADO ANTERIORMENTE"), 
	CANCELAMENTO_NAO_EFETUADO("X", "CANCELAMENTO NÃO EFETUADO");

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
