import java.io.IOException;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    // I have made the file-name a public field so that I can include it within my
    // error message for convienience to the debugger.
    public String fileName;

    // Here is the constructor for the SyntaxAnalyser. This takes in the file name
    // for public visibility and also initialises a lexical analyser using the
    // filename.
    public SyntaxAnalyser(String fileName) throws IOException {
        this.fileName = fileName;
        this.lex = new LexicalAnalyser(fileName);
    }

    /**
     * Grammar Rule: <StatementPart> ::= begin <StatementList> end
     * 
     * This method first begins by commencing the beginning of the statement part,
     * checking for a begin symbol and then calling the statement list method.
     * After every statement in the statement list is processed, it checks for an
     * end symbol and then finishes the statement part.
     **/
    public void _statementPart_() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("StatementPart");
        acceptTerminal(Token.beginSymbol);
        statementList();
        acceptTerminal(Token.endSymbol);
        myGenerate.finishNonterminal("StatementPart");
    }

    /**
     * Grammar Rule: <StatementList> ::= <Statement> |
     * <StatementList> ; <Statement>
     * 
     * Following the left-recursive rule in the specification, it
     * calls itself recusively while there are any semicolon tokens
     * to account for multiple statements. For every detected statement,
     * it calls the statement method.
     **/
    public void statementList() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("StatementList");
        statement();
        while (nextToken.symbol == Token.semicolonSymbol) {
            acceptTerminal(Token.semicolonSymbol);
            statementList();
        }
        myGenerate.finishNonterminal("StatementList");
    }

    /**
     * Grammar Rule: <Statement> ::= <AssignmentStatement> |
     * <IfStatement> |
     * <WhileStatement> |
     * <ProcedureStatement> |
     * <UntilStatement> |
     * <ForStatement>
     * 
     * This method checks for which type of statement the token is and then
     * calls the respective method handler. (The non-terminal commence and finish
     * is also called before and after the statement's conclusion respectively.)
     **/
    public void statement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("Statement");
        switch (nextToken.symbol) {
            case Token.identifier:
                assignmentStatement();
                break;
            case Token.ifSymbol:
                ifStatement();
                break;
            case Token.whileSymbol:
                whileStatement();
                break;
            case Token.callSymbol:
                procedureStatement();
                break;
            case Token.untilSymbol:
                untilStatement();
                break;
            case Token.forSymbol:
                forStatement();
                break;
        }
        myGenerate.finishNonterminal("Statement");
    }

    /**
     * Grammar Rule: <AssignmentStatement> ::= identifier := <Expression> |
     * identifier := stringConstant
     * 
     * Due to the grammar rule for an assignment statement always having an
     * identifier followed by a becomes symbol, they are not included in the if/else
     * in order to reduce redundancy. (Including necessary commence and finish
     * non-terminal calls)
     **/
    public void assignmentStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("AssignmentStatement");
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);
        if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
        } else {
            expression();
        }
        myGenerate.finishNonterminal("AssignmentStatement");
    }

    /**
     * Grammar Rule: <IfStatement> ::= if <Condition> then <StatementList> end if |
     * if <Condition> then <StatementList> else <StatementList> end if
     * 
     * Due to the grammar rule for an if statement always having an if symbol
     * followed by a condition and then a statement list, they are not included in
     * the if statement in order to reduce redundancy. We do check for an else token
     * and then call the statement list method if necessary. We then check for an
     * end symbol and then an if symbol. (Including necessary commence and finish
     * non-terminal calls)
     **/
    public void ifStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("IfStatement");
        acceptTerminal(Token.ifSymbol);
        condition();
        acceptTerminal(Token.thenSymbol);
        statementList();
        if (nextToken.symbol == Token.elseSymbol) {
            acceptTerminal(Token.elseSymbol);
            statementList();
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
        myGenerate.finishNonterminal("IfStatement");
    }

    /**
     * Grammar Rule: <WhileStatement> ::= while <Condition> loop <StatementList> end
     * loop
     * 
     * While statement is straightforward, check for the while token, call the
     * condition handler, check for a loop token, call the statement list handler
     * and then check for an end and loop token. (Including necessary commence and
     * finish non-terminal calls)
     **/
    public void whileStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("WhileStatement");
        acceptTerminal(Token.whileSymbol);
        condition();
        acceptTerminal(Token.loopSymbol);
        statementList();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        myGenerate.finishNonterminal("WhileStatement");
    }

    /**
     * Grammar Rule: <ProcedureStatement> ::= call identifier ( <ArgumentList> )
     * 
     * Another straightforward rule/method, check for the call symbol and an
     * identifier, then check for an open bracket, call the argument list
     * handler and then a closing bracket. (Including necessary commence and
     * finish non-terminal calls)
     **/
    public void procedureStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("ProcedureStatement");
        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.leftParenthesis);
        argumentList();
        acceptTerminal(Token.rightParenthesis);
        myGenerate.finishNonterminal("ProcedureStatement");
    }

    /**
     * Grammar Rule: <UntilStatement> ::= do <StatementList> until <Condition>
     * 
     * Straightforward method. Calls appropriate checks and handlers. (Including
     * necessary commence and finish non-terminal calls)
     **/
    public void untilStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("UntilStatement");
        acceptTerminal(Token.doSymbol);
        statementList();
        acceptTerminal(Token.untilSymbol);
        condition();
        myGenerate.finishNonterminal("UntilStatement");
    }

    /**
     * Grammar Rule: <ForStatement> ::= for ( <AssignmentStatement> ; <Condition> ;
     * <AssignmentStatement> ) do <StatementList> end loop
     * 
     * Straightforward method. Calls appropriate checks and handlers. (Including
     * necessary commence and finish non-terminal calls)
     **/
    public void forStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("ForStatement");
        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        assignmentStatement();
        acceptTerminal(Token.semicolonSymbol);
        condition();
        acceptTerminal(Token.semicolonSymbol);
        assignmentStatement();
        acceptTerminal(Token.rightParenthesis);
        acceptTerminal(Token.doSymbol);
        statementList();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        myGenerate.finishNonterminal("ForStatement");
    }

    /**
     * Grammar Rule: <ArgumentList> ::= identifier |
     * <ArgumentList> , identifier
     * 
     * Following the left-recursive rule in the specification, it
     * calls itself recusively while there are any comma tokens after
     * the initial identifier token.
     **/
    public void argumentList() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("ArgumentList");
        acceptTerminal(Token.identifier);
        while (nextToken.symbol == Token.commaSymbol) {
            acceptTerminal(Token.commaSymbol);
            argumentList();
        }
        myGenerate.finishNonterminal("ArgumentList");
    }

    /**
     * Grammar Rule: <Condition> ::= identifier <ConditionalOperator> identifier |
     * identifier <ConditionalOperator> numberConstant |
     * identifier <ConditionalOperator> stringConstant
     * 
     * Due to the grammar rule stating that a condition always has an identifier
     * followed by a conditional operator, they are not included in the switch case.
     **/
    public void condition() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("Condition");
        acceptTerminal(Token.identifier);
        conditionalOperator();
        switch (nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.stringConstant:
                acceptTerminal(Token.stringConstant);
                break;
        }
        myGenerate.finishNonterminal("Condition");
    }

    /**
     * Grammar Rule: <ConditionalOperator> ::= > | >= | = | != | < | <=
     * 
     * Straightforward method. Calls appropriate checks and handlers. (Including
     * necessary commence and finish non-terminal calls)
     **/
    public void conditionalOperator() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("ConditionalOperator");
        switch (nextToken.symbol) {
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
        }
        myGenerate.finishNonterminal("ConditionalOperator");
    }

    /**
     * Grammar Rule: <Expression> ::= <Term> |
     * <Expression> + <Term> |
     * <Expression> - <Term>
     * 
     * Following the left-recursive rule in the specification, it
     * calls itself recusively while there are any plus or minus tokens after
     * the initial term token.
     **/
    public void expression() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("Expression");
        term();
        while (nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol) {
            if (nextToken.symbol == Token.plusSymbol) {
                acceptTerminal(Token.plusSymbol);
            } else {
                acceptTerminal(Token.minusSymbol);
            }
            expression();
        }
        myGenerate.finishNonterminal("Expression");
    }

    /**
     * Grammar Rule: <Term> ::= <Factor> | <Term> * <Factor> | <Term> / <Factor> |
     * <Term> % <Factor>
     * 
     * Following the left-recursive rule in the specification, it
     * calls itself recusively while there are any multiply, divide or
     * modulo tokens after the initial factor token.
     **/
    public void term() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("Term");
        factor();
        while (nextToken.symbol == Token.timesSymbol || nextToken.symbol == Token.divideSymbol
                || nextToken.symbol == Token.modSymbol) {
            switch (nextToken.symbol) {
                case Token.timesSymbol:
                    acceptTerminal(Token.timesSymbol);
                    break;
                case Token.divideSymbol:
                    acceptTerminal(Token.divideSymbol);
                    break;
                case Token.modSymbol:
                    acceptTerminal(Token.modSymbol);
                    break;
            }
            term();
        }
        myGenerate.finishNonterminal("Term");
    }

    /**
     * Grammar Rule: <Factor> ::= identifier | numberConstant | ( <Expression> )
     * 
     * A simple switch, case statement is used here to check if the next token is
     * either an identifier, numberConstant or a open bracket. (And therefore an
     * expression)
     **/
    public void factor() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("Factor");
        switch (nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.leftParenthesis:
                acceptTerminal(Token.leftParenthesis);
                expression();
                acceptTerminal(Token.rightParenthesis);
                break;
        }
        myGenerate.finishNonterminal("Factor");
    }

    /**
     * Due to my program always checking the next symbol instead of the current
     * once, acceptTerminal functions by checking if the next token is the expected
     * token (Which is the token we are parsing in). If this is the case, it inserts
     * terminals the next token and then assigns nextToken to the next token in the
     * file. If this isn't the case, it generates an error message by first saying
     * which file the error occured in (relevent due to us using 12 files), the line
     * on which the error occured on, and what the expected token should have been.
     * 
     * It then invokes the reportError method in myGenerate using the provided error
     * message.
     **/
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        if (nextToken.symbol == symbol) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        } else {
            String explanatoryMessage = "[File: " + fileName + " - Line " + nextToken.lineNumber + "] - Expected: '"
                    + Token.getName(symbol)
                    + "' but got: " + nextToken.toString();
            myGenerate.reportError(nextToken, explanatoryMessage);
        }
    }
}