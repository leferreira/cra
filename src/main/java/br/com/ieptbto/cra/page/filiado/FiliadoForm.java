package br.com.ieptbto.cra.page.filiado;

import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.SetorFiliado;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.FiliadoMediator;
import br.com.ieptbto.cra.page.base.BaseForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public class FiliadoForm extends BaseForm<Filiado> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	FiliadoMediator filiadoMediator;

	private List<SetorFiliado> setores;

	public FiliadoForm(String id, IModel<Filiado> model, List<SetorFiliado> setores) {
		super(id, model);
		this.setores = setores;
	}

	@Override
	protected void onSubmit() {
		Filiado filiado = getModelObject();
		boolean existeSetorAtivo = false;

		try {
			for (SetorFiliado setor : setores) {
				if (setor.isSituacaoAtivo() == true) {
					existeSetorAtivo = true;
					break;
				}
			}
			if (existeSetorAtivo == false) {
				throw new InfraException("Não é possível salvar os dados do filiado! Ao menos um setor deve estar ativo!");
			}

			if (filiado.getId() != 0) {
				filiadoMediator.alterarFiliado(filiado);
			} else {
				filiadoMediator.salvarFiliado(filiado);
			}
			setResponsePage(new ListaFiliadoPage("Os dados do novo filiado foi salvo com sucesso !"));
		} catch (InfraException ex) {
			error(ex.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			error("Não foi possível cadastrar a empresa filiada ! Entre em contato com o IEPTB !");
		}
	}

}