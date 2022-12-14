create or replace PROCEDURE "PRO_CVT_AC_ENTREGA_MANUAL" (
       P_CODUSU NUMBER,       
       P_IDSESSAO VARCHAR2,   
       P_QTDLINHAS NUMBER,    
       P_MENSAGEM OUT VARCHAR2 
) AS
       FIELD_NFE VARCHAR2(4000);
       FIELD_CODCLIENTE NUMBER;
       FIELD_CHNFE VARCHAR2(4000);
       P_OBS VARCHAR2(4000);
       P_OCOR VARCHAR2(4000);
       P_DATAOCOR VARCHAR2(4000);
       P_CHAVENFE VARCHAR2(4000);
       P_SEQUENCIA INT;
       P_CODPARC INT;
       P_VLRCORREIOS NUMBER;
BEGIN
       P_DATAOCOR    := ACT_DTA_PARAM(P_IDSESSAO,    'NEW_DTOCOR');
       P_OBS         := ACT_TXT_PARAM(P_IDSESSAO,    'NEW_OBS');
       P_OCOR        := ACT_TXT_PARAM(P_IDSESSAO,    'NEW_OCOR');
       P_VLRCORREIOS := ACT_TXT_PARAM(P_IDSESSAO,    'NEW_VLRCORREIOS');
       FIELD_NFE     := ACT_TXT_FIELD(P_IDSESSAO,1,  'NFE');
       FIELD_CHNFE   := ACT_TXT_FIELD(P_IDSESSAO,1,  'CHNFE');
       
       IF P_QTDLINHAS != 1 THEN
          RAISE_APPLICATION_ERROR(-20000, FC_FORMATAHTML(NULL,'Selecione uma nota fiscal. ', NULL));       
       END IF;

       SELECT MAX(DISTINCT(CHAVENFE)) INTO P_CHAVENFE
         FROM TGFCAB
        WHERE NUMNOTA = FIELD_NFE
          AND TIPMOV = 'V';


       SELECT NVL(MAX(SEQOCOR)+1, 0) INTO P_SEQUENCIA
         FROM AD_ACENTREGA
        WHERE CHNFE = FIELD_CHNFE;

       
       SELECT MAX(CODPARC) INTO P_CODPARC
         FROM TGFCAB
        WHERE NUMNOTA = FIELD_NFE
          AND TIPMOV = 'V'; 
        

        IF FIELD_CHNFE IS NULL THEN 
           RAISE_APPLICATION_ERROR(-20001, 'O registro selecionado não possui Chave NF-e. <div style="visibility: hidden">' || FIELD_CHNFE);
        END IF;
        
        
        INSERT INTO AD_ACENTREGA (
                    CHNFE, 
                    SEQOCOR, 
                    NFE, 
                    CODOCORRENCIA, 
                    NOMEOCOR, 
                    COMENT, 
                    DATAOCOR, 
                    STATUSOCOR, 
                    CODCLIENTE, 
                    VLRCORREIO) VALUES (FIELD_CHNFE,
                                        P_SEQUENCIA,
                                        FIELD_NFE,
                                        9999,
                                        P_OCOR,
                                        P_OBS,
                                        TO_CHAR(TO_DATE(P_DATAOCOR),'YYYY-MM-DD hh:mm:ss'),
                                        'Lançamento Manual - USUARIO INCLUSÃO'||' = '|| STP_GET_CODUSULOGADO,
                                        P_CODPARC, 
                                        P_VLRCORREIOS);
                                       
     P_MENSAGEM:='Inclusão realizada com sucesso!';
END;
