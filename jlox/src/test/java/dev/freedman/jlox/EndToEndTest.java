package dev.freedman.jlox;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class EndToEndTest {
    private static final FileFilter LOX_FILE_FILTER = new FileFilter() {
        public boolean accept(File file) {
            return file.isFile() && file.getName().endsWith(".lox");
        }
    };

    private static final class HappyPathFileNamesArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) throws Exception {
            final File happyPathDirectory = new File("src/test/resources/happy_path_files");
            final File[] loxFiles = happyPathDirectory.listFiles(LOX_FILE_FILTER);
            return Stream.of(loxFiles)
                    .map(File::getAbsolutePath)
                    .map((fileName) -> {
                        final String fileContents;
                        try {
                            fileContents = new String(Files.readAllBytes(Paths.get(fileName)),
                                    Charset.defaultCharset());
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                        return Arguments.of(Named.of(fileName, fileContents));
                    });
        }
    }

    @DisplayName("Happy Path Test")
    @ParameterizedTest(name = "{index}: {0}")
    @ArgumentsSource(HappyPathFileNamesArgumentsProvider.class)
    public void HappyPathTests(final String fileContents) throws IOException, InterpreterException {
        // Act
        final Scanner scanner = new Scanner(fileContents);
        final Parser parser = new Parser(scanner.scanTokens());
        final Interpreter interpreter = new Interpreter();
        final List<Statement> statements = parser.parse();
        for (final Statement statement : statements) {
            interpreter.execute(statement);
        }
        // no distinct Assert state because all we expect is for everything to work.
        // if it makes it here, then everything is okay. otherwise, the test fails
    }
}
