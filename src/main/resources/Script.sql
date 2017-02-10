
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