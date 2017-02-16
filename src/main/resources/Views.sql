/**
 * VIEW VIEW_BATIMENTO_RETORNO
 * */
CREATE OR REPLACE VIEW view_batimento_retorno AS 
 SELECT arq.id_arquivo AS idarquivo_arquivo,
    arq.nome_arquivo AS nomearquivo_arquivo,
    arq.data_envio AS dataenvio_arquivo,
    arq.hora_envio AS horaenvio_arquivo,
    arq.data_recebimento AS datarecebimento_arquivo,
    rem.id_remessa AS idremessa_remessa,
    rem.situacao_batimento_retorno::text AS situacaobatimento_remessa,
    ins_ori.id_instituicao AS idinstituicao_cartorio,
    ins_ori.nome_fantasia AS nomefantasia_cartorio,
    mun.id_municipio AS idmunicipio_municipio,
    mun.nome_municipio AS nomemunicipio_municipio,
    mun.cod_ibge AS cod_ibge_municipio,
    ins_des.id_instituicao AS idinstituicao_instituicao,
    ins_des.tipo_batimento AS tipobatimento_instituicao,
    ins_des.nome_fantasia AS nomefantasia_instituicao,
    ins_des.codigo_compensacao AS codigocompensacao_instituicao,
    sum(
        CASE
            WHEN ret.tipo_ocorrencia::text = '1'::text THEN ret.valor_saldo_titulo
            ELSE 0::numeric
        END) AS totalvalorlpagos,
    sum(
        CASE
            WHEN ret.tipo_ocorrencia::text = '2'::text OR ret.tipo_ocorrencia::text = '3'::text OR ret.tipo_ocorrencia::text = '6'::text THEN ret.valor_custa_cartorio
            ELSE 0::numeric
        END) AS totalcustascartorio,
    sum(ret.valor_demais_despesas) AS totaldemaisdespesas
   FROM tb_remessa rem
     JOIN tb_retorno ret ON ret.remessa_id = rem.id_remessa
     JOIN tb_arquivo arq ON rem.arquivo_id = arq.id_arquivo
     JOIN tb_instituicao ins_ori ON rem.instituicao_origem_id = ins_ori.id_instituicao
     JOIN tb_municipio mun ON ins_ori.municipio_id = mun.id_municipio
     JOIN tb_instituicao ins_des ON rem.instituicao_destino_id = ins_des.id_instituicao
  WHERE rem.situacao = false
  GROUP BY arq.id_arquivo, arq.nome_arquivo, arq.data_envio, arq.hora_envio, arq.data_recebimento, rem.id_remessa, rem.situacao_batimento_retorno, ins_ori.id_instituicao, ins_ori.nome_fantasia, mun.id_municipio, mun.nome_municipio, mun.cod_ibge, ins_des.id_instituicao, ins_des.tipo_batimento, ins_des.nome_fantasia, ins_des.codigo_compensacao
  ORDER BY arq.data_recebimento;
  
/**
 * VIEW TITULOS
 * */
CREATE OR REPLACE VIEW view_titulo AS 
 SELECT tit.id_titulo AS id_tituloremessa,
    tit.nosso_numero AS nossonumero_tituloremessa,
    tit.numero_titulo AS numerotitulo_tituloremessa,
    tit.numero_controle_devedor AS numerocontroledevedor_tituloremessa,
    tit.nome_devedor AS nomedevedor_tituloremessa,
    tit.valor_saldo_titulo AS saldotitulo_tituloremessa,
    arq.nome_arquivo AS nomearquivo_arquivo_remessa,
    arq.data_recebimento::date AS datarecebimento_arquivo_remessa,
    arq.data_envio AS dataenvio_arquivo_remessa,
    arq.id_arquivo AS id_arquivo_remessa,
    rem.id_remessa AS id_remessa_remessa,
    ins_apre.id_instituicao AS id_instituicao_instituicao,
    ins_apre.nome_fantasia AS nomefantasia_instituicao,
    ins_apre.codigo_compensacao AS codigocompensacao_instituicao,
    ins_apre.tipo_instituicao_id::integer AS tipoinstituicao_instituicao,
    conf.id_confirmacao,
    conf.numero_protocolo_cartorio AS numeroprotocolocartorio_confirmacao,
    arq_conf.nome_arquivo AS nomearquivo_arquivo_confirmacao,
    arq_conf.id_arquivo AS id_arquivo_confirmacao,
    arq_conf.data_recebimento::date AS datarecebimento_arquivo_confirmacao,
    arq_conf.data_envio AS dataenvio_arquivo_confirmacao,
    rem_conf.id_remessa AS id_remessa_confirmacao,
    arq_conf_bco.nome_arquivo AS nomearquivo_arquivo_confirmacao_instituicao,
    arq_conf_bco.id_arquivo AS id_arquivo_confirmacao_instituicao,
    arq_conf_bco.data_recebimento::date AS datarecebimento_arquivo_confirmacao_instituicao,
    ret.id_retorno,
    arq_ret.nome_arquivo AS nomearquivo_arquivo_retorno,
    arq_ret.id_arquivo AS id_arquivo_retorno,
    arq_ret.data_recebimento::date AS datarecebimento_arquivo_retorno,
    arq_ret.data_envio AS dataenvio_arquivo_retorno,
    rem_ret.id_remessa AS id_remessa_retorno,
    arq_ret_bco.nome_arquivo AS nomearquivo_arquivo_retorno_instituicao,
    arq_ret_bco.id_arquivo AS id_arquivo_retorno_instituicao,
    arq_ret_bco.data_recebimento::date AS datarecebimento_arquivo_retorno_instituicao,
        CASE
            WHEN ret.id_retorno IS NOT NULL THEN ret.data_ocorrencia
            WHEN conf.id_confirmacao IS NOT NULL THEN conf.data_ocorrencia
            ELSE NULL::bytea
        END AS dataocorrencia_confirmacaoretorno,
    ins_cart.id_instituicao AS id_instituicao_cartorio,
    mun.id_municipio,
    mun.nome_municipio AS nomemunicipio_municipio,
    mun.cod_ibge AS codigoibge_municipio,
        CASE
            WHEN conf.id_confirmacao IS NULL THEN 'S/CONFIRMAÇÃO'::text
            WHEN conf.tipo_ocorrencia::text = '5'::text THEN 'DEVOLVIDO S/C'::text
            WHEN conf.id_confirmacao IS NOT NULL AND ret.id_retorno IS NULL THEN 'ABERTO'::text
            WHEN ret.tipo_ocorrencia::text = '1'::text THEN 'PAGO'::text
            WHEN ret.tipo_ocorrencia::text = '2'::text THEN 'PROTESTADO'::text
            WHEN ret.tipo_ocorrencia::text = '3'::text THEN 'RETIRADO'::text
            WHEN ret.tipo_ocorrencia::text = '4'::text THEN 'SUSTADO'::text
            WHEN ret.tipo_ocorrencia::text = '5'::text THEN 'DEVOLVIDO S/C'::text
            WHEN ret.tipo_ocorrencia::text = '6'::text THEN 'DEVOLVIDO C/C'::text
            ELSE NULL::text
        END AS situacaotitulo,
    tit.numero_identificacao_devedor AS numeroidentificacaodevedor_tituloremessa,
    tit.nome_sacador_vendedor AS nomesacadorvendedor_tituloremessa,
    tit.documento_sacador AS documentosacador_tituloremessa
   FROM tb_titulo tit
     JOIN tb_remessa rem ON tit.remessa_id = rem.id_remessa
     JOIN tb_arquivo arq ON rem.arquivo_id = arq.id_arquivo
     JOIN tb_instituicao ins_apre ON rem.instituicao_origem_id = ins_apre.id_instituicao
     JOIN tb_instituicao ins_cart ON rem.instituicao_destino_id = ins_cart.id_instituicao
     JOIN tb_municipio mun ON ins_cart.municipio_id = mun.id_municipio
     LEFT JOIN tb_confirmacao conf ON tit.id_titulo = conf.titulo_id
     LEFT JOIN tb_remessa rem_conf ON conf.remessa_id = rem_conf.id_remessa
     LEFT JOIN tb_arquivo arq_conf ON rem_conf.arquivo_id = arq_conf.id_arquivo
     LEFT JOIN tb_arquivo arq_conf_bco ON rem_conf.arquivo_gerado_banco_id <> rem_conf.arquivo_id AND rem_conf.arquivo_gerado_banco_id = arq_conf_bco.id_arquivo
     LEFT JOIN tb_retorno ret ON tit.id_titulo = ret.titulo_id
     LEFT JOIN tb_remessa rem_ret ON ret.remessa_id = rem_ret.id_remessa
     LEFT JOIN tb_arquivo arq_ret ON rem_ret.arquivo_id = arq_ret.id_arquivo
     LEFT JOIN tb_arquivo arq_ret_bco ON rem_ret.arquivo_gerado_banco_id <> rem_ret.arquivo_id AND rem_ret.arquivo_gerado_banco_id = arq_ret_bco.id_arquivo;
     
ALTER TABLE view_titulo
  OWNER TO postgres;

/**
 *  View de Arquivos Pendentes
 * */  
CREATE OR REPLACE VIEW view_remessa_pendente AS 
SELECT 'B'::text AS tipo_arquivo,
    arq.id_arquivo AS idarquivo_arquivo,
    arq.nome_arquivo AS nomearquivo_arquivo,
    arq.data_recebimento AS datarecebimento_arquivo,
    arq.data_envio AS dataenvio_arquivo,
    arq.hora_envio AS horaenvio_arquivo,
    rem.status_download AS statusdownload,
    rem.id_remessa AS idremessa_remessa,
        CASE
            WHEN count(ane.id_anexo) > 0 THEN true
            ELSE false
        END AS documentosanexos_anexo,
    NULL::integer AS iddesistencia_desistenciaprotesto,
    NULL::integer AS idcancelamento_cancelamentoprotesto,
    NULL::integer AS idautorizacao_autorizacaocancelamento,
    ins_ori.id_instituicao AS idinstituicao_instituicao,
    ins_ori.nome_fantasia AS nomefantasia_instituicao,
    ins_ori.codigo_compensacao AS codigocompensacao_instituicao,
    ins_des.id_instituicao AS idinstituicao_cartorio,
    ins_des.nome_fantasia AS nomefantasia_cartorio,
    mun.nome_municipio AS nomemunicipio_municipio,
    mun.cod_ibge AS cod_ibge_municipio
   FROM tb_titulo tit
     JOIN tb_remessa rem ON tit.remessa_id = rem.id_remessa
     JOIN tb_instituicao ins_ori ON rem.instituicao_origem_id = ins_ori.id_instituicao
     JOIN tb_instituicao ins_des ON rem.instituicao_destino_id = ins_des.id_instituicao
     JOIN tb_municipio mun ON ins_des.municipio_id = mun.id_municipio
     JOIN tb_arquivo arq ON rem.arquivo_id = arq.id_arquivo
     LEFT JOIN tb_confirmacao conf ON tit.id_titulo = conf.titulo_id
     LEFT JOIN tb_anexo ane ON tit.id_titulo = ane.titulo_id
  WHERE conf.id_confirmacao IS NULL AND arq.id_arquivo > 18088
  GROUP BY arq.id_arquivo, arq.nome_arquivo, arq.data_recebimento, arq.data_envio, rem.id_remessa, ins_ori.id_instituicao, ins_ori.nome_fantasia, ins_ori.codigo_compensacao, ins_des.id_instituicao, ins_des.nome_fantasia, mun.nome_municipio, mun.cod_ibge
  ORDER BY rem.id_remessa;
  
  ALTER TABLE view_remessa_pendente OWNER TO postgres;
  
CREATE OR REPLACE VIEW view_desistencia_pendente AS 
 SELECT 'DP'::text AS tipo_arquivo,
    arq.id_arquivo AS idarquivo_arquivo,
    arq.nome_arquivo AS nomearquivo_arquivo,
    arq.data_recebimento AS datarecebimento_arquivo,
    arq.data_envio AS dataenvio_arquivo,
    arq.hora_envio AS horaenvio_arquivo,
    'AGUARDANDO'::character varying AS statusdownload,
    NULL::integer AS idremessa_remessa,
    false AS documentosanexos_anexo,
    dp.id_desistencia_protesto AS iddesistencia_desistenciaprotesto,
    NULL::integer AS idcancelamento_cancelamentoprotesto,
    NULL::integer AS idautorizacao_autorizacaocancelamento,
    ins_ori.id_instituicao AS idinstituicao_instituicao,
    ins_ori.nome_fantasia AS nomefantasia_instituicao,
    ins_ori.codigo_compensacao AS codigocompensacao_instituicao,
    ins_des.id_instituicao AS idinstituicao_cartorio,
    ins_des.nome_fantasia AS nomefantasia_cartorio,
    mun.nome_municipio AS nomemunicipio_municipio,
    mun.cod_ibge AS cod_ibge_municipio
   FROM tb_desistencia_protesto dp
     JOIN tb_remessa_desistencia_protesto rem ON dp.remessa_desistencia_protesto_id = rem.id_remessa_desistencia_protesto
     JOIN tb_cabecalho_arquivo_desistencia_cancelamento cab_arq ON cab_arq.id_cabecalho_arquivo_dp = rem.cabecalho_desistencia_protesto_id
     JOIN tb_instituicao ins_ori ON cab_arq.codigo_apresentante::text = ins_ori.codigo_compensacao::text
     JOIN tb_cabecalho_cartorio_desistencia_cancelamento cab_car ON dp.cabecalho_id = cab_car.id_cabecalho_cartorio_dp
     JOIN tb_municipio mun ON cab_car.codigo_municipio::text = mun.cod_ibge::text
     JOIN tb_instituicao ins_des ON ins_des.municipio_id = mun.id_municipio AND ins_des.tipo_instituicao_id = 2
     JOIN tb_arquivo arq ON rem.arquivo_id = arq.id_arquivo
  WHERE dp.download_realizado = false
  GROUP BY arq.id_arquivo, arq.nome_arquivo, arq.data_recebimento, arq.data_envio, dp.id_desistencia_protesto, ins_ori.id_instituicao, ins_ori.nome_fantasia, ins_ori.codigo_compensacao, ins_des.id_instituicao, ins_des.nome_fantasia, mun.nome_municipio, mun.cod_ibge
  ORDER BY dp.id_desistencia_protesto;
  
ALTER TABLE view_desistencia_pendente OWNER TO postgres;
  
CREATE OR REPLACE VIEW view_cancelamento_pendente AS 
SELECT 'CP'::text AS tipo_arquivo,
    arq.id_arquivo AS idarquivo_arquivo,
    arq.nome_arquivo AS nomearquivo_arquivo,
    arq.data_recebimento AS datarecebimento_arquivo,
    arq.data_envio AS dataenvio_arquivo,
    arq.hora_envio AS horaenvio_arquivo,
    'AGUARDANDO'::character varying AS statusdownload,
    NULL::integer AS idremessa_remessa,
    false AS documentosanexos_anexo,
    NULL::integer AS iddesistencia_desistenciaprotesto,
    cp.id_cancelamento_protesto AS idcancelamento_cancelamentoprotesto,
    NULL::integer AS idautorizacao_autorizacaocancelamento,
    ins_ori.id_instituicao AS idinstituicao_instituicao,
    ins_ori.nome_fantasia AS nomefantasia_instituicao,
    ins_ori.codigo_compensacao AS codigocompensacao_instituicao,
    ins_des.id_instituicao AS idinstituicao_cartorio,
    ins_des.nome_fantasia AS nomefantasia_cartorio,
    mun.nome_municipio AS nomemunicipio_municipio,
    mun.cod_ibge AS cod_ibge_municipio
   FROM tb_cancelamento_protesto cp
     JOIN tb_remessa_cancelamento_protesto rem ON cp.remessa_cancelamento_protesto_id = rem.id_remessa_cancelamento_protesto
     JOIN tb_cabecalho_arquivo_desistencia_cancelamento cab_arq ON cab_arq.id_cabecalho_arquivo_dp = rem.cabecalho_cancelamento_protesto_id
     JOIN tb_instituicao ins_ori ON cab_arq.codigo_apresentante::text = ins_ori.codigo_compensacao::text
     JOIN tb_cabecalho_cartorio_desistencia_cancelamento cab_car ON cp.cabecalho_id = cab_car.id_cabecalho_cartorio_dp
     JOIN tb_municipio mun ON cab_car.codigo_municipio::text = mun.cod_ibge::text
     JOIN tb_instituicao ins_des ON ins_des.municipio_id = mun.id_municipio AND ins_des.tipo_instituicao_id = 2
     JOIN tb_arquivo arq ON rem.arquivo_id = arq.id_arquivo
  WHERE cp.download_realizado = false
  GROUP BY arq.id_arquivo, arq.nome_arquivo, arq.data_recebimento, arq.data_envio, cp.id_cancelamento_protesto, ins_ori.id_instituicao, ins_ori.nome_fantasia, ins_ori.codigo_compensacao, ins_des.id_instituicao, ins_des.nome_fantasia, mun.nome_municipio, mun.cod_ibge
  ORDER BY cp.id_cancelamento_protesto;
  
ALTER TABLE view_cancelamento_pendente OWNER TO postgres;
  
CREATE OR REPLACE VIEW view_autorizacao_pendente AS 
SELECT 'AC'::text AS tipo_arquivo,
    arq.id_arquivo AS idarquivo_arquivo,
    arq.nome_arquivo AS nomearquivo_arquivo,
    arq.data_recebimento AS datarecebimento_arquivo,
    arq.data_envio AS dataenvio_arquivo,
    arq.hora_envio AS horaenvio_arquivo,
    'AGUARDANDO'::character varying AS statusdownload,
    NULL::integer AS idremessa_remessa,
    false AS documentosanexos_anexo,
    NULL::integer AS iddesistencia_desistenciaprotesto,
    NULL::integer AS idcancelamento_cancelamentoprotesto,
    ac.id_autorizacao_cancelamento AS idautorizacao_autorizacaocancelamento,
    ins_ori.id_instituicao AS idinstituicao_instituicao,
    ins_ori.nome_fantasia AS nomefantasia_instituicao,
    ins_ori.codigo_compensacao AS codigocompensacao_instituicao,
    ins_des.id_instituicao AS idinstituicao_cartorio,
    ins_des.nome_fantasia AS nomefantasia_cartorio,
    mun.nome_municipio AS nomemunicipio_municipio,
    mun.cod_ibge AS cod_ibge_municipio
   FROM tb_autorizacao_cancelamento ac
     JOIN tb_remessa_autorizacao_cancelamento rem ON ac.remessa_autorizacao_cancelamento_protesto_id = rem.id_remessa_autorizacao_cancelamento
     JOIN tb_cabecalho_arquivo_desistencia_cancelamento cab_arq ON cab_arq.id_cabecalho_arquivo_dp = rem.cabecalho_autorizacao_cancelamento_id
     JOIN tb_instituicao ins_ori ON cab_arq.codigo_apresentante::text = ins_ori.codigo_compensacao::text
     JOIN tb_cabecalho_cartorio_desistencia_cancelamento cab_car ON ac.cabecalho_id = cab_car.id_cabecalho_cartorio_dp
     JOIN tb_municipio mun ON cab_car.codigo_municipio::text = mun.cod_ibge::text
     JOIN tb_instituicao ins_des ON ins_des.municipio_id = mun.id_municipio AND ins_des.tipo_instituicao_id = 2
     JOIN tb_arquivo arq ON rem.arquivo_id = arq.id_arquivo
  WHERE ac.download_realizado = false
  GROUP BY arq.id_arquivo, arq.nome_arquivo, arq.data_recebimento, arq.data_envio, ac.id_autorizacao_cancelamento, ins_ori.id_instituicao, ins_ori.nome_fantasia, ins_ori.codigo_compensacao, ins_des.id_instituicao, ins_des.nome_fantasia, mun.nome_municipio, mun.cod_ibge
  ORDER BY ac.id_autorizacao_cancelamento;
  
  ALTER TABLE view_autorizacao_pendente OWNER TO postgres;
  