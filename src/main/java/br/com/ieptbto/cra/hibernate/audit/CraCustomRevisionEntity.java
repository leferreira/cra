package br.com.ieptbto.cra.hibernate.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

/**
 * 
 * @author Lefer
 *
 */
@Entity
@RevisionEntity(CraRevisionListener.class)
@Table(name = "REVINFO")
public class CraCustomRevisionEntity extends DefaultRevisionEntity {

	/****/
	private static final long serialVersionUID = 1L;
	private String login;
	private int idUsuario;

	public String getLogin() {
		return login;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
}
