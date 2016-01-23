package br.com.ieptbto.cra.page.cra;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.page.base.BasePage;
import br.com.ieptbto.cra.page.base.HomePage;

/**
 * @author Thasso Araújo
 *
 */
public class MensagemPage<T extends AbstractEntidade<T>> extends BasePage<T>{

	/***/
	private static final long serialVersionUID = 1L;

	private Class<?> classe;
	private String nomeDaPagina;
	private String mensagem;
	
	public MensagemPage(Class<?> classe, String pageName, String message) {
		this.nomeDaPagina = pageName;
		this.mensagem = message;

		info(message);
		add(labelNomeDaPagina());
	}
	
	private Label labelNomeDaPagina() {
		return new Label("pageName", getNomeDaPagina().toUpperCase());
	}

	public Class<?> getClasse() {
		if (classe == null) {
			classe = HomePage.class;
		}
		return classe;
	}
	
	public String getNomeDaPagina() {
		if (mensagem == null) {
			mensagem = StringUtils.EMPTY;
		}
		return nomeDaPagina;
	}
	
	public String getMensagem() {
		if (mensagem == null) {
			mensagem = StringUtils.EMPTY;
		}
		return mensagem;
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}
