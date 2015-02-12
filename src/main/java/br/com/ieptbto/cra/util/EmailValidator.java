package br.com.ieptbto.cra.util;

import org.apache.wicket.validation.CompoundValidator;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * 
 * @author Leandro
 * 
 */
public class EmailValidator extends CompoundValidator<String> {

	private static final long serialVersionUID = 1L;

	public EmailValidator() {
		add(new PatternValidator("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$"));

	}
}
