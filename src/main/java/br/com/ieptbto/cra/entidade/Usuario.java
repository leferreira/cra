package br.com.ieptbto.cra.entidade;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.string.Strings;
import org.hibernate.envers.Audited;

import br.com.ieptbto.cra.security.IAuthModel;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(name = "TB_USUARIO")
@org.hibernate.annotations.Table(appliesTo = "TB_USUARIO")
public class Usuario extends AbstractEntidade<Usuario> implements IClusterable, IAuthModel {

	private int id;
	private String nome;
	private String login;
	private String senha;
	private String confirmarSenha;
	private String contato;
	private String email;
	private boolean status;
	private GrupoUsuario grupoUsuario;
	private Instituicao instituicao;
	private List<Historico> historicos;

	@Override
	@Id
	@Column(name = "ID_USUARIO", columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name = "SENHA", nullable = false)
	public String getSenha() {
		return senha;
	}

	@Column(name = "CONTATO")
	public String getContato() {
		return contato;
	}

	@OneToOne
	@JoinColumn(name = "GRUPO_USUARIO_ID")
	public GrupoUsuario getGrupoUsuario() {
		return grupoUsuario;
	}

	@ManyToOne
	@JoinColumn(name = "INSTITUICAO_ID")
	public Instituicao getInstituicao() {
		return instituicao;
	}

	@Column(name = "EMAIL")
	public String getEmail() {
		return email;
	}

	@Column(name = "STATUS")
	public boolean isStatus() {
		return status;
	}

	@OneToMany(mappedBy="usuarioAcao")
	public List<Historico> getHistoricos() {
		return historicos;
	}

	public void setHistoricos(List<Historico> historicos) {
		this.historicos = historicos;
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

	public void setContato(String contato) {
		this.contato = contato;
	}

	public void setGrupoUsuario(GrupoUsuario grupoUsuario) {
		this.grupoUsuario = grupoUsuario;
	}

	public void setInstituicao(Instituicao instituicao) {
		this.instituicao = instituicao;
	}

	public void setStatus(boolean status) {
		this.status = status;
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

	@Transient
	public String getSituacao() {
		if (isStatus() == true) {
			return "Ativo";
		} else {
			return "Não Ativo";
		}
	}

	public void setSituacao(String situacao) {
		if (situacao == "Ativo") {
			setStatus(true);
		} else {
			setStatus(false);
		}
	}

	@Transient
	public String getConfirmarSenha() {
		return confirmarSenha;
	}

	public void setConfirmarSenha(String confirmarSenha) {
		this.confirmarSenha = confirmarSenha;
	}
}
