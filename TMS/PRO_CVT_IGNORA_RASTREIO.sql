create or replace PROCEDURE "PRO_CVT_IGNORA_RASTREIO" (
       P_CODUSU NUMBER,       
       P_IDSESSAO VARCHAR2,   
       P_QTDLINHAS NUMBER,    
       P_MENSAGEM OUT VARCHAR2 
) AS
       FIELD_CHNFE      NUMBER;   
       FIELD_SEQOCOR    INT;

BEGIN       
       FIELD_CHNFE     := ACT_TXT_FIELD(P_IDSESSAO,1,  'CHNFE');

       IF NVL(FIELD_CHNFE, '') = '' THEN
        RAISE_APPLICATION_ERROR(-20000, FC_FORMATAHTML('Alteração de Registro - Operação não permitida',
                'A funcionalidade está disponível apenas para NF-e ', NULL));
       END IF;

       UPDATE TGFCAB 
          SET AD_IGNORA_RASTREIO = 'S' 
        WHERE CHAVENFE = FIELD_CHNFE;

       P_MENSAGEM:='Registro atualizado com sucesso! ';
END;
