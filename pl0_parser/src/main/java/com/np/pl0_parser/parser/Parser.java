/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.np.pl0_parser.parser;

import com.np.pl0_parser.pojo.ParserException;
import com.np.pl0_parser.constants.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author npetrov_2
 */
public class Parser extends PLBase{

    public List<String> parse(String programCode) throws ParserException {
        this.symbols = normalizeProgramCode(programCode);
        nextSymbol();
        processBlock();
        return symbols;
    }
    /**
     * Метод для нормализации кода программы
     *
     * @param programCode
     * @return
     */
    private List<String> normalizeProgramCode(String programCode) {
        String tempCode = programCode.replaceAll(":=", " @ ");
        tempCode = tempCode.replaceAll("\\s+", " ");
        for (String delimiter : Constants.DELIMITERS) {
            tempCode = tempCode.replace(delimiter, " " + delimiter + " ");
        }
        List<String> result = new ArrayList<>(Arrays.asList(tempCode.replaceAll("\\s+", " ").split(" ")));
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).equals("@")) {
                result.set(i, ":=");
            }
        }
        return result;
    }

    private void processBlock() throws ParserException {
        if (acceptSymbol(Constants.CONST)) {
            processConstants();
        }
        while (acceptSymbol(Constants.VAR_INT)) {
            processIntVariable();
        }
        while (acceptSymbol(Constants.VAR_STRING)) {
            processStringVariable();
        }
        while (acceptSymbol(Constants.PROCEDURE)) {
            processProcedure();
        }
        processStatement();
    }

    /**
     * Обработка констант
     */
    private void processConstants() throws ParserException {
        do {
            expectSymbol(Constants.IDENT_REGEX);
            expectSymbol(Constants.EQUAL);
            expectSymbol(Constants.NUMBER_REGEX);
        } while (acceptSymbol(Constants.COMMA));
        expectSymbol(Constants.SEMICOLON);
    }

    /**
     * Обработка строковых переменных
     */
    private void processStringVariable() throws ParserException {
        do {
            expectSymbol(Constants.IDENT_REGEX);
        } while (acceptSymbol(Constants.COMMA));
        expectSymbol(Constants.SEMICOLON);
    }

    /**
     * Обработка переменных целочисленного типа
     */
    private void processIntVariable() throws ParserException {
        do {
            expectSymbol(Constants.IDENT_REGEX);
        } while (acceptSymbol(Constants.COMMA));
        expectSymbol(Constants.SEMICOLON);
    }

    /**
     * Обработка процедуры
     *
     * @throws ParserException
     */
    private void processProcedure() throws ParserException {
        expectSymbol(Constants.IDENT_REGEX);
        expectSymbol(Constants.SEMICOLON);
        processBlock();
        expectSymbol(Constants.SEMICOLON);
    }

    private void processFactor() throws ParserException {
        if (processIdent()) {
        } else if (acceptSymbol(Constants.NUMBER_REGEX)) {
        } else if (acceptSymbol(Constants.STRING_REGEX)) {
        } else if (acceptSymbol(Constants.LPAREN)) {
            processExpression();
            expectSymbol(Constants.RPAREN);
        } else {
            throw new ParserException("syntax error");
        }
    }

    private void processTerm() throws ParserException {
        processFactor();
        while (this.currentSymbol.equals(Constants.TIMES) || this.currentSymbol.equals(Constants.SLASH)) {
            nextSymbol();
            processFactor();
        }
    }

    private void processExpression() throws ParserException {
        if (this.currentSymbol.equals(Constants.PLUS) || this.currentSymbol.equals(Constants.MINUS)) {
            nextSymbol();
        }
        processTerm();
        while (this.currentSymbol.equals(Constants.PLUS) || this.currentSymbol.equals(Constants.MINUS)) {
            nextSymbol();
            processTerm();
        }
    }

    private void processCondition() throws ParserException {
        if (acceptSymbol(Constants.ODD)) {
            processExpression();
        } else {
            processExpression();
            if (this.currentSymbol.equals(Constants.EQUAL) || this.currentSymbol.equals(Constants.NOT_EQUAL)
                    || this.currentSymbol.equals(Constants.LESS) || this.currentSymbol.equals(Constants.GREATER)) {
                nextSymbol();
                processExpression();
            } else {
                throw new ParserException("invalid operator");
            }
        }
    }

    private void processStatement() throws ParserException {
        if (acceptSymbol(Constants.CALL)) {
            expectSymbol(Constants.IDENT_REGEX);
        } else if (acceptSymbol(Constants.BEGIN)) {
            do {
                processStatement();
            } while (acceptSymbol(Constants.SEMICOLON));
            expectSymbol(Constants.END);
        } else if (acceptSymbol(Constants.IF)) {
            processCondition();
            expectSymbol(Constants.THEN);
            processStatement();
        } else if (acceptSymbol(Constants.WHILE)) {
            processCondition();
            expectSymbol(Constants.DO);
            processStatement();
        } else if (acceptSymbol(Constants.IN)) {
            processIdent();
        } else if (acceptSymbol(Constants.OUT)) {
            processExpression();
        } else if (processIdent()) {
            expectSymbol(Constants.ASSIGN);
            processExpression();
        } else {
            throw new ParserException("Syntax error");
        }
    }
    
    private boolean processIdent() {
        if (this.currentSymbol.matches(Constants.IDENT_REGEX)) {
            nextSymbol();
            return true;
        }
        return false;
    }

}
