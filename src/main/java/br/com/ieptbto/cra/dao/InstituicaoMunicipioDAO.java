package br.com.ieptbto.cra.dao;

import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ieptbto.cra.entidade.InstituicaoMunicipio;

@Repository
public class InstituicaoMunicipioDAO extends AbstractBaseDAO {

	@Transactional(readOnly = true)
	public InstituicaoMunicipio salvar(InstituicaoMunicipio im) {
		InstituicaoMunicipio nova = new InstituicaoMunicipio();
		Transaction transaction = getBeginTransation();
		try {
			nova = save(im);
		} catch (Exception ex) {
			transaction.rollback();
		}
		return nova;
	}
}
