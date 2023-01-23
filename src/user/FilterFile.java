/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class FilterFile extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".txt");
    }

    @Override
    public String getDescription() {
        return ".txt";
    }
}
