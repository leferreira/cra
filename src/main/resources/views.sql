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