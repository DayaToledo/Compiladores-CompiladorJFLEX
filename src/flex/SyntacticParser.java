/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flex;

import cup.sym;
import java.util.ArrayList;
import user.CustomScanner;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class SyntacticParser {

    private final ArrayList<Errors> errors;
    private final ArrayList<Token> ignoredTokens = new ArrayList<>();
    private final SymbolParser tableSymbols;
    private final CustomScanner scanner;
    private Token currentToken;

    public SyntacticParser(CustomScanner scanner, ArrayList<Errors> errors) {
        this.scanner = scanner;
        currentToken = scanner.nextToken();
        this.errors = errors;
        this.tableSymbols = new SymbolParser(errors);
    }

    public void consume() {
        currentToken = scanner.nextToken();
        if (currentToken.getToken() >= 41) {
            getLexicalError(currentToken);
            consume();
        }
    }

    public void consume(Integer... tokens) {
        while (!isToken(tokens) && !isToken(sym.EOF)) {
            ignoredTokens.add(currentToken);
            consume();
        }
    }

    public boolean start() {
        validateProgram();
        return currentToken.getToken() == sym.EOF;
    }

    private boolean isToken(Integer... tokens) {
        if (tokens != null) {
            for (Integer i : tokens) {
                if (currentToken.getToken() == i) {
                    return true;
                }
            }
        }
        return false;
    }

    private void getValidateProgramError(String msg) {
        getSintaxError("Declaração de programa incorreto, espera-se " + msg);
        if (scanner.lookAhead(sym.RESERVADO_BEGIN, sym.RESERVADO_BOOLEAN, sym.RESERVADO_INTEGER, sym.RESERVADO_PROCEDURE) != sym.EOF) {
            consume(sym.RESERVADO_BEGIN, sym.RESERVADO_BOOLEAN, sym.RESERVADO_INTEGER, sym.RESERVADO_PROCEDURE);
            validateBlocoCodigo();
        } else {
            getSintaxError("Espera-se declaração de variaveis, procedimentos ou inicio do bloco de comandos");
        }
    }

    private void getSintaxError(String msgErro) {
        if (errors != null) {
            errors.add(
                    new Errors(
                            currentToken.getRow() + 1,
                            currentToken.getColumnStart(),
                            currentToken.getOffset(),
                            msgErro
                    )
            );
        }
    }

    public ArrayList<Token> getTokenIgnorados() {
        return ignoredTokens;
    }

    private void getLexicalError(Token currentToken) {
        if (errors != null) {
            errors.add(
                    new Errors(
                            currentToken.getRow() + 1,
                            currentToken.getColumnStart(),
                            currentToken.getOffset(),
                            "Erro léxico: " + currentToken.getTokenName()
                    )
            );
        }
    }

    private void validateProgram() {
        if (isToken(sym.RESERVADO_PROGRAM)) {
            consume();
        } else {
            getValidateProgramError("program");
            return;
        }
        if (isToken(sym.IDENTIFICADOR)) {
            tableSymbols.addSimbolo(currentToken, SymbolParser.CATEGORIA_PROGRAM, SymbolParser.TIPO_NULL);
            consume();
        } else {
            getValidateProgramError("identificador");
            return;
        }
        if (isToken(sym.PONTO_VIRGULA)) {
            consume();
        } else {
            getValidateProgramError("';'");
            return;
        }
        validateBlocoCodigo();
        if (isToken(sym.PONTO)) {
            consume();
        } else {
            getSintaxError("Espera-se ponto");
        }
        if (isToken(sym.EOF)) {
            consume();
        } else {
            getSintaxError("Espera-se fim de arquivo");
        }
    }

    private void validateBlocoCodigo() {
        if (isToken(sym.RESERVADO_BOOLEAN, sym.RESERVADO_INTEGER)) {
            validateListaDeclaraVariavel();
            validateAposListaDeclaraVariavel();
        } else {
            if (isToken(sym.RESERVADO_PROCEDURE)) {
                validateListaDeclaraProcedure();
                validateCompandoComposto();
            } else {
                if (isToken(sym.RESERVADO_BEGIN)) {
                    validateCompandoComposto();
                } else {
                    getSintaxError("Espera-se declaração de variaveis, procedimentos ou inicio do bloco de comandos");
                    consume(sym.RESERVADO_BOOLEAN, sym.RESERVADO_INTEGER, sym.RESERVADO_PROCEDURE, sym.RESERVADO_BEGIN, sym.PONTO);
                    if (isToken(sym.PONTO)) {
                        return;
                    }
                    validateBlocoCodigo();
                }
            }
        }
    }

    private void validateListaDeclaraVariavel() {
        if (isToken(sym.RESERVADO_INTEGER, sym.RESERVADO_BOOLEAN)) {
            validateDeclaraVariavel();
        }
        if (isToken(sym.PONTO_VIRGULA)) {
            consume();
        } else {
            getSintaxError("Espera-se ';' na expressão");
            consume(sym.RESERVADO_BOOLEAN, sym.RESERVADO_INTEGER, sym.RESERVADO_BEGIN, sym.RESERVADO_PROCEDURE);
        }
        validateListaDeclaraVariavel2();
    }

    public void validateListaDeclaraVariavel2() {
        if (isToken(sym.RESERVADO_BOOLEAN, sym.RESERVADO_INTEGER)) {
            validateListaDeclaraVariavel();
        }
    }

    private void validateDeclaraVariavel() {
        int type = validateTipo();
        ArrayList<Token> tokens = validateListaVariaveis();
        for (Token t : tokens) {
            tableSymbols.addSimbolo(t, SymbolParser.CATEGORIA_VARIAVEL, type);
        }
    }

    private ArrayList<Token> validateListaVariaveis() {
        ArrayList<Token> t = new ArrayList<>();
        if (isToken(sym.IDENTIFICADOR)) {
            t.add(currentToken);
            consume();
        } else {
            getSintaxError("Espera-se um identificador");
        }
        while (isToken(sym.VIRGULA)) {
            consume();
            if (isToken(sym.IDENTIFICADOR)) {
                t.add(currentToken);
                consume();
            } else {
                getSintaxError("Espera-se um identificador");
            }
        }
        return t;
    }

    private void validateAposListaDeclaraVariavel() {
        if (isToken(sym.RESERVADO_PROCEDURE)) {
            validateListaDeclaraProcedure();
            if (!isToken(sym.RESERVADO_BEGIN)) {
                getSintaxError("Espera-se PROCEDURE ou BEGIN");
                consume(sym.PONTO);
                return;
            }
            validateCompandoComposto();
        } else {
            if (isToken(sym.RESERVADO_BEGIN)) {
                validateCompandoComposto();
            } else {
                getSintaxError("Espera-se PROCEDURE ou BEGIN");
                consume(sym.PONTO);
            }
        }
    }

    private void validateListaDeclaraProcedure() {
        validateDeclaraProcedure();
        if (isToken(sym.PONTO_VIRGULA)) {
            consume();
        } else {
            getSintaxError("Espera-se ';' na expressão");
            consume(sym.RESERVADO_PROCEDURE, sym.RESERVADO_BEGIN);
        }
        validateListaDeclaraProc2();
    }

    private void validateCompandoComposto() {
        if (isToken(sym.RESERVADO_BEGIN)) {
            consume();
        } else {
            getSintaxError("Espera-se begin");
            consume(sym.IDENTIFICADOR, sym.RESERVADO_BEGIN, sym.RESERVADO_WHILE, sym.RESERVADO_IF, sym.RESERVADO_END, sym.PONTO_VIRGULA);
            if (isToken(sym.RESERVADO_END)) {
                consume();
                return;
            }
            if (isToken(sym.PONTO_VIRGULA)) {
                consume();
            }
        }
        validateComando();
        validateListaComandos();
        if (isToken(sym.RESERVADO_END)) {
            consume();
        } else {
            getSintaxError("Espera-se END");
            consume(sym.RESERVADO_ELSE, sym.PONTO_VIRGULA, sym.PONTO, sym.RESERVADO_END);
        }
    }

    private void validateDeclaraProcedure() {
        if (isToken(sym.RESERVADO_PROCEDURE)) {
            consume();
        } else {
            getSintaxError("Espera-se procedure");
            consume(sym.IDENTIFICADOR, sym.PONTO_VIRGULA, sym.ABRE_PARENTESES);
            if (isToken(sym.ABRE_PARENTESES, sym.PONTO_VIRGULA)) {
                validateDeclaraProc2();
                return;
            }
        }
        if (isToken(sym.IDENTIFICADOR)) {
            Token t = currentToken;
            tableSymbols.addSimbolo(t, SymbolParser.CATEGORIA_PROCEDURE, SymbolParser.TIPO_NULL);
            consume();
        }
        validateDeclaraProc2();
    }

    private void validateDeclaraProc2() {
        if (isToken(sym.ABRE_PARENTESES)) {
            validateProcParametros();
        }
        if (isToken(sym.PONTO_VIRGULA)) {
            consume();
        } else {
            consume(sym.RESERVADO_VAR, sym.RESERVADO_BEGIN, sym.PONTO_VIRGULA);
            if (isToken(sym.PONTO_VIRGULA)) {
                consume();
            }
            if (isToken(sym.RESERVADO_VAR)) {
                validateProcParametros();
                if (isToken(sym.PONTO_VIRGULA)) {
                    consume();
                } else {
                    getSintaxError("Espera-se ';' na expressão");
                    consume(sym.RESERVADO_BEGIN, sym.PONTO_VIRGULA);
                    if (isToken(sym.PONTO_VIRGULA)) {
                        return;
                    }
                }
            } else {
                getSintaxError("Espera-se ';' ou '(' na expressão");
            }
        }
        validateDeclaraProc3();
    }

    private void validateProcParametros() {
        if (isToken(sym.ABRE_PARENTESES)) {
            consume();
        } else {
            getSintaxError("Espera-se '(' na expressão");
            consume(sym.RESERVADO_VAR, sym.PONTO_VIRGULA, sym.FECHA_PARENTESES);
            if (isToken(sym.PONTO_VIRGULA)) {
                validateProcListaParametros2();
            }
            if (isToken(sym.FECHA_PARENTESES)) {
                consume();
                return;
            }
        }
        validateProcListaParametros();
        validateProcListaParametros2();
        if (isToken(sym.FECHA_PARENTESES)) {
            consume();
        } else {
            getSintaxError("Espera-se ')' na expressão");
            consume(sym.PONTO_VIRGULA);
        }
    }

    private void validateDeclaraProc3() {
        if (isToken(sym.RESERVADO_BOOLEAN, sym.RESERVADO_INTEGER)) {
            validateListaDeclaraVariavel();
        }
        validateCompandoComposto();
        tableSymbols.removeCategoriaVariavelLocal();
    }

    private void validateProcListaParametros() {
        if (isToken(sym.RESERVADO_VAR)) {
            consume();
        }
        ArrayList<Token> ids = validateListaVariaveis();
        int type;
        if (isToken(sym.DOIS_PONTOS)) {
            consume();
        } else {
            getSintaxError("Espera-se ':' na expressão");
            consume(sym.RESERVADO_BOOLEAN, sym.RESERVADO_INTEGER, sym.PONTO_VIRGULA);
            if (isToken(sym.PONTO_VIRGULA)) {
                getSintaxError("Espera-se um tipo");
                return;
            }
        }
        if (isToken(sym.RESERVADO_INTEGER, sym.RESERVADO_BOOLEAN)) {
            type = validateTipo();
        } else {
            getSintaxError("Espera-se boolean ou int");
            consume(sym.PONTO_VIRGULA, sym.FECHA_PARENTESES);
            return;
        }
        for (Token t : ids) {
            tableSymbols.addSimbolo(t, SymbolParser.CATEGORIA_PARAMETROS, type);
        }
    }

    private void validateProcListaParametros2() {
        if (isToken(sym.PONTO_VIRGULA)) {
            consume();
            validateProcListaParametros();
            validateProcListaParametros2();
        }
    }

    private int validateTipo() {
        if (isToken(sym.RESERVADO_BOOLEAN)) {
            consume();
            return SymbolParser.TIPO_BOOLEAN;
        }
        if (isToken(sym.RESERVADO_INTEGER)) {
            consume();
            return SymbolParser.TIPO_INT;
        }
        getSintaxError("Espera-se boolean ou int");
        consume(sym.IDENTIFICADOR, sym.PONTO_VIRGULA, sym.FECHA_PARENTESES);
        return SymbolParser.TIPO_NULL;
    }

    private void validateListaDeclaraProc2() {
        if (isToken(sym.RESERVADO_PROCEDURE)) {
            validateListaDeclaraProcedure();
        }
    }

    private void validateComando() {
        if (isToken(sym.IDENTIFICADOR)) {
            validateComando2();
        } else {
            if (isToken(sym.RESERVADO_BEGIN)) {
                validateCompandoComposto();
            } else if (isToken(sym.RESERVADO_IF)) {
                validateComandoCondicional();
            } else if (isToken(sym.RESERVADO_WHILE)) {
                validateComandoRepetitivo();
            } else {
                getSintaxError("Espera-se um comando");
                consume(sym.RESERVADO_END, sym.PONTO_VIRGULA);
            }
        }
    }

    private void validateListaComandos() {
        if (isToken(sym.PONTO_VIRGULA)) {
            consume();
            validateComando();
            validateListaComandos();
        }
    }

    private void validateComandoCondicional() {
        if (isToken(sym.RESERVADO_IF)) {
            consume();
        }
        int type = validateExpressao();
        if (type != SymbolParser.TIPO_BOOLEAN) {
            getSintaxError("A expressão contida na condição do IF deve ser do tipo booleano");
        }
        if (isToken(sym.RESERVADO_THEN)) {
            consume();
        } else {
            getSintaxError("Espera-se THEN na expressão");
            consume(sym.IDENTIFICADOR, sym.RESERVADO_BEGIN, sym.RESERVADO_WHILE, sym.RESERVADO_IF,
                    sym.RESERVADO_ELSE,
                    sym.PONTO_VIRGULA, sym.RESERVADO_END);
            if (isToken(sym.RESERVADO_ELSE)) {
                validateComandoCondicional2();
                return;
            }
            if (isToken(sym.PONTO_VIRGULA, sym.RESERVADO_END)) {
                return;
            }
        }
        validateComando();
        validateComandoCondicional2();
    }

    private void validateComandoCondicional2() {
        if (isToken(sym.RESERVADO_ELSE)) {
            consume();
            validateComando();
        }
    }

    private void validateComandoRepetitivo() {
        if (isToken(sym.RESERVADO_WHILE)) {
            consume();
        }
        int type = validateExpressao();
        if (type != SymbolParser.TIPO_BOOLEAN) {
            getSintaxError("A expressão contida na condição do WHILE deve ser do tipo booleano");
        }
        if (isToken(sym.RESERVADO_DO)) {
            consume();
        } else {
            getSintaxError("Espera-se DO na expressão");
            consume(sym.IDENTIFICADOR, sym.RESERVADO_BEGIN, sym.RESERVADO_WHILE, sym.RESERVADO_IF,
                    sym.RESERVADO_ELSE, sym.PONTO_VIRGULA, sym.RESERVADO_END);
            if (isToken(sym.RESERVADO_ELSE, sym.PONTO_VIRGULA, sym.RESERVADO_END)) {
                return;
            }
        }
        validateComando();
    }

    private void validateComando2() {
        Token id = currentToken;
        consume();
        if (isToken(sym.SIMBOLO_ATRIBUICAO)) {
            int type = tableSymbols.searchSimbolo(id, SymbolParser.CATEGORIA_VARIAVEL);
            consume();
            int typeSecondary = validateAtribuicao();
            if (type != typeSecondary && type != SymbolParser.TIPO_NULL && typeSecondary != SymbolParser.TIPO_NULL) {
                getSintaxError("Atribuição com tipos conflitantes");
            }
        } else {
            if (isToken(sym.ABRE_PARENTESES)) {
                tableSymbols.searchSimbolo(id, SymbolParser.CATEGORIA_PROCEDURE);
                if (id.getLexeme().equals("read")) {
                    validateChamadaFuncao2(id);
                } else {
                    validateChamadaFuncao(id);
                }
            } else {
                getSintaxError("Espera-se ':=' ou '(' na expressão");
                consume(sym.RESERVADO_ELSE, sym.PONTO_VIRGULA, sym.RESERVADO_END);
            }
        }
    }

    private int validateExpressao() {
        int typePrimary = validateExpressaoSimples();
        boolean opEntreInt = false;
        if (isToken(sym.OPERACAO_IGUAL, sym.OPERACAO_MAIOR, sym.OPERACAO_MAIOR_IGUAL, sym.OPERACAO_MENOR,
                sym.OPERACAO_MENOR_IGUAL, sym.OPERACAO_DIFERENTE)) {
            if (!isToken(sym.OPERACAO_IGUAL, sym.OPERACAO_DIFERENTE)) {
                opEntreInt = true;
            }
            consume();
            int typeSecondary = validateExpressaoSimples();
            if (typePrimary != typeSecondary) {
                getSintaxError("Operação de comparação deve ser entre tipos iguais");
            } else if (opEntreInt && (typePrimary != SymbolParser.TIPO_INT || typeSecondary != SymbolParser.TIPO_INT)) {
                getSintaxError("Operação de comparação deve ser entre tipos inteiros");
            }
            return SymbolParser.TIPO_BOOLEAN;
        }
        return typePrimary;
    }

    private int validateAtribuicao() {
        int type = validateExpressao();
        return type;
    }

    private void validateChamadaFuncao(Token idProcedimento) {
        if (isToken(sym.ABRE_PARENTESES)) {
            consume();
            ArrayList<Integer> tipos = validateListaExpressoes();
            tableSymbols.parametrosChamadaProcedimento(tipos, idProcedimento);
            if (isToken(sym.FECHA_PARENTESES)) {
                consume();
            } else {
                getSintaxError("Espera-se ')' na expressão");
                consume(sym.RESERVADO_ELSE, sym.PONTO_VIRGULA, sym.RESERVADO_END);
            }
        } else {
            if (isToken(sym.RESERVADO_ELSE, sym.RESERVADO_END, sym.PONTO_VIRGULA)) {
            }
        }
    }

    private void validateChamadaFuncao2(Token idProcedimento) {
        if (isToken(sym.ABRE_PARENTESES)) {
            consume();
            ArrayList<Integer> tipos = new ArrayList<>();
            if (isToken(sym.IDENTIFICADOR)) {
                tipos.add(tableSymbols.searchSimbolo(currentToken, SymbolParser.CATEGORIA_VARIAVEL));
                consume();
                while (isToken(sym.VIRGULA)) {
                    consume();
                    if (isToken(sym.IDENTIFICADOR)) {
                        tipos.add(tableSymbols.searchSimbolo(currentToken, SymbolParser.CATEGORIA_VARIAVEL));
                        consume();
                    } else {
                        getSintaxError("Espera-se um identificador");
                        consume(sym.FECHA_PARENTESES, sym.RESERVADO_ELSE, sym.PONTO_VIRGULA, sym.RESERVADO_END);
                    }
                }
                tableSymbols.parametrosChamadaProcedimento(tipos, idProcedimento);
            } else {
                getSintaxError("Espera-se um identificador");
                consume(sym.FECHA_PARENTESES, sym.RESERVADO_ELSE, sym.PONTO_VIRGULA, sym.RESERVADO_END);
            }
            if (isToken(sym.FECHA_PARENTESES)) {
                consume();
            } else {
                getSintaxError("Espera-se ')' na expressão");
                consume(sym.RESERVADO_ELSE, sym.PONTO_VIRGULA, sym.RESERVADO_END);
            }
        }
    }

    private int validateExpressaoSimples() {
        if (isToken(sym.OPERACAO_SOMA, sym.OPERACAO_SUB)) {
            consume();
        }
        int typePrimary = validateTermo();
        int typeOperation;
        int typeSecondary;
        while (isToken(sym.OPERACAO_SOMA, sym.OPERACAO_SUB, sym.OPERACAO_OR)) {
            if (isToken(sym.OPERACAO_OR)) {
                typeOperation = SymbolParser.TIPO_BOOLEAN;
            } else {
                typeOperation = SymbolParser.TIPO_INT;
            }
            consume();
            typeSecondary = validateTermo();

            boolean erro = false;
            if (typePrimary != typeOperation && typePrimary != SymbolParser.TIPO_NULL) {
                erro = true;
            }
            if (typeSecondary != typeOperation && typeSecondary != SymbolParser.TIPO_NULL) {
                erro = true;
            }
            if (erro) {
                if (typeOperation == SymbolParser.TIPO_BOOLEAN) {
                    getSintaxError("Operação OR deve ser entre boleanos");
                } else {
                    getSintaxError("Operação númerica deve ser entre números inteiros");
                }
            }
        }
        return typePrimary;
    }

    private ArrayList<Integer> validateListaExpressoes() {
        ArrayList<Integer> tipos = new ArrayList<>();
        tipos.add(validateExpressao());
        while (isToken(sym.VIRGULA)) {
            consume();
            tipos.add(validateExpressao());
        }
        return tipos;
    }

    private int validateTermo() {
        int typePrimary = validateFator();
        int typeSecondary;
        int typeOperation;
        while (isToken(sym.OPERACAO_DIV, sym.OPERACAO_AND, sym.OPERACAO_MULT)) {
            if (isToken(sym.OPERACAO_AND)) {
                typeOperation = SymbolParser.TIPO_BOOLEAN;
            } else {
                typeOperation = SymbolParser.TIPO_INT;
            }
            consume();
            typeSecondary = validateFator();

            boolean erro = false;
            if (typePrimary != typeOperation && typePrimary != SymbolParser.TIPO_NULL) {
                erro = true;
            }
            if (typeSecondary != typeOperation && typeSecondary != SymbolParser.TIPO_NULL) {
                erro = true;
            }
            if (erro) {
                if (typeOperation == SymbolParser.TIPO_BOOLEAN) {
                    getSintaxError("Operação AND deve ser entre booleanos");
                } else {
                    getSintaxError("Operação númerica deve ser entre números inteiros");
                }
            }
        }
        return typePrimary;
    }

    private int validateFator() {
        int type = SymbolParser.TIPO_NULL;
        if (isToken(sym.IDENTIFICADOR)) {
            type = validateVariavel();
        } else if (isToken(sym.NUMERO_INTEIRO)) {
            type = SymbolParser.TIPO_INT;
            consume();
        } else if (isToken(sym.RESERVADO_FALSE, sym.RESERVADO_TRUE)) {
            type = SymbolParser.TIPO_BOOLEAN;
            consume();
        } else if (isToken(sym.ABRE_PARENTESES)) {
            consume();
            type = validateExpressao();
            if (isToken(sym.FECHA_PARENTESES)) {
                consume();
            } else {
                getSintaxError("Espera-se ')' na expressão");
                consume(sym.OPERACAO_DIV, sym.OPERACAO_AND, sym.OPERACAO_MULT, sym.OPERACAO_SOMA, sym.OPERACAO_IGUAL, sym.OPERACAO_DIFERENTE,
                        sym.OPERACAO_MENOR_IGUAL, sym.OPERACAO_MENOR, sym.OPERACAO_MAIOR_IGUAL, sym.OPERACAO_MAIOR,
                        sym.VIRGULA, sym.FECHA_PARENTESES, sym.RESERVADO_DO, sym.RESERVADO_THEN, sym.RESERVADO_ELSE,
                        sym.PONTO_VIRGULA, sym.RESERVADO_END);
            }
        } else if (isToken(sym.OPERACAO_NOT)) {
            consume();
            type = validateFator();
            if (type != SymbolParser.TIPO_BOOLEAN) {
                if (type != SymbolParser.TIPO_NULL) {
                    getSintaxError("Operação NOT com um valor não boleano");
                }
                type = SymbolParser.TIPO_BOOLEAN;
            }
        } else {
            getSintaxError("Espera-se um identificador, número inteiro, '(', NOT, FALSE ou TRUE");
            consume(sym.OPERACAO_DIV, sym.OPERACAO_AND, sym.OPERACAO_MULT, sym.OPERACAO_SOMA, sym.OPERACAO_IGUAL, sym.OPERACAO_DIFERENTE,
                    sym.OPERACAO_MENOR_IGUAL, sym.OPERACAO_MENOR, sym.OPERACAO_MAIOR_IGUAL, sym.OPERACAO_MAIOR,
                    sym.VIRGULA, sym.FECHA_PARENTESES, sym.RESERVADO_DO, sym.RESERVADO_THEN, sym.RESERVADO_ELSE,
                    sym.PONTO_VIRGULA, sym.RESERVADO_END);
        }
        return type;
    }

    private int validateVariavel() {
        int type = SymbolParser.TIPO_NULL;
        if (isToken(sym.IDENTIFICADOR)) {
            type = tableSymbols.searchSimbolo(currentToken, SymbolParser.CATEGORIA_VARIAVEL);
            consume();
        }
        return type;
    }
}
