==================================================================================================================================
cod:3
descricao:CORREÇÃO PARA ENTRADA MANUAL E LAYOUT PERSONALIZADO
dependencia:0

UPDATE tb_instituicao as ins
SET layout_padrao_xml='ENTRADA_MANUAL_LAYOUT_PERSONALIZADO'
WHERE ins.layout_padrao_xml = 'LAYOUT_PERSONALIZADO_CONVENIOS' 
	OR ins.layout_padrao_xml = 'ENTRADA_MANUAL';

UPDATE tb_instituicao as ins SET administrar_empresas_filiadas=false;
UPDATE tb_instituicao as ins SET administrar_empresas_filiadas=true WHERE ins.tipo_instituicao_id=4;

UPDATE tb_instituicao as ins SET oficio_desistencia_cancelamento_obrigatorio=false;
UPDATE tb_instituicao as ins SET oficio_desistencia_cancelamento_obrigatorio=true WHERE ins.tipo_instituicao_id=4;

UPDATE tb_instituicao as ins SET layout_retorno_recebimento_empresa=false;

==================================================================================================================================

cod:2
descricao:AJUSTES DE CAMPOS DOWNLOAD REMESSA
dependencia:0

/****
RODAR ANTES
*/
ALTER TABLE tb_instituicao RENAME permitido_setores_convenio TO setores_convenio;
ALTER TABLE audit_tb_instituicao RENAME permitido_setores_convenio TO setores_convenio;

/*
ALTER TABLE tb_remessa RENAME status_remessa TO status_download;
ALTER TABLE audit_tb_remessa RENAME status_remessa TO status_download;

ALTER TABLE tb_status_arquivo RENAME situacao_arquivo TO status_download;
ALTER TABLE audit_tb_status_arquivo RENAME situacao_arquivo TO status_download;
*/

ALTER TABLE tb_layout_empresa RENAME tipo_arquivo TO layout_arquivo;
ALTER TABLE audit_tb_layout_empresa RENAME tipo_arquivo TO layout_arquivo;

/****
RODAR DEPOIS
*/
UPDATE tb_cra_service_config AS conf SET status=true WHERE conf.ativo='SIM';
UPDATE audit_tb_cra_service_config AS conf SET status=false WHERE conf.ativo='NAO' OR conf.ativo=NULL;
ALTER TABLE tb_cra_service_config DROP COLUMN ativo;
ALTER TABLE audit_tb_cra_service_config DROP COLUMN ativo;

UPDATE tb_remessa SET status_download=status_remessa;
UPDATE audit_tb_remessa SET status_download=status_remessa;

UPDATE tb_status_arquivo SET status_download=situacao_arquivo;
UPDATE audit_tb_status_arquivo SET status_download=situacao_arquivo;


==================================================================================================================================

cod:1
descricao:CRIAÇÃO DE CHAVE COMPOSTA NA TABELA DE REGISTROS CNP E FILIADOS
dependencia:0

ALTER TABLE tb_registro_cnp 
ADD	CONSTRAINT comp_pk_tb_registro_cnp 
UNIQUE (tipo_registro_cnp, cidade_devedor, numero_protocolo_cartorio, numero_documento_devedor, digito_controle_documento_devedor);

ALTER TABLE tb_filiado 
ADD	CONSTRAINT comp_pk_tb_filiado_instituicao 
UNIQUE (instituicao_id, cpf_cnpj);