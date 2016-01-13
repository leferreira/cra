/*
 * Copyright (c) Lefer Software Corp.
 *
 * Este software e confidencial e propriedade da Lefer Software Corp.
 * Não e permitida sua distribuição ou divulgação do seu conteudo sem
 * expressa autorização da Lefer Software Corp.
 * Este arquivo contem informaçães proprietarias.
 * 
 */

package br.com.ieptbto.cra.hibernate;

import java.util.ArrayList;
import java.util.List;

import br.com.ieptbto.cra.entidade.AgenciaBancoDoBrasil;
import br.com.ieptbto.cra.entidade.AgenciaBradesco;
import br.com.ieptbto.cra.entidade.AgenciaCAF;
import br.com.ieptbto.cra.entidade.Anexo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.ArquivoCnp;
import br.com.ieptbto.cra.entidade.AutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.Avalista;
import br.com.ieptbto.cra.entidade.Batimento;
import br.com.ieptbto.cra.entidade.BatimentoDeposito;
import br.com.ieptbto.cra.entidade.CabecalhoArquivo;
import br.com.ieptbto.cra.entidade.CabecalhoCartorio;
import br.com.ieptbto.cra.entidade.CabecalhoCnp;
import br.com.ieptbto.cra.entidade.CabecalhoRemessa;
import br.com.ieptbto.cra.entidade.CancelamentoProtesto;
import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.Deposito;
import br.com.ieptbto.cra.entidade.DesistenciaProtesto;
import br.com.ieptbto.cra.entidade.EnvelopeSLIP;
import br.com.ieptbto.cra.entidade.EtiquetaSLIP;
import br.com.ieptbto.cra.entidade.Filiado;
import br.com.ieptbto.cra.entidade.GrupoUsuario;
import br.com.ieptbto.cra.entidade.Historico;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.InstrumentoProtesto;
import br.com.ieptbto.cra.entidade.LayoutFiliado;
import br.com.ieptbto.cra.entidade.Municipio;
import br.com.ieptbto.cra.entidade.PedidoAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoCancelamento;
import br.com.ieptbto.cra.entidade.PedidoDesistencia;
import br.com.ieptbto.cra.entidade.PermissaoEnvio;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.RemessaAutorizacaoCancelamento;
import br.com.ieptbto.cra.entidade.RemessaCancelamentoProtesto;
import br.com.ieptbto.cra.entidade.RemessaCnp;
import br.com.ieptbto.cra.entidade.RemessaDesistenciaProtesto;
import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.Rodape;
import br.com.ieptbto.cra.entidade.RodapeArquivo;
import br.com.ieptbto.cra.entidade.RodapeCartorio;
import br.com.ieptbto.cra.entidade.RodapeCnp;
import br.com.ieptbto.cra.entidade.SolicitacaoDesistenciaCancelamentoConvenio;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.TipoInstituicao;
import br.com.ieptbto.cra.entidade.TituloCnp;
import br.com.ieptbto.cra.entidade.TituloFiliado;
import br.com.ieptbto.cra.entidade.TituloRemessa;
import br.com.ieptbto.cra.entidade.TituloSemTaxaCRA;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.UsuarioFiliado;
import br.com.ieptbto.cra.hibernate.audit.CraCustomRevisionEntity;

/**
 * 
 * 
 */
public class AnnotatedClassesFactory {

	/** Lista de classes Hibernate */
	private static List<Class<?>> classesHibernate = new ArrayList<Class<?>>();

	/** Tem todas as classes: Hibernate. */
	private static List<Class<?>> classes = new ArrayList<Class<?>>();

	static {
		adicionarClassesHibernate();
		inicializarListaCompletaDeClasses();
	}

	/**
	 * Metodo responsavel por mapear classes Hibernate.
	 */
	private static void adicionarClassesHibernate() {
		classesHibernate.add(Usuario.class);
		classesHibernate.add(GrupoUsuario.class);
		classesHibernate.add(Instituicao.class);
		classesHibernate.add(TipoInstituicao.class);
		classesHibernate.add(Municipio.class);
		classesHibernate.add(TipoArquivo.class);
		classesHibernate.add(PermissaoEnvio.class);
		classesHibernate.add(Arquivo.class);
		classesHibernate.add(TituloRemessa.class);
		classesHibernate.add(Confirmacao.class);
		classesHibernate.add(Retorno.class);
		classesHibernate.add(CabecalhoRemessa.class);
		classesHibernate.add(Rodape.class);
		classesHibernate.add(Remessa.class);
		classesHibernate.add(StatusArquivo.class);
		classesHibernate.add(Historico.class);
		classesHibernate.add(InstrumentoProtesto.class);
		classesHibernate.add(EnvelopeSLIP.class);
		classesHibernate.add(EtiquetaSLIP.class);
		classesHibernate.add(AgenciaCAF.class);
		classesHibernate.add(AgenciaBancoDoBrasil.class);
		classesHibernate.add(AgenciaBradesco.class);
		classesHibernate.add(UsuarioFiliado.class);
		classesHibernate.add(Filiado.class);
		classesHibernate.add(TituloFiliado.class);
		classesHibernate.add(Avalista.class);
		classesHibernate.add(CraCustomRevisionEntity.class);
		classesHibernate.add(RemessaDesistenciaProtesto.class);
		classesHibernate.add(CabecalhoArquivo.class);
		classesHibernate.add(CabecalhoCartorio.class);
		classesHibernate.add(RodapeArquivo.class);
		classesHibernate.add(RodapeCartorio.class);
		classesHibernate.add(DesistenciaProtesto.class);
		classesHibernate.add(PedidoDesistencia.class);
		classesHibernate.add(LayoutFiliado.class);
		classesHibernate.add(TituloSemTaxaCRA.class);
		classesHibernate.add(Anexo.class);
		classesHibernate.add(RemessaCancelamentoProtesto.class);
		classesHibernate.add(RemessaAutorizacaoCancelamento.class);
		classesHibernate.add(CancelamentoProtesto.class);
		classesHibernate.add(PedidoCancelamento.class);
		classesHibernate.add(AutorizacaoCancelamento.class);
		classesHibernate.add(PedidoAutorizacaoCancelamento.class);
		classesHibernate.add(SolicitacaoDesistenciaCancelamentoConvenio.class);
		classesHibernate.add(ArquivoCnp.class);
		classesHibernate.add(RemessaCnp.class);
		classesHibernate.add(CabecalhoCnp.class);
		classesHibernate.add(TituloCnp.class);
		classesHibernate.add(RodapeCnp.class); 

		classesHibernate.add(Deposito.class);
		classesHibernate.add(BatimentoDeposito.class);
		classesHibernate.add(Batimento.class);
		
	}

	/**
	 * Metodo responsavel por inicializar as classes do Hibernate.
	 */
	private static void inicializarListaCompletaDeClasses() {
		classes.addAll(classesHibernate);
	}

	/**
	 * Retorna uma Lista com os nomes das classes informadas.
	 * 
	 * @param classes
	 *            Lista de classe anotadas
	 * @return {@link List} Lista de nomes das classes
	 */
	@SuppressWarnings("unused")
	private static List<String> classToString(List<Class<?>> classes) {
		List<String> classesString = new ArrayList<String>();
		for (Class<?> classe : classes) {
			classesString.add(classe.getName());
		}
		return classesString;
	}

	/**
	 * Retorna a lista com todas as classes do sistema que devem ser mapeadas
	 * com o Hibernate.
	 * 
	 * @return todas as classes mapeadas
	 */
	public static List<Class<?>> getClassesAnotadas() {
		return classes;
	}

}
