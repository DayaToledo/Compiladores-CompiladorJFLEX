/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package syntacticAndSemantic;

import lexicon.Token;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class SymbolParser {

    public static final int VARIAVEL_NAO_ENCONTRADA = -1;

    public static final int CATEGORIA_VARIAVEL = 1;
    public static final int CATEGORIA_PROCEDURE = 2;
    public static final int CATEGORIA_PARAMETROS = 3;
    public static final int CATEGORIA_PROGRAM = 4;

    public static final int TIPO_INT = 1;
    public static final int TIPO_BOOLEAN = 2;
    public static final int TIPO_NULL = 4;
    public static final int TIPO_READ = -1;
    public static final int TIPO_WRITE = -1;

    private HashMap<KeyTableSymbols, ValueTableSymbols> tableSymbolsGlobal;
    private ArrayList<LinkedHashMap<KeyTableSymbols, ValueTableSymbols>> tableProcedures;
    private LinkedHashMap<KeyTableSymbols, ValueTableSymbols> procedure;
    private ArrayList<Errors> errors;
    private int currentAddress;

    public SymbolParser(ArrayList<Errors> errors) {
        this.tableSymbolsGlobal = new HashMap();
        this.tableProcedures = new ArrayList();
        procedure = null;
        this.errors = errors;
        this.currentAddress = 0;
        init();
    }

    private void init() {
        ValueTableSymbols read = new ValueTableSymbols("read", CATEGORIA_PROCEDURE, TIPO_READ);
        ValueTableSymbols write = new ValueTableSymbols("write", CATEGORIA_PROCEDURE, TIPO_WRITE);
        tableSymbolsGlobal.put(read.getKey(), read);
        tableSymbolsGlobal.put(write.getKey(), write);
    }

    public boolean addSimbolo(Token t, int category, int tipo) {
        boolean erro = false;
        ValueTableSymbols v = new ValueTableSymbols(t, category, tipo, currentAddress);
        KeyTableSymbols k = v.getKey();

        if (procedure != null) {
            if (procedure.containsKey(k)) {
                errors.add(new Errors(t.getRow(), t.getColumnStart(), t.getOffset(), getMsgErrorAdd(category)));
                erro = true;
            } else {
                procedure.put(k, v);
            }
        } else {
            if (tableSymbolsGlobal.containsKey(k)) {
                errors.add(new Errors(t.getRow(), t.getColumnStart(), t.getOffset(), getMsgErrorAdd(category)));
                erro = true;
            } else {
                if (category == CATEGORIA_PROCEDURE) {
                    v.tipo = tableProcedures.size();
                    procedure = new LinkedHashMap<>();
                    tableProcedures.add(procedure);
                }
                tableSymbolsGlobal.put(k, v);
            }
        }
        if (category == CATEGORIA_VARIAVEL) {
            currentAddress++;
        }
        return erro;
    }

    public int searchSimbolo(Token t, int category) {
        ValueTableSymbols v = new ValueTableSymbols(t, category, TIPO_NULL, 0);
        KeyTableSymbols k = v.getKey();
        if (procedure != null) {
            if (procedure.containsKey(k)) {
                return procedure.get(k).tipo;
            }
        }
        if (!tableSymbolsGlobal.containsKey(k)) {
            errors.add(new Errors(t.getRow(), t.getColumnStart(), t.getOffset(), getMsgErrorSearch(category)));
            return TIPO_NULL;
        } else {
            return tableSymbolsGlobal.get(k).tipo;
        }
    }

    public void removeCategoriaVariavelLocal() {
        if (procedure != null) {
            Collection<ValueTableSymbols> values = procedure.values();
            Iterator<ValueTableSymbols> iterator = values.iterator();
            while (iterator.hasNext()) {
                ValueTableSymbols value = iterator.next();
                if (value.category == CATEGORIA_VARIAVEL) {
                    iterator.remove();
                }
            }
        }
        procedure = null;
    }

    public void parametrosChamadaProcedimento(ArrayList<Integer> tipos, Token idProcedimento) {
        KeyTableSymbols k = new KeyTableSymbols(idProcedimento.getLexeme(), CATEGORIA_PROCEDURE);
        ValueTableSymbols v = tableSymbolsGlobal.get(k);
        if (v == null) {
            return;
        }
        if (v.tipo == TIPO_READ || v.tipo == TIPO_WRITE) {
            for (int i = 1; i < tipos.size(); i++) {
                if (!tipos.get(i).equals(tipos.get(i - 1))) {
                    errors.add(new Errors(idProcedimento.getRow(), idProcedimento.getColumnStart(), idProcedimento.getOffset(), "Read/Write com parametros de tipo diferentes"));
                    return;
                }
            }
            return;
        }
        procedure = tableProcedures.get(v.tipo);
        Iterator iterator = procedure.values().iterator();
        int cont = 0;
        while (iterator.hasNext() && cont < tipos.size()) {
            ValueTableSymbols entry = (ValueTableSymbols) iterator.next();
            if (entry.category != CATEGORIA_PARAMETROS) {
                continue;
            }
            if (entry.tipo != tipos.get(cont)) {
                errors.add(new Errors(idProcedimento.getRow(), idProcedimento.getColumnStart(), idProcedimento.getOffset(), "O tipo do parametro formal e real diferem"));
            }
            cont++;
        }
        if (cont != tipos.size()) {
            errors.add(new Errors(idProcedimento.getRow(), idProcedimento.getColumnStart(), idProcedimento.getOffset(), "Quantidade de parametros formal e real diferem"));
        }
    }

    private String getMsgErrorSearch(int category) {
        switch (category) {
            case CATEGORIA_PROCEDURE -> {
                return "Procedimento não declarado";
            }
            case CATEGORIA_PARAMETROS, CATEGORIA_VARIAVEL -> {
                return "Variavel não declarada";
            }
        }
        return "";
    }

    private String getMsgErrorAdd(int category) {
        switch (category) {
            case CATEGORIA_PARAMETROS -> {
                return "Parâmetro já declarado.";
            }
            case CATEGORIA_PROCEDURE -> {
                return "Procedimento já declarado.";
            }
            case CATEGORIA_VARIAVEL -> {
                return "Variavel já decladara.";
            }
        }
        return "";

    }

    public Integer addressSymbol(Token t) {
        KeyTableSymbols key = new KeyTableSymbols(t.getLexeme(), CATEGORIA_VARIAVEL);
        ValueTableSymbols value = null;
        if (procedure != null) {
            value = procedure.get(key);
        }
        if (value == null) {
            value = tableSymbolsGlobal.get(key);
        }
        if (value != null) {
            return value.endereco;
        } else {
            return VARIAVEL_NAO_ENCONTRADA;
        }
    }

    public int getCurrentAddress() {
        return currentAddress;
    }

    private class ValueTableSymbols {

        public String lexeme;
        public int category;
        public int tipo;
        public int valor;
        public boolean utilizada;
        public int endereco;

        public ValueTableSymbols(Token t, int category, int tipo, int endereco) {
            lexeme = t.getLexeme();
            this.category = category;
            this.tipo = tipo;
            this.utilizada = false;
            this.endereco = endereco;
        }

        public ValueTableSymbols(String lexeme, int category, int tipo) {
            this.lexeme = lexeme;
            this.category = category;
            this.tipo = tipo;
            this.utilizada = false;
        }

        public KeyTableSymbols getKey() {
            return new KeyTableSymbols(lexeme, category);
        }
    }

    private class KeyTableSymbols {

        public String lexeme;
        public int category;

        public KeyTableSymbols(String lexeme, int category) {
            this.lexeme = lexeme;
            this.category = category;
            if (this.category == CATEGORIA_PARAMETROS) {
                this.category = CATEGORIA_VARIAVEL;
            }
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
            final KeyTableSymbols other = (KeyTableSymbols) obj;
            if (this.category != other.category) {
                return false;
            }
            return Objects.equals(this.lexeme, other.lexeme);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 71 * hash + this.lexeme.hashCode();
            hash = 71 * hash + (this.category);
            return hash;
        }
    }
}
