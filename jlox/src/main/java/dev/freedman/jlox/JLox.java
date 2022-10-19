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
                run(new String(bytes, Charset.defaultCharset()));
            } catch (final ScannerException scannerException) {
                reportError(scannerException.getErrors());
                System.out.println("Exiting...");
                System.exit(65); // EX_DATAERR
            }
        } else {
            // otherwise, read-evaluate-print loop (REPL)
            // until the user ends stdin by ^D
            final InputStreamReader input = new InputStreamReader(System.in);
            final BufferedReader reader = new BufferedReader(input);
            while (true) {
                System.out.print("> ");
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                try {
                    run(line);
                } catch (final ScannerException scannerException) {
                    reportError(scannerException.getErrors());
                }
            }
        }
    }

    private static void run(final String source) throws ScannerException {
        final Scanner scanner = new Scanner(source);
        final List<Token> tokens = scanner.scanTokens();
        System.out.println(tokens);
    }

    private static void reportError(final List<ScannerError> errors) {
        System.out.println("The following errors occurred:");
        for (final ScannerError error : errors) {
            if (error instanceof ScannerError.InvalidCharacter invalidCharacter) {
                System.out.printf("Line %d: Invalid character %c\n", invalidCharacter.line(), invalidCharacter.c());
            } else if (error instanceof ScannerError.UnterminatedString unterminatedString) {
                System.out.printf("Line %d: Unterminated string %c\n", unterminatedString.line());
            }
        }
    }
}