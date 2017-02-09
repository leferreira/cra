==================================================================================================================================

cod:1
descricao:livro indicador{CRIAÇÃO DE CHAVE PRIMARIA COMPOSTA NA TABELA DE REGISTROS CNP}
dependencia:0

ALTER TABLE tb_registro_cnp 
ADD	CONSTRAINT comp_pk_tb_registro_cnp 
UNIQUE (tipo_registro_cnp, cidade_devedor, numero_protocolo_cartorio, numero_documento_devedor, digito_controle_documento_devedor);