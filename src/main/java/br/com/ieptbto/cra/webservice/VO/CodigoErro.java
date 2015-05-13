package br.com.ieptbto.cra.webservice.VO;

public enum CodigoErro {
	SUCESSO("0000", "Envio efetuado com sucesso"), //
	ERRO_NO_PROCESSAMENTO_DO_ARQUIVO("9999", "Erro no processamento do arquivo"), //
	FALHA_NA_AUTENTICACAO("0001", "Falha na autenticação."), //
	NOME_DO_ARQUIVO_INVALIDO("0002", "Nome do arquivo inválido."), //
	NAO_EXISTE_RETORNO_NA_DATA_INFORMADA("0004", "Não existe retorno na data informada."), //
	NAO_EXISTE_CONFIRMACAO_NA_DATA_INFORMADA(" 0005", "Não existe confirmação na data informada."), //
	NAO_EXISTE_REMESSA_NA_DATA_INFORMADA(" 0006", "Não existe remessa na data informada."), //
	ARQUIVO_CONTEM_CARACTERE_QUE_NAO_ESTA_NO_PADRAO_ASCII(" 2101",
	        "Arquivo contém caractere que não está no padrão ASCII. Linha: XXX - Coluna(s): XXX."), //
	ARQUIVO_CORROMPIDO(" 2102", "Arquivo corrompido. Tamanho da linha inválido. Tamanho: XXX - Linha: XXX."), //
	MUNICIPIO_NAO_CADASTRADO_NA_CRA(" 2103", "Município: XXX não cadastrado na CRA."), //
	MUNICIPIO_NAO_POSSUI_CARTORIO_NA_CRA(" 2104", "Município: XXX - XXX não possui cartório na CRA."), //
	APRESENTANTE_NAO_AUTORIZADO_A_ENVIAR_TITULOS_PARA_O_MUNICIPIO(" 2105",
	        "Apresentante não autorizado a enviar títulos para o município (XXX - XXX)."), //
	MUNICIPIO_NAO_ESTA_ATIVO(" 2106", "Município: XXX - XXX não está ativo."), //
	ARQUIVO_CORROMPIDO_NUMERO_DE_CONTROLE_DO_DEVEDOR_NAO_ESTA_CONTINUO(" 2107",
	        "Arquivo corrompido. Número de controle do devedor (XXX) não está contínuo."), //
	ARQUIVO_CORROMPIDO_SOMA_DE_TITULOS_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER(" 2108",
	        "Arquivo corrompido. Soma de títulos existentes no arquivo (XXX) não confere com total informado no header (XXX)."), //
	ARQUIVO_CORROMPIDO_SOMA_DE_INDICACOES_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER(" 2109",
	        "Arquivo corrompido. Soma de indicações existentes no arquivo (XXX) não confere com total informado no header (XXX)."), //
	ARQUIVO_CORROMPIDO_SOMA_DE_ORIGINAIS_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER(" 2110",
	        "Arquivo corrompido. Soma de originais existentes no arquivo (XXX) não confere com total informado no header (XXX)."), //
	SEQUENCIAL_DO_DEVEDOR_INVALIDO(" 2111", "Sequencial do devedor inválido (XXX)"), //
	INFORMAR_O_NOME_DO_DEVEDOR(" 2112", "Informar o nome do devedor."), //
	INFORMAR_O_DOCUMENTO_DO_DEVEDOR(" 2113", "Informar o documento do devedor."), //
	ARQUIVO_CORROMPIDO_NUMERO_SEQUENCIAL_DO_REGISTRO_NO_HEADER_NAO_ESTA_CONTINUO_FALTOU_REGISTRO(" 2114",
	        "Arquivo corrompido. Número sequencial do registro no header (XXX) não está contínuo. Faltou registro (XXX)."), //
	ARQUIVO_NAO_E_DE_REMESSA(" 2115", "Arquivo não é de remessa (tipo TPR). "), O_ARQUIVO_JA_FOI_ENVIADO_EM(" 2116",
	        "O arquivo XXX já foi enviado em XXX."), JA_FOI_ENVIADA_REMESSA_COM_O_MESMO_SEQUENCIAL_PARA_O_MUNICIPIO(" 2117",
	        "Já foi enviada remessa com o mesmo sequencial (XXX) para o município (XXX)."), //
	CODIGO_FEBRABAN_DO_APRESENTANTE_INVALIDO(" 2118", "Código Febraban do apresentante inválido (XXX)."), EXISTE_REMESSA_NO_ARQUIVO_COM_O_MESMO_SEQUENCIAL_PARA_O_MUNICIPIO(
	        " 2119", "Existe remessa no arquivo com o mesmo sequencial (XXX) para o município (XXX)."), //
	ARQUIVO_CORROMPIDO_NUMERO_SEQUENCIAL_DO_REGISTRO_NA_TRANSACAO_NAO_ESTA_CONTINUO_FALTOU_REGISTRO(" 2120",
	        "Arquivo corrompido. Número sequencial do registro na transação (XXX) não está contínuo. Faltou registro (XXX)."), //
	ARQUIVO_CORROMPIDO_NUMERO_SEQUENCIAL_DO_REGISTRO_NO_TRAILLER_NAO_ESTA_CONTINUO_FALTOU_REGISTRO(" 2121",
	        "Arquivo corrompido. Número sequencial do registro no trailler (XXX) não está contínuo. Faltou registro (XXX)."), //
	ARQUIVO_CORROMPIDO_SOMA_DE_REGISTROS_DE_TRANSACAO_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER(" 2122",
	        "Arquivo corrompido. Soma de registros de transação existentes no arquivo (XXX) não confere com total informado no header (XXX)."), //
	ARQUIVO_CORROMPIDO_SOMATÓRIO_DO_SALDO_NO_TRAILLER_NAO_CONFERE_COM_SOMATORIO_DOS_SALDOS_DOS_TITULOS(" 2123",
	        "Arquivo corrompido. Somatório do saldo no trailler (XXX) não confere com somatório dos saldos dos títulos (XXX)."), //
	ARQUIVO_CORROMPIDO_NUMERO_SEQUENCIAL_DO_REGISTRO_NO_HEADER_DO_CARTORIO_NAO_ESTA_CONTINUO_FALTOU_REGISTRO(" 2124",
	        "Arquivo corrompido. Número sequencial do registro no header do cartório (XXX) não está contínuo. Faltou registro (XXX)."), //
	ARQUIVO_CORROMPIDO_SOLICITACAO_NAO_E_DE_DESISTENCIA(" 2125", "Arquivo corrompido. Solicitação não é de desistência (S)."), //
	ARQUIVO_CORROMPIDO_NUMERO_SEQUENCIAL_DO_REGISTRO_NO_TRAILLER_DO_CARTORIO_NAO_ESTA_CONTINUO_FALTOU_REGISTRO(" 2126",
	        "Arquivo corrompido. Número sequencial do registro no trailler do cartório (XXX) não está contínuo. Faltou registro (XXX)."), //
	ARQUIVO_CORROMPIDO_SOMA_DE_SOLICITACOES_DO_CARTORIO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER_DO_CARTORIO(" 2127",
	        "Arquivo corrompido. Soma de solicitações do cartório (XXX) não confere com total informado no header do cartório (XXX)."), //
	ARQUIVO_CORROMPIDO_TOTAL_DE_REGISTROS_INFORMADO_NO_TRAILLER_NAO_CONFERE_COM_A_SOMA_DOS_TOTAIS_INFORMADOS_NO_HEADER_DO_ARQUIVO(
	        " 2128",
	        "Arquivo corrompido. Total de registros informado no trailler (XXX) não confere com a soma dos totais informados no header do arquivo (XXX)."), //
	ARQUIVO_CORROMPIDO_SOMA_DE_REGISTROS_EXISTENTES_NO_ARQUIVO_NAO_CONFERE_COM_TOTAL_INFORMADO_NO_HEADER_DO_ARQUIVO(" 2129",
	        "Arquivo corrompido. Soma de registros existentes no arquivo (XXX) não confere com total informado no header do arquivo (XXX)."), CODIGO_DO_MUNICIPIO_NAO_INFORMADO(
	        " 2130", "Código do município não informado."), //
	NAO_EXISTE_MUNICIPIO_COM_O_CODIGO_INFORMADO_NA_CRA(" 2131", "Não existe município com o código informado (XXX) na CRA."), CARTORIO_NAO_ENCONTRADO_NO_MUNICIPIO(
	        " 2132", "Cartório (XXX) não encontrado no município (XXX - XXX)."), //
	ARQUIVO_CORROMPIDO_SOLICITAÇÃO_NAO_E_DE_CANCELAMENTO(" 2133", "Arquivo corrompido. Solicitação não é de cancelamento (C)."), PROTOCOLO_INVALIDO(
	        " 2134", "Protocolo inválido (XXX)."), DATA_DO_PROTOCOLO_INVALIDA(" 2135", "Data do protocolo inválida (XXX)."), //
	ARQUIVO_CORROMPIDO_SOMATORIO_DE_SEGURANCA_DOS_REGISTROS_NAO_CONFERE_COM_A_SOMA_DAS_QUANTIDADES_INFORMADAS_NO_HEADER(" 2136",
	        "Arquivo corrompido. Somatório de segurança dos registros (XXX) não confere com a soma das quantidades informadas no header (XXX)."), CAMPO_INVALIDO(
	        " 2137", "Campo 'XXX' inválido."), //
	DATA_DO_MOVIMENTO_INVALIDA(" 2138", "Data do movimento inválida (XXX). "), JA_PASSOU_DO_HORÁRIO_LIMITE_PARA_ENVIO_DA_REMESSA(" 2139",
	        "Já passou do horário limite para envio da remessa."), JA_PASSOU_DO_HORÁRIO_LIMITE_PARA_ENVIO_DO_CANCELAMENTO(" 2140",
	        "Já passou do horário limite para envio do cancelamento."), //
	JA_PASSOU_DO_HORÁRIO_LIMITE_PARA_ENVIO_DA_DESISTENCIA("2141", "Já passou do horário limite para envio da desistência."), DADOS_DE_ENVIO_INVALIDOS(
	        "0003", "Dados de envio inválidos");

	private String codigo;
	private String descricao;

	private CodigoErro(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
}
