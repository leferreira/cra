package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.hibernate.envers.Audited;

/**
 * 
 * @author Leandro
 * 
 */
@Entity
@Audited
@Table(name = "TB_GRUPO_USUARIO")
@org.hibernate.annotations.Table(appliesTo = "TB_GRUPO_USUARIO")
public class GrupoUsuario extends AbstractEntidade<GrupoUsuario> {

	private static final long serialVersionUID = 1L;
	private int id;
	private Roles roles;
	private String grupo;

	/**
	 * @return the id
	 */
	@Id
	@Column(name = "ID_GRUPO_USUARIO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	/**
	 * @return the roles
	 */
	@Column(name = "ROLES")
	public Roles getRoles() {
		return roles;
	}

	/**
	 * @return the grupo
	 */
	@Column(name = "NOME_GRUPO", unique = true, nullable = false)
	public String getGrupo() {
		return grupo;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	/**
	 * @param grupo
	 *            the grupo to set
	 */
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	@Override
	public int compareTo(GrupoUsuario entidade) {
		return 0;
	}

	public boolean equals(Object user) {
		if (getId() != 0 && user instanceof Usuario) {
			return getId() == ((Usuario) user).getId();
		}
		return false;
	}
}
