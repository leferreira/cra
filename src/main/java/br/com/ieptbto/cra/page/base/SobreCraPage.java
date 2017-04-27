package br.com.ieptbto.cra.page.base;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.security.CraRoles;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Thasso Aráujo
 *
 * @param <T>
 */
@AuthorizeInstantiation(value = CraRoles.USER)
@AuthorizeAction(action = Action.RENDER, roles = { CraRoles.ADMIN, CraRoles.SUPER, CraRoles.USER })
public class SobreCraPage<T extends AbstractEntidade<T>> extends BasePage<T> {

	/***/
	private static final long serialVersionUID = 1L;

	public SobreCraPage() {
		adicionarComponentes();
	}

	@Override
	protected void adicionarComponentes() {
		add(textAreaException());
	}
	
	private Label textAreaException() {
		BufferedReader reader = null;

		String content = StringUtils.EMPTY;
		try {
			File changelogFile = new ClassPathResource("CHANGELOG").getFile();
			if (changelogFile.exists()) {
				reader = new BufferedReader(new FileReader(changelogFile));
				String linha = "";
				while ((linha = reader.readLine()) != null) {
					content = content.concat(linha) + "<br>";
				}
			} else {
				logger.info("Arquivo de CHANGELOG não encontrado. Favor entrar em contato com a CRA...");
				error("Arquivo de CHANGELOG não encontrado. Favor entrar em contato com a CRA...");
			}
		} catch (IOException ex) {
			logger.info("Erro ao ler o arquivo de log de mudanças. Favor entrar em contato com a CRA...");
			error("Erro ao ler o arquivo de log de mudanças. Favor entrar em contato com a CRA...");
		}
				
		Label textArea = new Label("changelog", content);
		textArea.setEscapeModelStrings(false);
		textArea.setEnabled(false);
		return textArea;
	}

	@Override
	protected IModel<T> getModel() {
		return null;
	}
}
