package br.com.ieptbto.cra.entidade;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

public class UsuarioAnonimo extends Usuario {
	private static final long serialVersionUID = 1L;

	@Override
	public Roles getRoles() {
		// anonymous users have only one role
		return new Roles("is_anonymous");
	}

	@Override
	public boolean equals(Object user) {
		if (user instanceof UsuarioAnonimo) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
