package br.com.ieptbto.cra.entidade;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/*
 * Copyright (c) Lefer Software Corp.
 *
 * Este software é confidencial e propriedade da Lefer Software Corp.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização da Micromed Biotecnologia.
 * Este arquivo contém informações proprietárias.
 br.com.nmeios.kliniek.apper.sisjudi.app;

 import java.io.Serializable;

 /**
 * 
 * @author Lefer
 * 
 * @param <T>
 */
@MappedSuperclass
public abstract class AbstractEntidade<T> implements Serializable, Comparable<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Transient
	public abstract int getId();

	public abstract int compareTo(T entidade);

}
