/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

import flex.LexiconParser;
import flex.Token;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java_cup.runtime.Symbol;
import jflex.sym;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class CustomScanner extends LexiconParser {

    private final ArrayList<Token> tokensTable;
    private final ArrayList<Token> lookAhead;

    public CustomScanner(Reader in, ArrayList<Token> tokensTable) {
        super(in);
        this.tokensTable = tokensTable;
        this.lookAhead = new ArrayList<>();
    }

    @Override
    public Symbol next_token() throws IOException {
        Symbol nextToken = super.next_token();
        Token token = super.yylex();
        if (token.getToken() != 0 && tokensTable != null) {
            tokensTable.add(super.yylex());
        }
        return nextToken;
    }

    public Token nextToken() {
        try {
            Token token;
            if (lookAhead.isEmpty()) {
                super.next_token();
                token = super.yylex();
            } else {
                token = lookAhead.remove(0);
            }
            if (token.getToken() != 0 && tokensTable != null) {
                tokensTable.add(token);
            }
            return token;
        } catch (IOException ex) {
            return null;
        }
    }

    public int lookAhead(Integer ... tokens) {
        Token token = nextToken();
        try {
            while (!tokenIs(token, tokens)) {
                lookAhead.add(token);
                super.next_token();
                token = super.yylex();
                if (token.getToken() == sym.EOF) {
                    break;
                }
            }
        } catch (IOException e) {
            return sym.EOF;
        }
        lookAhead.add(token);
        return token.getToken();
    }

    public boolean tokenIs(Token token, Integer... tokens) {
        for (Integer t : tokens) {
            if (t == token.getToken()) {
                return true;
            }
        }
        return false;
    }
}
