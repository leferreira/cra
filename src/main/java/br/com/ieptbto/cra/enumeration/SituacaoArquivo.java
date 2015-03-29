package br.com.ieptbto.cra.enumeration;

import br.com.ieptbto.cra.exception.InfraException;

public enum SituacaoArquivo implements CraEnum {
	
	AGUARDANDO("0","Aguardando"),ENVIADO("1","Enviado"),RECEBIDO("2","Recebido");
	
	private String constante;
	private String label;

	SituacaoArquivo(String constante, String label) {
		this.constante = constante;
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getConstante() {
		return constante;
	}

	/**
	 * retorna o status do arquivo dependendo da constante
	 * 
	 * @param valor
	 * @return status arquivo
	 */
	public static SituacaoArquivo get(String valor) {
		SituacaoArquivo[] values = SituacaoArquivo.values();
		for (SituacaoArquivo situacao : values) {
			if (valor.startsWith(situacao.getLabel())) {
				return situacao;
			}
		}
		throw new InfraException("Situacao do Arquivo desconhecido : " + valor);
	}
}
