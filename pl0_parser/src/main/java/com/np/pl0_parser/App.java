/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.np.pl0_parser;

import com.np.pl0_parser.parser.PLCompiler;
import com.np.pl0_parser.parser.Parser;
import com.np.pl0_parser.parser.Translator;
import com.np.pl0_parser.pojo.ParserException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.script.ScriptException;

/**
 *
 * @author npetrov_2
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParserException, ScriptException {
        String programCode = readFile("input.txt", Charset.forName("utf-8"));
        Parser parser = new Parser();
        List<String> symbols = parser.parse(programCode);
        Translator translator = new Translator();
        String translateRes = translator.translate(symbols);
        FileWriter writer = new FileWriter("res.js");
        writer.write(translateRes);
        writer.close();
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
