package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayVariable;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Function;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class Parser {

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
        if (this.match(TokenType.LEFT_BRACE)) return new BlockStatement(this.peek(), this.block());
        if (this.match(TokenType.BREAK)) return this.breakStatement();
        if (this.match(TokenType.CONTINUE)) return this.continueStatement();
        if (this.match(TokenType.TEST)) return this.testStatement();
        if (this.match(TokenType.MODULE)) return this.moduleStatement();

        final TokenType type = this.peek().type();
        if (type.standaloneStatement()) {
            throw new IllegalStateException("Unsupported standalone statement type: " + type);
        }

        return this.expressionStatement();
    }

    private Statement moduleStatement() {
        final Token name = this.expect(TokenType.IDENTIFIER, "module name");
        this.expectAfter(TokenType.SEMICOLON, TokenType.MODULE);
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
        final Token name = this.expect(TokenType.IDENTIFIER, "class name");

        VariableExpression superclass = null;
        if (this.match(TokenType.EXTENDS)) {
            this.expect(TokenType.IDENTIFIER, "superclass name");
            superclass = new VariableExpression(this.previous());
        }

        this.expectBefore(TokenType.LEFT_BRACE, "class body");

        final List<FunctionStatement> methods = new ArrayList<>();
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            methods.add(this.methodDeclaration());
        }

        this.expectAfter(TokenType.RIGHT_BRACE, "class body");

        return new ClassStatement(name, superclass, methods);
    }

    private FunctionStatement methodDeclaration() {
        return this.function(TokenType.FUN, TokenType.LEFT_BRACE, (name, parameters) -> {
            final List<Statement> body = this.block();
            return new FunctionStatement(name, parameters, body);
        });
    }

    private Function funcDeclaration(final Token token) {
        final Token name = this.expect(TokenType.IDENTIFIER, "function name");

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
            this.expectAfter(TokenType.COLON, "class name");
            extensionName = this.expect(TokenType.IDENTIFIER, "extension name");
        }

        this.expectAfter(TokenType.LEFT_PAREN, "function name");
        final List<Token> parameters = new ArrayList<>();
        if (!this.check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= expectedNumberOrArguments) {
                    this.report(this.peek(), "Cannot have more than " + expectedNumberOrArguments + " parameters for " + token.type() + " functions");
                }
                parameters.add(this.expect(TokenType.IDENTIFIER, "parameter name"));
            }
            while (this.match(TokenType.COMMA));
        }

        this.expectAfter(TokenType.RIGHT_PAREN, "parameters");
        this.expectBefore(TokenType.LEFT_BRACE, "function body");
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
        this.expect(TokenType.FUN);
        final Token moduleName = this.expect(TokenType.IDENTIFIER, "module name");

        while (this.match(TokenType.COLON)) {
            final Token token = new Token(TokenType.DOT, ".", moduleName.line());
            moduleName.concat(token);
            final Token moduleName2 = this.expect(TokenType.IDENTIFIER, "module name");
            moduleName.concat(moduleName2);
        }

        return this.function(TokenType.DOT, TokenType.SEMICOLON, (name, parameters) -> {
            return new NativeFunctionStatement(name, moduleName, parameters);
        });
    }

    private <T extends Statement> T function(final TokenType keyword, final TokenType end, final BiFunction<Token, List<Token>, T> converter) {
        this.expectBefore(keyword, "method body");
        final Token funcName = this.expect(TokenType.IDENTIFIER, "function name");
        this.expectAfter(TokenType.LEFT_PAREN, "method name");
        final List<Token> parameters = new ArrayList<>();
        if (!this.check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= MAX_NUM_OF_ARGUMENTS) {
                    this.report(this.peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " parameters.");
                }
                parameters.add(this.expect(TokenType.IDENTIFIER, "parameter name"));
            }
            while (this.match(TokenType.COMMA));
        }
        this.expectAfter(TokenType.RIGHT_PAREN, "parameters");
        this.expectAfter(end, "value");
        return converter.apply(funcName, parameters);
    }

    private Statement varDeclaration() {
        final Token name = this.expect(TokenType.IDENTIFIER, "variable name");

        Expression initializer = null;
        if (this.match(TokenType.EQUAL)) {
            initializer = this.expression();
        }

        this.expectAfter(TokenType.SEMICOLON, "variable declaration");
        return new VariableStatement(name, initializer);
    }

    private Statement breakStatement() {
        final Token keyword = this.previous();
        this.expectAfter(TokenType.SEMICOLON, "value");
        return new BreakStatement(keyword);
    }

    private Statement continueStatement() {
        final Token keyword = this.previous();
        this.expectAfter(TokenType.SEMICOLON, "value");
        return new ContinueStatement(keyword);
    }

    private Statement ifStatement() {
        this.expectAfter(TokenType.LEFT_PAREN, TokenType.IF);
        final Expression condition = this.expression();
        this.expectAfter(TokenType.RIGHT_PAREN, "if condition");
        this.consume(TokenType.LEFT_BRACE, "Expected '{' to start if body.");
        final List<Statement> thenBranch = new ArrayList<>();
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            thenBranch.add(this.declaration());
        }
        this.consume(TokenType.RIGHT_BRACE, "Expected '}' to end if body.");
        List<Statement> elseBranch = null;
        if (this.match(TokenType.ELSE)) {
            elseBranch = new ArrayList<>();

            if (this.match(TokenType.IF)) {
                elseBranch.add(this.ifStatement());
            }
            else {
                this.consume(TokenType.LEFT_BRACE, "Expected '{' to start else body.");
                while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
                    elseBranch.add(this.declaration());
                }
                this.consume(TokenType.RIGHT_BRACE, "Expected '}' to end else body.");
            }
        }
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement whileStatement() {
        this.expectAfter(TokenType.LEFT_PAREN, TokenType.WHILE);
        final Expression condition = this.expression();
        this.expectAfter(TokenType.RIGHT_PAREN, "while condition");
        final Statement loopBody = this.statement();
        return new WhileStatement(condition, loopBody);
    }

    private Statement doWhileStatement() {
        final Statement loopBody = this.statement();
        this.expect(TokenType.WHILE);
        this.expectAfter(TokenType.LEFT_PAREN, TokenType.WHILE);
        final Expression condition = this.expression();
        this.expectAfter(TokenType.RIGHT_PAREN, "do while condition");
        this.expectAfter(TokenType.SEMICOLON, "do while condition");
        return new DoWhileStatement(condition, loopBody);
    }

    private Statement repeatStatement() {
        this.expectAfter(TokenType.LEFT_PAREN, "repeat");
        final Expression value = this.expression();
        this.expectAfter(TokenType.RIGHT_PAREN, "repeat value");
        final Statement loopBody = this.statement();
        return new RepeatStatement(value, loopBody);
    }

    private Statement printStatement() {
        final Expression value;
        if (this.check(TokenType.LEFT_PAREN)) {
            this.advance();
            value = this.expression();
            this.expectAfter(TokenType.RIGHT_PAREN, "print expression");
        }
        else value = this.expression();
        this.expectAfter(TokenType.SEMICOLON, "value");
        return new PrintStatement(value);
    }

    private Statement returnStatement() {
        final Token keyword = this.previous();
        Expression value = null;
        if (!this.check(TokenType.SEMICOLON)) {
            value = this.expression();
        }
        this.expectAfter(TokenType.SEMICOLON, "return value");
        return new ReturnStatement(keyword, value);
    }

    private Statement testStatement() {
        this.expectAfter(TokenType.LEFT_PAREN, "test statement");
        final Token name = this.expect(TokenType.STRING, "test name");
        this.expectAfter(TokenType.RIGHT_PAREN, "test statement name value");
        this.expectBefore(TokenType.LEFT_BRACE, "test body");
        final List<Statement> statements = new ArrayList<>();
        Statement returnStatement = null;
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            final Statement statement = this.declaration();
            if (statement instanceof ReturnStatement) {
                returnStatement = statement;
                this.expectAfter(TokenType.RIGHT_BRACE, "block");
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
        this.expectAfter(TokenType.SEMICOLON, "expression");
        return new ExpressionStatement(expr);
    }

    private List<Statement> block() {
        final List<Statement> statements = new ArrayList<>();
        while (!this.check(TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
            statements.add(this.declaration());
        }
        this.expectAfter(TokenType.RIGHT_BRACE, "block");
        return statements;
    }

    private Expression expression() {
        return this.assignment();
    }

    private Expression assignment() {
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
            this.report(equals, "Invalid assignment target.");
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
            this.errorReporter.error(Phase.PARSING, colon, "Expected Expression after " + TokenType.COLON.representation());
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
        // For zero arguments
        if (!this.check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= MAX_NUM_OF_ARGUMENTS) {
                    this.report(this.peek(), "Cannot have more than " + MAX_NUM_OF_ARGUMENTS + " arguments.");
                }
                arguments.add(this.expression());
            }
            while (this.match(TokenType.COMMA));
        }
        final Token paren = this.expectAfter(TokenType.RIGHT_PAREN, "arguments");
        return new FunctionCallExpression(callee, paren, arguments);
    }

    private Expression primary() {
        if (this.match(TokenType.FALSE)) return new LiteralExpression(this.peek(), false);
        if (this.match(TokenType.TRUE)) return new LiteralExpression(this.peek(), true);
        if (this.match(TokenType.NULL)) return new LiteralExpression(this.peek(), null);
        if (this.match(TokenType.THIS)) return new ThisExpression(this.previous());
        if (this.match(TokenType.NUMBER, TokenType.STRING, TokenType.CHAR)) return new LiteralExpression(this.peek(), this.previous().literal());
        if (this.match(TokenType.IDENTIFIER)) {
            final Token next = this.peek();
            if (next.type() == TokenType.ARRAY_OPEN) {
                final Token name = this.previous();
                this.expect(TokenType.ARRAY_OPEN);
                final Expression index = this.expression();
                this.expect(TokenType.ARRAY_CLOSE);
                return new ArrayVariable(name, index);
            }
            return new VariableExpression(this.previous());
        }
        if (this.match(TokenType.LEFT_PAREN)) {
            final Expression expr = this.expression();
            this.expectAfter(TokenType.RIGHT_PAREN, "expression");
            return new GroupingExpression(expr);
        }
        if (this.match(TokenType.ARRAY)) {
            this.expect(TokenType.ARRAY_OPEN);
            final Expression sizeOrIndex = this.expression();
            this.expect(TokenType.ARRAY_CLOSE);
            return new ArrayGetExpression(sizeOrIndex);
        }
        if (this.match(TokenType.SUPER)) {
            final Token keyword = this.previous();
            this.expectAfter(TokenType.DOT, TokenType.SUPER);
            final Token method = this.expect(TokenType.IDENTIFIER, "superclass method name");
            return new SuperExpression(keyword, method);
        }
        throw this.error(this.peek(), "Expected expression, but found " + this.tokens.get(this.current));
    }

    private ParseError error(final Token token, final String message) {
        this.errorReporter.error(Phase.PARSING, token, message);
        return new ParseError();
    }

    private void report(final Token token, final String message) {
        this.errorReporter.error(Phase.PARSING, token, message);
    }

    private Token consume(final TokenType type, final String message) {
        if (this.check(type)) return this.advance();
        if (type != TokenType.SEMICOLON) throw this.error(this.peek(), message);
        return null;
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

    private Token expect(final TokenType type) {
        return this.expect(type, type.representation() + (type.keyword() ? " keyword" : ""));
    }

    private Token expect(final TokenType type, final String what) {
        return this.consume(type, "Expected " + what + ".");
    }

    private Token expectBefore(final TokenType type, final String before) {
        return this.expectAround(type, before, "before");
    }

    private Token expectAfter(final TokenType type, final TokenType after) {
        return this.expectAround(type, after.representation(), "after");
    }

    private Token expectAfter(final TokenType type, final String after) {
        return this.expectAround(type, after, "after");
    }

    private Token expectAround(final TokenType type, final String where, final String position) {
        return this.consume(type, "Expected '%s' %s %s.".formatted(type.representation(), position, where));
    }
}
