package br.com.ieptbto.cra.webservice.dao;

/**
 * @author Thasso Araújo
 *
 */
public enum MensagemCartorioProtesto {

	ERRO_DE_PRECESSAMENTO_CRA("Erro de processamento, informe à CRA !"),
	CODIGO_PORTADOR_INVALIDO(""),
	NUMERO_TITULO_INVALIDO(""),
	NOSSO_NUMERO_INVALIDO(""),
	OCORRENCIA_NAO_ENCONTRADA_OU_NAO_EXISTE(""),
	OCORRENCIA_PROCESSADA_COM_SUCESSO("");
	
	private String mensagem;
	
	private MensagemCartorioProtesto(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getMensagem() {
		return mensagem;
	}
}
