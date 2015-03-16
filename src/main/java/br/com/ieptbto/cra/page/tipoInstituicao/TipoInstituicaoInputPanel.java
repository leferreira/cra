package br.com.ieptbto.cra.page.tipoInstituicao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.mediator.PermissaoEnvioMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;

@SuppressWarnings({ "serial" })
public class TipoInstituicaoInputPanel extends Panel {

	@SpringBean
	TipoInstituicaoMediator tipoInstituicaoMediator;
	@SpringBean
	TipoArquivoMediator tipoArquivoMediator;
	@SpringBean
	PermissaoEnvioMediator permissaoEnvioMediator;

	private Component button;
	private List<TipoArquivoEnum> enumLista = new ArrayList<TipoArquivoEnum>();
	private TextField<String> textField;
	private CheckBoxMultipleChoice<String> checkBox;
	private List<String> choices = new ArrayList<String>();;

	public TipoInstituicaoInputPanel(String id, IModel<TipoInstituicao> model) {
		super(id, model);
		adicionarCampos();
	}

	public TipoInstituicaoInputPanel(String id) {
		super(id);
		adicionarCampos();
	}

	public void adicionarCampos() {
		add(campoTipoInstituicao());
		add(comboCidades());
		add(botaoSalvar());
	}

	private TextField<String> campoTipoInstituicao() {
		textField = new TextField<String>("tipoInstituicao");
		textField.setLabel(new Model<String>("Tipo Instituição"));
		textField.setRequired(true);
		textField.setOutputMarkupId(true);
		return textField;
	}

	public Component comboCidades() {
		enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choices.add(tipo.toString().replace("_", " "));
		}
		return checkBox = new CheckBoxMultipleChoice<String>(
				"arquivosEnvioPermitido", choices);
	}

	private Component botaoSalvar() {
		button = new Button("botaoSalvar") {
			@SuppressWarnings("unchecked")
		
			public void onSubmit() {	
				TipoInstituicao tipoInstituicao = new TipoInstituicao();
				TipoArquivo tipoArquivo = new TipoArquivo();
				PermissaoEnvio permitidos = new PermissaoEnvio();
				TipoArquivoEnum arquivoEnum;
				
				String nomeTipo = (String) textField.getDefaultModelObject();
				List<String> selects = (List<String>) checkBox
						.getDefaultModelObject();

				tipoInstituicao.setTipoInstituicao(nomeTipo);
				tipoInstituicaoMediator.salvar(tipoInstituicao);

				for (String s : selects) {
					s = s.replace(" ", "_");
					
					if (TipoArquivoEnum.valueOf(s) != null) {	
						arquivoEnum = TipoArquivoEnum.valueOf(s);
						tipoArquivo = tipoArquivoMediator
								.buscarTipoPorNome(arquivoEnum);
						permitidos.setTipoInstituicao(tipoInstituicao);
						permitidos.setTipoArquivo(tipoArquivo);
						permissaoEnvioMediator.salvar(permitidos);
					}
				}
			}
		};
		return button;

	}
}
