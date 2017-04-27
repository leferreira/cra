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

import br.com.ieptbto.cra.entidade.*;
import br.com.ieptbto.cra.entidade.view.*;

import java.util.ArrayList;
import java.util.List;

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
        classesHibernate.add(UsuarioAnonimo.class);
        classesHibernate.add(LogCra.class);
        classesHibernate.add(CraServiceConfig.class);
        classesHibernate.add(Usuario.class);
        classesHibernate.add(GrupoUsuario.class);
        classesHibernate.add(Instituicao.class);
        classesHibernate.add(TipoInstituicao.class);
        classesHibernate.add(Municipio.class);
        classesHibernate.add(TipoArquivo.class);
        classesHibernate.add(Arquivo.class);
        classesHibernate.add(TituloRemessa.class);
        classesHibernate.add(Confirmacao.class);
        classesHibernate.add(Retorno.class);
        classesHibernate.add(CabecalhoRemessa.class);
        classesHibernate.add(Rodape.class);
        classesHibernate.add(Remessa.class);
        classesHibernate.add(StatusArquivo.class);
        classesHibernate.add(InstrumentoProtesto.class);
        classesHibernate.add(EnvelopeSLIP.class);
        classesHibernate.add(EtiquetaSLIP.class);
        classesHibernate.add(AgenciaCAF.class);
        classesHibernate.add(AgenciaBancoDoBrasil.class);
        classesHibernate.add(AgenciaBradesco.class);
        classesHibernate.add(UsuarioFiliado.class);
        classesHibernate.add(Filiado.class);
        classesHibernate.add(SetorFiliado.class);
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
        classesHibernate.add(Anexo.class);
        classesHibernate.add(RemessaCancelamentoProtesto.class);
        classesHibernate.add(RemessaAutorizacaoCancelamento.class);
        classesHibernate.add(CancelamentoProtesto.class);
        classesHibernate.add(PedidoCancelamento.class);
        classesHibernate.add(AutorizacaoCancelamento.class);
        classesHibernate.add(PedidoAutorizacaoCancelamento.class);
        classesHibernate.add(SolicitacaoDesistenciaCancelamento.class);
        classesHibernate.add(Deposito.class);
        classesHibernate.add(BatimentoDeposito.class);
        classesHibernate.add(Batimento.class);
        classesHibernate.add(LoteCnp.class);
        classesHibernate.add(RegistroCnp.class);
        classesHibernate.add(TaxaCra.class);

        classesHibernate.add(ViewBatimentoRetorno.class);
        classesHibernate.add(ViewTitulo.class);
        classesHibernate.add(RemessaPendente.class);
        classesHibernate.add(DesistenciaPendente.class);
        classesHibernate.add(CancelamentoPendente.class);
        classesHibernate.add(AutorizacaoPendente.class);
        classesHibernate.add(RetornoCancelamento.class);
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