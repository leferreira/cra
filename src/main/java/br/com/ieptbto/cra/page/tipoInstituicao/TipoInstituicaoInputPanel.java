package br.com.ieptbto.cra.page.tipoInstituicao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
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
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.PermissaoEnvioMediator;
import br.com.ieptbto.cra.mediator.TipoArquivoMediator;
import br.com.ieptbto.cra.mediator.TipoInstituicaoMediator;

@SuppressWarnings("serial")
public class TipoInstituicaoInputPanel extends Panel {

	private static final Logger logger = Logger.getLogger(TipoInstituicaoInputPanel.class);
	@SpringBean
	private TipoInstituicaoMediator tipoInstituicaoMediator;
	@SpringBean
	private TipoArquivoMediator tipoArquivoMediator;
	@SpringBean
	private PermissaoEnvioMediator permissaoEnvioMediator;

	private List<TipoArquivo> listaTiposArquivos = new ArrayList<TipoArquivo>();
	private Component button;
	private TextField<String> nomeTipo;
	private CheckBoxMultipleChoice<String> tipoPermitidos;

	public TipoInstituicaoInputPanel(String id, IModel<TipoInstituicao> model,TipoInstituicao tipoInstituicao) {
		super(id, model);
		adicionarCampos();
	}

	public TipoInstituicaoInputPanel(String id) {
		super(id);
		adicionarCampos();
	}

	public void adicionarCampos() {
		add(campoTipoInstituicao());
		add(comboTipoArquivos());
		add(botaoSalvar());
	}

	private Component botaoSalvar() {
		button = new Button("botaoSalvar") {
			/** */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			public void onSubmit() {
				TipoInstituicao tipoInstituicao = new TipoInstituicao();
				String tipo = (String)nomeTipo.getDefaultModelObject();
				tipoInstituicao.setTipoInstituicao(tipo);
				List<String> tiposArquivos = (List<String>) tipoPermitidos.getDefaultModelObject();
				
				//[B, CP, DP]
				try {
					if (tipoInstituicao.getId() != 0) {
						TipoInstituicao tipoSalvo = tipoInstituicaoMediator.alterar(tipoInstituicao);
						listaTiposArquivos = buscarListaTipoArquivos(tiposArquivos);
						setPermissoes(tipoSalvo,listaTiposArquivos);
						info("Os dados foram alterado com sucesso!");
					} else {
						TipoInstituicao tipoSalvo = tipoInstituicaoMediator.salvar(tipoInstituicao);
						listaTiposArquivos = buscarListaTipoArquivos(tiposArquivos);
						setPermissoes(tipoSalvo,listaTiposArquivos);
						info("Os dados foram salvos com sucesso!");
					}
				} catch (InfraException ex) {
					logger.error(ex.getMessage());
					error(ex.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
				}
			}
		};
		return button;

	}
	
	/***
	 * Método que transforma o array de strings em TipoArquivo 
	 * */
	private List<TipoArquivo> buscarListaTipoArquivos(List<String> tiposArquivo){
		TipoArquivo tipoArquivo = new TipoArquivo();
		for (String label: tiposArquivo){
			tipoArquivo= tipoArquivoMediator.buscarTipoPorNome(TipoArquivoEnum.getTipoArquivoEnum(label));
			listaTiposArquivos.add(tipoArquivo);
		}
		return listaTiposArquivos;
	}
	
	/**
	 * Método que seta a permissão para o tipo instituicao
	 * */
	private void setPermissoes(TipoInstituicao tipo, List<TipoArquivo> listaTipo){
		PermissaoEnvio permitidos = new PermissaoEnvio();
		try {
			for (TipoArquivo tipoArquivo: listaTipo){
				permitidos.setTipoArquivo(tipoArquivo);
				permitidos.setTipoInstituicao(tipo);
				permissaoEnvioMediator.alterar(permitidos);
			}
		} catch (InfraException ex) {
			logger.error(ex.getMessage());
			error(ex.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			error("Não foi possível realizar esta operação! \n Entre em contato com a CRA ");
		}
	}
	
	private TextField<String> campoTipoInstituicao() {
		nomeTipo = new TextField<String>("tipoInstituicao");
		nomeTipo.setLabel(new Model<String>("Tipo Instituição"));
		nomeTipo.setRequired(true);
		nomeTipo.setOutputMarkupId(true);
		return nomeTipo;
	}

	public Component comboTipoArquivos() {
		List<String> choices = new ArrayList<String>();
		List<TipoArquivoEnum> enumLista = Arrays.asList(TipoArquivoEnum.values());
		for (TipoArquivoEnum tipo : enumLista) {
			choices.add(tipo.constante);
		}
		return tipoPermitidos = new CheckBoxMultipleChoice<String>("arquivosEnvioPermitido", choices);
	}
}
