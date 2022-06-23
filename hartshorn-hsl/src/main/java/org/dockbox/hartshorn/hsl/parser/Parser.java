package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayVariable;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Function;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.statement.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {

    /*
     * TODO : Create advanced error system for parser to show expect .... in line ....
     */
    private int current = 0;
    private List<Token> tokens;
    private final ErrorReporter errorReporter;

    private static final int MAX_NUM_OF_ARGUMENTS = 8;

    private final Set<String> prefixFunctions = new HashSet<>();
    private final Set<String> infixFunctions = new HashSet<>();

    public Parser(final List<Token> tokens, final ErrorReporter errorReporter) {
        this.tokens = tokens;
        this.errorReporter = errorReporter;
    }

    public List<Token> tokens() {
        return this.tokens;
    }

    public void tokens(final List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse() {
        final List<Statement> statements = new ArrayList<>();
        while (!this.isAtEnd()) {
            statements.add(this.declaration());
        }
        return statements;
    }

    private Statement statement() {
        if (this.match(TokenType.IF)) return this.ifStatement();
        if (this.match(TokenType.DO)) return this.doWhileStatement();
        if (this.match(TokenType.WHILE)) return this.whileStatement();
        if (this.match(TokenType.REPEAT)) return this.repeatStatement();
        if (this.match(TokenType.PRINT)) return this.printStatement();
        if (this.match(TokenType.RETURN)) return this.returnStatement();
        if (this.match(TokenType.LEFT_BRACE)) return new BlockStatement(this.block());
        if (this.match(TokenType.BREAK)) return this.breakStatement();
        if (this.match(TokenType.CONTINUE)) return this.continueStatement();
        if (this.match(TokenType.TEST)) return this.testStatement();
        if (this.match(TokenType.MODULE)) return this.moduleStatement();
        return this.expressionStatement();
    }

    private Statement moduleStatement() {
        final Token name = this.consume(TokenType.IDENTIFIER, "Expected module name.");
        this.consume(TokenType.SEMICOLON, "Expected ';' after module.");
        return new ModuleStatement(name);
    }

    private Statement declaration() {
        try {
            if (this.match(TokenType.PREFIX, TokenType.INFIX) && this.match(TokenType.FUN)) return this.funcDeclaration(this.tokens.get(this.current - 2));
            if (this.match(TokenType.FUN)) return this.funcDeclaration(this.previous());
            if (this.match(TokenType.VAR)) return this.varDeclaration();
            if (this.match(TokenType.CLASS)) return this.classDeclaration();
            if (this.match(TokenType.NATIVE)) return this.nativeFuncDeclaration();
            return this.statement();
        }
        catch (final ParseError error) {
            this.synchronize();
            return null;
        }
    }

    private Statement classDeclaration() {
        final Token name = this.consume(TokenType.IDENTIFIER, "Expected class name.");

        VariableExpression superclass = null;
        if (this.match(TokenType.EXTENDS)) {
            this.consume(TokenType.IDENTIFIER, "Expected superclass name.");
            superclass = new VariableExpression(this.previous());
        }

        this.consume(TokenType.LEFT_BRACE, "Expected '{' before class body.");

        final List<FunctionStatement> methods = new ArrayList<>();
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            methods.add(this.methodDeclaration());
        }

        this.consume(TokenType.RIGHT_BRACE, "Expected '}' after class body.");

        return new ClassStatement(name, superclass, methods);
    }

    private FunctionStatement methodDeclaration() {
        this.consume(TokenType.FUN, "Expected fun keyword");
        final Token name = this.consume(TokenType.IDENTIFIER, "Expected method name.");
        this.consume(TokenType.LEFT_PAREN, "Expected '(' after method name.");
        final List<Token> parameters = new ArrayList<>();
        if (!this.check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= MAX_NUM_OF_ARGUMENTS) {
                    this.error(this.peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " parameters.");
                }
                parameters.add(this.consume(TokenType.IDENTIFIER, "Expected parameter name."));
            }
            while (this.match(TokenType.COMMA));
        }
        this.consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters.");
        this.consume(TokenType.LEFT_BRACE, "Expected '{' before method body.");
        final List<Statement> body = this.block();
        return new FunctionStatement(name, parameters, body);
    }

    private Function funcDeclaration(final Token token) {
        final Token name = this.consume(TokenType.IDENTIFIER, "Expected function name.");

        int expectedNumberOrArguments = 8;
        if (token.type() == TokenType.PREFIX) {
            this.prefixFunctions.add(name.lexeme());
            expectedNumberOrArguments = 1;
        }
        else if (token.type() == TokenType.INFIX) {
            this.infixFunctions.add(name.lexeme());
            expectedNumberOrArguments = 2;
        }

        Token extensionName = null;

        if (this.peek().type() == TokenType.COLON) {
            this.consume(TokenType.COLON, "Expected : After Class name");
            extensionName = this.consume(TokenType.IDENTIFIER, "Expected extension name.");
        }

        this.consume(TokenType.LEFT_PAREN, "Expected '(' after function name.");
        final List<Token> parameters = new ArrayList<>();
        if (!this.check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= expectedNumberOrArguments) {
                    this.error(this.peek(), "Cannot have more than " + expectedNumberOrArguments + " parameters for " + token.type() + " functions");
                }
                parameters.add(this.consume(TokenType.IDENTIFIER, "Expected parameter name."));
            }
            while (this.match(TokenType.COMMA));
        }

        this.consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters.");
        this.consume(TokenType.LEFT_BRACE, "Expected '{' before function body.");
        final List<Statement> body = this.block();

        if (extensionName != null) {
            final FunctionStatement function = new FunctionStatement(extensionName, parameters, body);
            return new ExtensionStatement(name, function);
        }
        else {
            return new FunctionStatement(name, parameters, body);
        }
    }

    private Statement nativeFuncDeclaration() {
        this.consume(TokenType.FUN, "Expected fun keyword");
        final Token moduleName = this.consume(TokenType.IDENTIFIER, "Expected module name.");

        while (this.match(TokenType.COLON)) {
            final Token token = new Token(TokenType.DOT, ".", moduleName.line());
            moduleName.concat(token);
            final Token moduleName2 = this.consume(TokenType.IDENTIFIER, "Expected module name.");
            moduleName.concat(moduleName2);
        }

        this.consume(TokenType.DOT, "Expected '.' before method body.");
        final Token funcName = this.consume(TokenType.IDENTIFIER, "Expected function name.");
        this.consume(TokenType.LEFT_PAREN, "Expected '(' after method name.");
        final List<Token> parameters = new ArrayList<>();
        if (!this.check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= MAX_NUM_OF_ARGUMENTS) {
                    this.error(this.peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " parameters.");
                }
                parameters.add(this.consume(TokenType.IDENTIFIER, "Expected parameter name."));
            }
            while (this.match(TokenType.COMMA));
        }
        this.consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters.");
        this.consume(TokenType.SEMICOLON, "Expected ';' after value.");
        return new NativeFunctionStatement(funcName, moduleName, parameters);
    }

    private Statement varDeclaration() {
        final Token name = this.consume(TokenType.IDENTIFIER, "Expected variable name.");

        Expression initializer = null;
        if (this.match(TokenType.EQUAL)) {
            initializer = this.expression();
        }

        this.consume(TokenType.SEMICOLON, "Expected ';' after variable declaration.");
        return new VariableStatement(name, initializer);
    }

    private Statement breakStatement() {
        final Token keyword = this.previous();
        this.consume(TokenType.SEMICOLON, "Expected ';' after value.");
        return new BreakStatement(keyword);
    }

    private Statement continueStatement() {
        final Token keyword = this.previous();
        this.consume(TokenType.SEMICOLON, "Expected ';' after value.");
        return new ContinueStatement(keyword);
    }

    //TODO : improve if to work with multi time of else if before get else
    private Statement ifStatement() {
        this.consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'.");
        final Expression condition = this.expression();
        this.consume(TokenType.RIGHT_PAREN, "Expected ')' after if condition.");
        this.consume(TokenType.LEFT_BRACE, "Expected '{' to start if body.");
        final List<Statement> thenBranch = new ArrayList<>();
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            thenBranch.add(this.declaration());
        }
        this.consume(TokenType.RIGHT_BRACE, "Expected '}' to end if body.");
        List<Statement> elseBranch = null;
        if (this.match(TokenType.ELSE)) {
            elseBranch = new ArrayList<>();
            this.consume(TokenType.LEFT_BRACE, "Expected '{' to start else body.");
            while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
                elseBranch.add(this.declaration());
            }
            this.consume(TokenType.RIGHT_BRACE, "Expected '}' to end else body.");
        }
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement whileStatement() {
        this.consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'.");
        final Expression condition = this.expression();
        this.consume(TokenType.RIGHT_PAREN, "Expected ')' after while condition.");
        final Statement loopBody = this.statement();
        return new WhileStatement(condition, loopBody);
    }

    private Statement doWhileStatement() {
        final Statement loopBody = this.statement();
        this.consume(TokenType.WHILE, "Expected while keyword.");
        this.consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'.");
        final Expression condition = this.expression();
        this.consume(TokenType.RIGHT_PAREN, "Expected ')' after do while condition.");
        this.consume(TokenType.SEMICOLON, "Expected ';' after do while condition.");
        return new DoWhileStatement(condition, loopBody);
    }

    private Statement repeatStatement() {
        this.consume(TokenType.LEFT_PAREN, "Expected '(' after 'repeat'.");
        final Expression value = this.expression();
        this.consume(TokenType.RIGHT_PAREN, "Expected ')' after repeat value.");
        final Statement loopBody = this.statement();
        return new RepeatStatement(value, loopBody);
    }

    private Statement printStatement() {
        final Expression value;
        if (this.check(TokenType.LEFT_PAREN)) {
            this.advance();
            value = this.expression();
            this.consume(TokenType.RIGHT_PAREN, "Expected ')' after print expression.");
        }
        else value = this.expression();
        this.consume(TokenType.SEMICOLON, "Expected ';' after value.");
        return new PrintStatement(value);
    }

    private Statement returnStatement() {
        final Token keyword = this.previous();
        Expression value = null;
        if (!this.check(TokenType.SEMICOLON)) {
            value = this.expression();
        }
        this.consume(TokenType.SEMICOLON, "Expected ';' after return value.");
        return new ReturnStatement(keyword, value);
    }

    private Statement testStatement() {
        this.consume(TokenType.LEFT_PAREN, "Expected '(' after 'test statement'.");
        final Token name = this.consume(TokenType.STRING, "Expected test name.");
        this.consume(TokenType.RIGHT_PAREN, "Expected ')' after test statement name value.");
        this.consume(TokenType.LEFT_BRACE, "Expected '{' before test body.");
        final List<Statement> statements = new ArrayList<>();
        Statement returnStatement = null;
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            final Statement statement = this.declaration();
            if (statement instanceof ReturnStatement) {
                returnStatement = statement;
                this.consume(TokenType.RIGHT_BRACE, "Expected '}' after block.");
                break;
            }
            else {
                statements.add(statement);
            }
        }
        return new TestStatement(name, statements, returnStatement);
    }

    private Statement expressionStatement() {
        final Expression expr = this.expression();
        this.consume(TokenType.SEMICOLON, "Expected ';' after expression.");
        return new ExpressionStatement(expr);
    }

    private List<Statement> block() {
        final List<Statement> statements = new ArrayList<>();
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            statements.add(this.declaration());
        }
        this.consume(TokenType.RIGHT_BRACE, "Expected '}' after block.");
        return statements;
    }

    private Expression expression() {
        return this.assignment();
    }

    private Expression assignment() {
        //equality lower than || lower than &&
        final Expression expr = this.elvisExp();

        if (this.match(TokenType.EQUAL)) {
            final Token equals = this.previous();
            final Expression value = this.assignment();

            if (expr instanceof VariableExpression) {
                final Token name = ((VariableExpression) expr).name();
                return new AssignExpression(name, value);
            }
            else if (expr instanceof final ArrayVariable arrayVariable) {
                final Token name = arrayVariable.name();
                return new ArraySetExpression(name, arrayVariable.index(), value);
            }
            else if (expr instanceof final GetExpression get) {
                return new SetExpression(get.object(), get.name(), value);
            }
            this.error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Expression elvisExp() {
        final Expression expr = this.ternaryExp();
        if (this.match(TokenType.ELVIS)) {
            final Token elvis = this.previous();
            final Expression rightExp = this.ternaryExp();
            return new ElvisExpression(expr, elvis, rightExp);
        }
        return expr;
    }

    private Expression ternaryExp() {
        final Expression expr = this.bitwise();

        if (this.match(TokenType.QUESTION_MARK)) {
            final Token question = this.previous();
            final Expression firstExp = this.or();
            final Token colon = this.peek();
            if (this.match(TokenType.COLON)) {
                final Expression secondExp = this.or();
                return new TernaryExpression(expr, question, firstExp, colon, secondExp);
            }
            this.errorReporter.error(colon, "Expected Expression after COLON");
        }
        return expr;
    }

    private Expression bitwise() {
        Expression expr = this.or();

        while (this.match(TokenType.SHIFT_LEFT, TokenType.SHIFT_RIGHT, TokenType.LOGICAL_SHIFT_RIGHT)) {
            final Token operator = this.previous();
            final Expression right = this.xor();
            expr = new BitwiseExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression or() {
        Expression expr = this.xor();

        while (this.match(TokenType.OR)) {
            final Token operator = this.previous();
            final Expression right = this.xor();
            expr = new LogicalExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression xor() {
        Expression expr = this.and();

        while (this.match(TokenType.XOR)) {
            final Token operator = this.previous();
            final Expression right = this.and();
            expr = new LogicalExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression and() {
        Expression expr = this.equality();

        while (this.match(TokenType.AND)) {
            final Token operator = this.previous();
            final Expression right = this.equality();
            expr = new LogicalExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression equality() {
        Expression expr = this.parsePrefixFunctionCall();

        while (this.match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            final Token operator = this.previous();
            final Expression right = this.parsePrefixFunctionCall();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private boolean match(final TokenType... types) {
        for (final TokenType type : types) {
            if (this.check(type)) {
                this.advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(final TokenType type) {
        if (this.isAtEnd()) return false;
        return this.peek().type() == type;
    }

    private Token advance() {
        if (!this.isAtEnd()) this.current++;
        return this.previous();
    }

    private boolean isAtEnd() {
        return this.peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return this.tokens.get(this.current);
    }

    private Token previous() {
        return this.tokens.get(this.current - 1);
    }

    private Expression parsePrefixFunctionCall() {
        if (this.check(TokenType.IDENTIFIER) && this.prefixFunctions.contains(this.tokens.get(this.current).lexeme())) {
            this.current++;
            final Token prefixFunctionName = this.previous();
            final Expression right = this.comparison();
            return new PrefixExpression(prefixFunctionName, right);
        }
        return this.comparison();
    }

    private Expression comparison() {
        Expression expr = this.addition();

        while (this.match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            final Token operator = this.previous();
            final Expression right = this.addition();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression addition() {
        Expression expr = this.multiplication();
        while (this.match(TokenType.MINUS, TokenType.PLUS)) {
            final Token operator = this.previous();
            final Expression right = this.multiplication();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression multiplication() {
        Expression expr = this.parseInfixExpressions();

        while (this.match(TokenType.SLASH, TokenType.STAR)) {
            final Token operator = this.previous();
            final Expression right = this.parseInfixExpressions();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression parseInfixExpressions() {
        Expression expr = this.unary();

        while (this.check(TokenType.IDENTIFIER) && this.infixFunctions.contains(this.tokens.get(this.current).lexeme())) {
            this.current++;
            final Token operator = this.previous();
            final Expression right = this.unary();
            expr = new InfixExpression(expr, operator, right);
        }
        return expr;
    }

    private Expression unary() {
        if (this.match(TokenType.BANG, TokenType.MINUS, TokenType.PLUS_PLUS, TokenType.MINUS_MINUS)) {
            final Token operator = this.previous();
            final Expression right = this.unary();
            return new UnaryExpression(operator, right);
        }
        return this.call();
    }

    private Expression call() {
        Expression expr = this.primary();
        while (true) {
            if (this.match(TokenType.LEFT_PAREN)) {
                expr = this.finishCall(expr);
            }
            else if (this.match(TokenType.DOT)) {
                final Token name = this.consume(TokenType.IDENTIFIER, "Expected property name after '.'.");
                expr = new GetExpression(name, expr);
            }
            else {
                break;
            }
        }
        return expr;
    }

    private Expression finishCall(final Expression callee) {
        final List<Expression> arguments = new ArrayList<>();
        //For zero Arguments
        if (!this.check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= MAX_NUM_OF_ARGUMENTS) {
                    //For now just report error but not throws it
                    this.error(this.peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " arguments.");
                }
                arguments.add(this.expression());
            }
            while (this.match(TokenType.COMMA));
        }
        final Token paren = this.consume(TokenType.RIGHT_PAREN, "Expected ')' after arguments.");
        return new FunctionCallExpression(callee, paren, arguments);
    }

    private Expression primary() {
        if (this.match(TokenType.FALSE)) return new LiteralExpression(false);
        if (this.match(TokenType.TRUE)) return new LiteralExpression(true);
        if (this.match(TokenType.NIL)) return new LiteralExpression(null);
        if (this.match(TokenType.THIS)) return new ThisExpression(this.previous());
        if (this.match(TokenType.NUMBER, TokenType.STRING, TokenType.CHAR)) return new LiteralExpression(this.previous().literal());
        if (this.match(TokenType.IDENTIFIER)) {
            final Token next = this.peek();
            if (next.type() == TokenType.ARRAY_OPEN) {
                final Token name = this.previous();
                this.consume(TokenType.ARRAY_OPEN, "Expected [");
                final Expression index = this.expression();
                this.consume(TokenType.ARRAY_CLOSE, "Expected ]");
                return new ArrayVariable(name, index);
            }
            return new VariableExpression(this.previous());
        }
        if (this.match(TokenType.LEFT_PAREN)) {
            final Expression expr = this.expression();
            this.consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");
            return new GroupingExpression(expr);
        }
        if (this.match(TokenType.ARRAY)) {
            this.consume(TokenType.ARRAY_OPEN, "Expected [");
            final Expression size = this.expression();
            this.consume(TokenType.ARRAY_CLOSE, "Expected ]");
            return new ArrayGetExpression(size);
        }
        if (this.match(TokenType.SUPER)) {
            final Token keyword = this.previous();
            this.consume(TokenType.DOT, "Expected '.' after 'super'.");
            final Token method = this.consume(TokenType.IDENTIFIER, "Expected superclass method name.");
            return new SuperExpression(keyword, method);
        }
        throw this.error(this.peek(), "Expected expression. " + this.tokens.get(this.current));
    }

    private ParseError error(final Token token, final String message) {
        this.errorReporter.error(token.line(), message);
        return new ParseError();
    }

    private Token consume(final TokenType type, final String message) {
        if (this.check(type)) return this.advance();
        throw this.error(this.peek(), message);
    }

    private void synchronize() {
        this.advance();

        while (!this.isAtEnd()) {
            if (this.previous().type() == TokenType.SEMICOLON) return;

            switch (this.peek().type()) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            this.advance();
        }
    }
}
