package br.com.ieptbto.cra.entidade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.string.Strings;

import br.com.ieptbto.cra.security.IAuthModel;

@SuppressWarnings("serial")
@Entity
@Table(name = "USUARIO")
@org.hibernate.annotations.Table(appliesTo = "USUARIO")
public class Usuario extends AbstractEntidade<Usuario> implements IClusterable, IAuthModel {

	private int id;
	private String nome;
	private String login;
	private String senha;
	private String email;
	private GrupoUsuario grupoUsuario;

	@Override
	@Id
	@Column(name = "ID_USUARIO")
	@GeneratedValue
	public int getId() {
		return this.id;
	}

	@Column(name = "NOME_USUARIO")
	public String getNome() {
		return nome;
	}

	@Column(name = "LOGIN", unique = true)
	public String getLogin() {
		return login;
	}

	@Column(name = "SENHA")
	public String getSenha() {
		return senha;
	}

	@OneToOne
	@JoinColumn(name = "ID_GRUPO_USUARIO")
	public GrupoUsuario getGrupoUsuario() {
		return grupoUsuario;
	}

	@Column(name = "EMAIL")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setGrupoUsuario(GrupoUsuario grupoUsuario) {
		this.grupoUsuario = grupoUsuario;
	}

	public boolean isSenha(String pass) {
		if (Strings.isEmpty(pass)) {
			return false;
		}
		return senha.equals(Usuario.cryptPass(pass));
	}

	public static String cryptPass(String password) {
		return DigestUtils.sha256Hex(password);
	}

	@Override
	public int compareTo(Usuario entidade) {
		return 0;
	}

	@Override
	public boolean equals(Object user) {
		if (getId() != 0 && user instanceof Usuario) {
			return getId() == ((Usuario) user).getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (getId() == 0) {
			return 0;
		}
		return getId();
	}

	@Transient
	public Roles getRoles() {
		return this.grupoUsuario.getRoles();
	}

	public boolean hasAnyRole(Roles roles) {
		if (grupoUsuario != null) {
			return this.grupoUsuario.getRoles().hasAnyRole(roles);
		}
		return false;
	}

	public boolean hasRole(String role) {
		return this.grupoUsuario.getRoles().hasRole(role);
	}

}
