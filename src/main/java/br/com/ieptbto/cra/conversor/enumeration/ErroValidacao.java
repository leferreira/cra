package br.com.ieptbto.cra.conversor.enumeration;

public enum ErroValidacao {
	/** */
	USUARIO_SEM_PERMISSAO_DE_ENVIO_DE_ARQUIVO("Usuário não possui permissão para envio desse arquivo", 100),
	/** */
	INSTITUICAO_BLOQUEADA("Instituição bloqueada para envio de arquivos.", 200),

	SEM_PERMISSAO_DE_ACESSO_A_PAGINA("Você não tem permissão para acessar essa página.", 400),

	/** */
	NOME_DO_ARQUIVO_INVALIDO("O nome do arquivo inválido.", 300);

	private String mensagemErro;
	private int codigo;

	private ErroValidacao(String mensagemErro, int codigo) {
		this.mensagemErro = mensagemErro;
		this.codigo = codigo;
	}

	/**
	 * @return
	 */
	public String getMensagemErro() {
		return mensagemErro;
	}

	public int getCodigo() {
		return codigo;
	}

}
