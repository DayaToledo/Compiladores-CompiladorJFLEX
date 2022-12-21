/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flex;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class Errors {
    private int row;
    private int column;
    private final int offset;
    private String error;

    public Errors(int row, int column, int offset, String error) {
        this.row = row;
        this.column = column;
        this.offset = offset;
        this.error = error;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getOffset() {
        return offset;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
