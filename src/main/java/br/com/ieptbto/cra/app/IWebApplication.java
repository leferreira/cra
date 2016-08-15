/*
 * Copyright (c) Lefer Software Corp.
 *
 * Este software é confidencial e propriedade da Lefer Software Corp.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização da Micromed Biotecnologia.
 * Este arquivo contém informações proprietárias.
 br.com.nmeios.kliniek.apper.sisjudi.app;

 import org.apache.wicket.Component;

 /**
 * Interface usada para obter informações gerais do sistema pelas páginas que extendem
 * {@link AbstractThunderaWebPage}. É automaticamente implementada por
 * {@link AbstractThunderaWebApplication.java}.
 * 
 */
package br.com.ieptbto.cra.app;

import org.apache.wicket.Component;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.webpage.AbstractWebPage;

public interface IWebApplication {
	/**
	 * Retorna o menu usado na aplicacao. O metodo pode retornar qualquer
	 * componente Wicket. Ele sera apenas adicionado pagina, sem operacao alguma
	 * adicional. Se retornar <code>null</code>, nenhum menu sera adicionado
	 * pagina.
	 * <p>
	 * Nao adicione o componente pagina. Isso sera feito posteriormente.
	 * <p>
	 * <strong>ATECAO!</strong> Este metodo e chamado no construtor das paginas.
	 * Portanto, os atributos da subclasse ainda nao estarao inicializados.
	 * 
	 * @param page
	 *            a pagina que usara o menu
	 * @param containerId
	 *            o wicket id que devera ser usado na construcao do componente
	 * @return o componente que representa o menu
	 */
	Component createMenuSistema(AbstractWebPage<?> page, String containerId, Usuario usuario);

	/**
	 * Obtem o titulo do sistema para apresentar no cabecalho das paginas. Por
	 * default, pega o que estiver na chave "sistema.titulo". Nos aplicativos
	 * sem internacionalizacao, pode ser mais simples sobrescrever o metodo.
	 * 
	 * @param page
	 *            a pagina que usara o titulo
	 */
	String getTituloSistema(AbstractWebPage<?> page);
}
