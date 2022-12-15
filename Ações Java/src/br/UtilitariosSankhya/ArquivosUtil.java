package br.UtilitariosSankhya;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public abstract class ArquivosUtil  {
	
public static File gerarArquivo(Clob xml) throws Exception {
    // Converte o CLOB para String
    String tempString = xml.getSubString(1, (int) xml.length());

    // Cria o arquivo
    File tempFile = File.createTempFile("temp", ".xml");

    // Escreve conteúdo da String para o arquivo
    BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
    bw.write(tempString);
    bw.close();

    return tempFile;
}

public static String nomeZip() {
	Calendar calendario = Calendar.getInstance();
	calendario.setTime(new Date());
	String ano = String.format("%04d", calendario.get(Calendar.YEAR));
	String mes = String.format("%02d", calendario.get(Calendar.MONTH) + 1);
	String dia = String.format("%02d", calendario.get(Calendar.DAY_OF_MONTH));

	// Nome do arquivo ZIP
	return  String.format("CIVITT-ZIP_%s-%s-%s.txt", ano, mes, dia);
}
public static File compactarZip (List<File> arquivos) throws Exception {

   
    // Crie o arquivo ZIP arquivo temporario aqui
    File zip = File.createTempFile (nomeZip(), "tmp" );
	    // Compactacao dos arquivos
    try (ZipOutputStream zipOutputStream = new ZipOutputStream (new FileOutputStream (zip))) {

        for (File arquivo : arquivos) {
            zipOutputStream.putNextEntry (new ZipEntry (arquivo.getName ()));
            zipOutputStream.write (Files.readAllBytes (arquivo.toPath ()));
            zipOutputStream.closeEntry ();
        }
    }
    return zip;
}
}
