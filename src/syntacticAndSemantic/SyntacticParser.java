/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package syntacticAndSemantic;

import lexicon.Token;
import cup.sym;
import java.util.ArrayList;
import machineCode.MachineCode;
import user.CustomScanner;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class SyntacticParser {

    private final ArrayList<Errors> errors;
    private final ArrayList<Token> ignoredTokens = new ArrayList<>();
    private final SymbolParser tableSymbols;
    private final MachineCode machineCode = new MachineCode();
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
        if (!errors.isEmpty()) {
            machineCode.clear();
        }
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

    public MachineCode getMachineCode() {
        return machineCode;
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

    private void verifyAndSetMachineCode(Token token) {
        switch (token.getToken()) {
            case sym.OPERACAO_IGUAL:
                machineCode.addInstruction(MachineCode.COMPARAR_IGUAL, null);
                break;
            case sym.OPERACAO_DIFERENTE:
                machineCode.addInstruction(MachineCode.COMPARAR_DIFERENTE, null);
                break;
            case sym.OPERACAO_MAIOR:
                machineCode.addInstruction(MachineCode.COMPARAR_MAIOR, null);
                break;
            case sym.OPERACAO_MAIOR_IGUAL:
                machineCode.addInstruction(MachineCode.COMPARAR_MAIOR_IGUAL, null);
                break;
            case sym.OPERACAO_MENOR:
                machineCode.addInstruction(MachineCode.COMPARAR_MENOR, null);
                break;
            case sym.OPERACAO_MENOR_IGUAL:
                machineCode.addInstruction(MachineCode.COMPARAR_MENOR_IGUAL, null);
                break;
            case sym.OPERACAO_SOMA:
                machineCode.addInstruction(MachineCode.OPERADOR_SOMA, null);
                break;
            case sym.OPERACAO_SUB:
                machineCode.addInstruction(MachineCode.OPERADOR_SUB, null);
                break;
            case sym.OPERACAO_OR:
                machineCode.addInstruction(MachineCode.OPERADOR_OR, null);
                break;
            case sym.OPERACAO_DIV:
                machineCode.addInstruction(MachineCode.OPERADOR_DIV, null);
                break;
            case sym.OPERACAO_AND:
                machineCode.addInstruction(MachineCode.OPERADOR_AND, null);
                break;
            case sym.OPERACAO_MULT:
                machineCode.addInstruction(MachineCode.OPERADOR_MULT, null);
                break;
            default:
                break;
        }
    }

    private void validateProgram() {
        if (isToken(sym.RESERVADO_PROGRAM)) {
            this.machineCode.addInstruction(MachineCode.INICIAR_PROGRAMA, null);
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
        for (int i = 0; i < tableSymbols.getCurrentAddress(); i++) {
            machineCode.addInstruction(MachineCode.DESALOCAR_MEMORIA, 1);
        }
        machineCode.addInstruction(MachineCode.PARAR, null);
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
            boolean alreadyDeclared = tableSymbols.addSimbolo(t, SymbolParser.CATEGORIA_VARIAVEL, type);
            if (!alreadyDeclared) {
                machineCode.addInstruction(MachineCode.ALOCAR_MEMORIA, 1);
            }
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
        int label = machineCode.getSize();
        machineCode.addInstruction(MachineCode.DESVIAR_SE_FALSE, null);
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
        machineCode.updateInstruction(label, MachineCode.DESVIAR_SE_FALSE, machineCode.getSize() + 1);
        validateComandoCondicional2();
    }

    private void validateComandoCondicional2() {
        if (isToken(sym.RESERVADO_ELSE)) {
            int label = machineCode.getSize();
            machineCode.addInstruction(MachineCode.DESVIAR_SEMPRE, null);
            machineCode.addInstruction(MachineCode.NADA, null);
            consume();
            validateComando();
            machineCode.updateInstruction(label, MachineCode.DESVIAR_SEMPRE, machineCode.getSize());
            machineCode.addInstruction(MachineCode.NADA, null);
        }
    }

    private void validateComandoRepetitivo() {
        if (isToken(sym.RESERVADO_WHILE)) {
            consume();
        }
        int positionInitLoop = machineCode.getSize();
        machineCode.addInstruction(MachineCode.NADA, null);
        int type = validateExpressao();
        if (type != SymbolParser.TIPO_BOOLEAN) {
            getSintaxError("A expressão contida na condição do WHILE deve ser do tipo booleano");
        }
        int positionEndLoop = machineCode.getSize();
        machineCode.addInstruction(MachineCode.DESVIAR_SE_FALSE, null);
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
        machineCode.addInstruction(MachineCode.DESVIAR_SEMPRE, positionInitLoop);
        machineCode.updateInstruction(positionEndLoop, MachineCode.DESVIAR_SE_FALSE, machineCode.getSize());
        machineCode.addInstruction(MachineCode.NADA, null);
    }

    private void validateComando2() {
        Token id = currentToken;
        consume();
        if (isToken(sym.SIMBOLO_ATRIBUICAO)) {
            int type = tableSymbols.searchSimbolo(id, SymbolParser.CATEGORIA_VARIAVEL);
            consume();
            int typeSecondary = validateAtribuicao();
            machineCode.addInstruction(MachineCode.ARMAZENAR_VALOR, tableSymbols.addressSymbol(id));
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
            Token token = currentToken;
            if (!isToken(sym.OPERACAO_IGUAL, sym.OPERACAO_DIFERENTE)) {
                opEntreInt = true;
            }
            consume();
            int typeSecondary = validateExpressaoSimples();
            verifyAndSetMachineCode(token);
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

    private void validateChamadaFuncao(Token token) {
        if (isToken(sym.ABRE_PARENTESES)) {
            consume();
            boolean isWrite = token.getLexeme().equals("write");
            ArrayList<Integer> tipos = validateListaExpressoes(isWrite);
            tableSymbols.parametrosChamadaProcedimento(tipos, token);
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
                machineCode.addInstruction(MachineCode.OPERACAO_LEITURA, null);
                machineCode.addInstruction(MachineCode.ARMAZENAR_VALOR, tableSymbols.addressSymbol(currentToken));
                tipos.add(tableSymbols.searchSimbolo(currentToken, SymbolParser.CATEGORIA_VARIAVEL));
                consume();
                while (isToken(sym.VIRGULA)) {
                    consume();
                    if (isToken(sym.IDENTIFICADOR)) {
                        machineCode.addInstruction(MachineCode.OPERACAO_LEITURA, null);
                        machineCode.addInstruction(MachineCode.ARMAZENAR_VALOR, tableSymbols.addressSymbol(currentToken));
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
            machineCode.addInstruction(MachineCode.INVERTER_SINAL, null);
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

            Token token = currentToken;
            verifyAndSetMachineCode(token);

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

    private ArrayList<Integer> validateListaExpressoes(boolean isWrite) {
        ArrayList<Integer> tipos = new ArrayList<>();
        tipos.add(validateExpressao());
        if (isWrite) {
            machineCode.addInstruction(MachineCode.OPERACAO_IMPRIME, null);
        }
        while (isToken(sym.VIRGULA)) {
            consume();
            tipos.add(validateExpressao());
            if (isWrite) {
                machineCode.addInstruction(MachineCode.OPERACAO_IMPRIME, null);
            }
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

            Token token = currentToken;
            verifyAndSetMachineCode(token);

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
            machineCode.addInstruction(MachineCode.CARREGAR_CONSTANTE, Integer.valueOf(currentToken.getLexeme()));
            consume();
        } else if (isToken(sym.RESERVADO_FALSE, sym.RESERVADO_TRUE)) {
            if (isToken(sym.RESERVADO_FALSE)) {
                machineCode.addInstruction(MachineCode.CARREGAR_CONSTANTE, 0);
            } else {
                machineCode.addInstruction(MachineCode.CARREGAR_CONSTANTE, 1);
            }
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
            machineCode.addInstruction(MachineCode.OPERADOR_NOT, null);
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
            machineCode.addInstruction(MachineCode.CARREGAR_VALOR, tableSymbols.addressSymbol(currentToken));
            consume();
        }
        return type;
    }
}
