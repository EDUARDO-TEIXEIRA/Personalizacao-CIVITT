create or replace PROCEDURE "PRO_CVT_DEL_OCOR_RASTREIO" (
       P_CODUSU NUMBER,       
       P_IDSESSAO VARCHAR2,   
       P_QTDLINHAS NUMBER,    
       P_MENSAGEM OUT VARCHAR2 
) AS
       FIELD_CHNFE           VARCHAR(44);   
       FIELD_SEQOCOR         INT;
       V_VERIFICAOCOR        INT;
       FIELD_TRANSPORTADORA  VARCHAR(99);
       V_ALTERAR             BOOLEAN;
       FIELD_CODOCORRENCIA   INT; 

BEGIN       
       FIELD_CHNFE          := ACT_TXT_FIELD(P_IDSESSAO,1,  'CHNFE');
       FIELD_TRANSPORTADORA := ACT_TXT_FIELD(P_IDSESSAO,1,  'TRANSPORTADORA');

       V_ALTERAR :=  ACT_CONFIRMAR('Exclusão de registros', 
                                   'Esta operação é irreverssível, deseja continuar?', P_IDSESSAO, 1);
                                   
       IF P_QTDLINHAS = 0 THEN
          RAISE_APPLICATION_ERROR(-20000, FC_FORMATAHTML(NULL,'Selecione uma ocorrência a ser excluída.', NULL));       
       END IF; 
       
       FOR I IN 1..P_QTDLINHAS LOOP    
       FIELD_SEQOCOR        := ACT_INT_FIELD(P_IDSESSAO, I,  'SEQOCOR');
       FIELD_CODOCORRENCIA  := ACT_INT_FIELD(P_IDSESSAO, I,  'CODOCORRENCIA');

           SELECT COUNT(1)
             INTO V_VERIFICAOCOR
             FROM AD_ACENTREGA
            WHERE CHNFE = FIELD_CHNFE
              AND NVL(CODOCORRENCIA,0) != 9999;
           
           IF V_VERIFICAOCOR > 0 THEN
              RAISE_APPLICATION_ERROR(-20000, 
                                      FC_FORMATAHTML('Alteração de Registro - Operação não permitida',
                                      'o rastreio é automático realizado pela transportadora '|| FIELD_TRANSPORTADORA|| '.', 
                                      NULL));
           END IF;
           
           DELETE FROM AD_ACENTREGA 
                 WHERE CHNFE = FIELD_CHNFE 
                   AND SEQOCOR = FIELD_SEQOCOR;
                   
       END LOOP; 
       
       P_MENSAGEM:='Registro atualizado com sucesso '|| FIELD_SEQOCOR|| ', chave: ' || FIELD_CHNFE ;  
END;
