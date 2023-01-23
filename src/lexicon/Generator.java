/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lexicon;

import java.io.File;
import java.nio.file.Paths;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class Generator {

    public static void main(String[] args) {
        String basePath = Paths.get("").toAbsolutePath().toString();
        String localPath = "/src/lexicon/";
        String file = basePath + localPath + "flexCode.txt";

        File sourceCode = new File(file);
        jflex.Main.generate(sourceCode);
    }
}
