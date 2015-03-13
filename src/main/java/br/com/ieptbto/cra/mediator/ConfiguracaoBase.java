package br.com.ieptbto.cra.mediator;

import org.springframework.stereotype.Service;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ConfiguracaoBase {

	public static final int TAMANHO_NOME_ARQUIVO = 13;
	public static String DIRETORIO_BASE = "../ARQUIVOS_CRA/";
	public static String DIRETORIO_TEMP_BASE = "../ARQUIVOS_CRA/TEMP/";
	public static String DIRETORIO_BASE_INSTITUICAO = DIRETORIO_BASE + "INSTITUICAO/";
	public static String BARRA = "/";

}
