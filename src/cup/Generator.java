/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class Generator {

    public static void main(String[] args) {
        String rootPath = Paths.get("").toAbsolutePath().toString();
        String subPath = "/src/cup/";

        String file = rootPath + subPath + "lalg.cup";

        try {
            java_cup.Main.main(new String[]{"-package", "cup", file});
            Files.move(Paths.get(rootPath, "/parser.java"), Paths.get(rootPath + subPath, "/parser.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.move(Paths.get(rootPath, "/sym.java"), Paths.get(rootPath + subPath, "/sym.java"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
