/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package flex;

import java.util.Objects;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class Token {

    public static void main(String[] args){
        for(int i=0; i < tokensNames.length;i++){
            System.out.println(i + " - " + tokensNames[i]);
        }
    }
    
    public static final String[] tokensNames = new String[]{
        "EOF",
        "error",
        "RESERVADO_PROGRAM",
        "RESERVADO_VAR",
        "RESERVADO_PROCEDURE",
        "RESERVADO_BEGIN",
        "RESERVADO_END",
        "RESERVADO_IF",
        "RESERVADO_THEN",
        "RESERVADO_ELSE",
        "RESERVADO_WHILE",
        "RESERVADO_DO",
        "RESERVADO_TRUE",
        "RESERVADO_FALSE",
        "RESERVADO_INTEGER",
        "RESERVADO_BOOLEAN",
        "PONTO_VIRGULA",
        "DOIS_PONTOS",
        "VIRGULA",
        "PONTO",
        "ABRE_PARENTESES",
        "FECHA_PARENTESES",
        "SIMBOLO_ATRIBUIÇÃO",
        "OPERACAO_MENOR",
        "OPERACAO_MAIOR",
        "OPERACAO_MENOR_IGUAL",
        "OPERACAO_MAIOR_IGUAL",
        "OPERACAO_DIFERENTE",
        "OPERACAO_IGUAL",
        "OPERACAO_SOMA",
        "OPERACAO_SUB",
        "OPERACAO_DIV",
        "OPERACAO_MULT",
        "OPERACAO_OR",
        "OPERACAO_AND",
        "OPERACAO_NOT",
        "NUMERO_REAL_FORMATO_RUIM",
        "NUMERO_INTEIRO",
        "NUMERO_REAL",
        "IDENTIFICADOR",
        "IDENTIFICADOR_MUITO_GRANDE",
        "COMENTARIO_LINHA",
        "COMENTARIO_BLOCO",
        "COMENTARIO_BLOCO_SEM_FECHAR",
        "COMENTARIO_BLOCO_SEM_ABRIR",
        "CHAR_INVALIDO"
    };

    private String lexeme;
    private int token;
    private int row;
    private int columnStart;
    private int columnEnd;
    private final int offset;

    public String getTokenName() {
        return tokensNames[token];
    }

    public Token(String lexeme, int token, int row, int columnStart, int offset) {
        this.lexeme = lexeme;
        this.token = token;
        this.row = row;
        this.columnStart = columnStart;
        this.columnEnd = columnStart + lexeme.length() - 1;
        this.offset = offset;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumnStart() {
        return columnStart;
    }

    public void setColumnStart(int columnStart) {
        this.columnStart = columnStart;
    }

    public int getColumnEnd() {
        return columnEnd;
    }

    public void setColumnEnd(int columnEnd) {
        this.columnEnd = columnEnd;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return lexeme + " -> " + token + " " + columnStart + " " + columnEnd + " " + (row+1) + " " + offset;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (this.token != other.token) {
            return false;
        }
        if (this.row != other.row) {
            return false;
        }
        if (this.columnStart != other.columnStart) {
            return false;
        }
        if (this.columnEnd != other.columnEnd) {
            return false;
        }
        if (this.offset != other.offset) {
            return false;
        }
        return Objects.equals(this.lexeme, other.lexeme);
    }
}