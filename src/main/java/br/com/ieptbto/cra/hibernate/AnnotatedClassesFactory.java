/*
 * Copyright (c) Lefer Software Corp.
 *
 * Este software e confidencial e propriedade da Lefer Software Corp.
 * Não e permitida sua distribuição ou divulgação do seu conteudo sem
 * expressa autorização da Lefer Software Corp.
 * Este arquivo contem informaçães proprietarias.
 * 
 */

package br.com.ieptbto.cra.hibernate;

import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.GrupoUsuario;
import br.com.ieptbto.cra.entidade.Usuario;

/**
 * 
 * 
 */
public class AnnotatedClassesFactory {

	/** Lista de classes Hibernate */
	private static List<Class<?>> classesHibernate = new ArrayList<Class<?>>();

	/** Tem todas as classes: Hibernate. */
	private static List<Class<?>> classes = new ArrayList<Class<?>>();

	static {
		adicionarClassesHibernate();
		inicializarListaCompletaDeClasses();
	}

	/**
	 * Metodo responsavel por mapear classes Hibernate.
	 */
	private static void adicionarClassesHibernate() {
		classesHibernate.add(Usuario.class);
		classesHibernate.add(GrupoUsuario.class);
		// classesHibernate.add(Atendimento.class);
		// classesHibernate.add(Aluno.class);
		// classesHibernate.add(Turma.class);
		// classesHibernate.add(KliniekRevisionEntity.class);
	}

	/**
	 * Metodo responsavel por inicializar as classes do Hibernate.
	 */
	private static void inicializarListaCompletaDeClasses() {
		classes.addAll(classesHibernate);
	}

	/**
	 * Retorna uma Lista com os nomes das classes informadas.
	 * 
	 * @param classes
	 *            Lista de classe anotadas
	 * @return {@link List} Lista de nomes das classes
	 */
	@SuppressWarnings("unused")
	private static List<String> classToString(List<Class<?>> classes) {
		List<String> classesString = new ArrayList<String>();
		for (Class<?> classe : classes) {
			classesString.add(classe.getName());
		}
		return classesString;
	}

	/**
	 * Retorna a lista com todas as classes do sistema que devem ser mapeadas
	 * com o Hibernate.
	 * 
	 * @return todas as classes mapeadas
	 */
	public static List<Class<?>> getClassesAnotadas() {
		return classes;
	}

}
