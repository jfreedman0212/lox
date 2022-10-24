package dev.freedman.jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JLox {
    public static void main(final String[] args) throws IOException {
        if (args.length > 1) {
            // invalid usage, bail
            System.out.println("Usage: jlox [script]");
            System.exit(64); // EX_USAGE
        } else if (args.length == 1) {
            // read code from a file and run that file
            final byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
            try {
                final List<Stmt> statements = getExecutableStatements(new String(bytes, Charset.defaultCharset()));
                final Interpreter interpreter = new Interpreter();
                for (final Stmt statement : statements) {
                    interpreter.execute(statement);
                }
            } catch (final InterpreterException e) {
                reportError(e.getErrors());
                System.exit(65); // EX_DATAERR
            }
        } else {
            // otherwise, read-evaluate-print loop (REPL)
            // until the user ends stdin by ^D
            final InputStreamReader input = new InputStreamReader(System.in);
            final BufferedReader reader = new BufferedReader(input);
            // use the same interpreter (with same internal state) for each
            // line that gets read by the REPL. This way, variables are maintained
            // across each line. this goes against the Lox spec, but I like this more,
            // so god dammit I'm going to do it
            final Interpreter interpreter = new Interpreter();
            while (true) {
                System.out.print("> ");
                final String line = reader.readLine();
                if (line == null) {
                    System.out.println(); // print a new line before exiting
                    break;
                }
                try {
                    final List<Stmt> statements = getExecutableStatements(line);
                    for (final Stmt statement : statements) {
                        interpreter.execute(statement);
                    }
                } catch (final InterpreterException e) {
                    reportError(e.getErrors());
                }
            }
        }
    }

    private static List<Stmt> getExecutableStatements(final String source) throws InterpreterException {
        final Scanner scanner = new Scanner(source);
        final List<Token> tokens = scanner.scanTokens();
        final Parser parser = new Parser(tokens);
        return parser.parse();
    }

    private static void reportError(final List<InterpreterIssue> errors) {
        System.out.println("The following errors occurred:");
        for (final InterpreterIssue error : errors) {
            System.out.println(error);
        }
    }
}