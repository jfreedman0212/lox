package dev.freedman.jlox;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
                    .map(Arguments::of);
        }
    }

    @DisplayName("Happy Path Test")
    @ParameterizedTest(name = "{index}: {0}")
    @ArgumentsSource(HappyPathFileNamesArgumentsProvider.class)
    public void HappyPathTests(final String fileName) throws IOException, InterpreterException {
        // Act
        final String[] args = { fileName };
        final int result = JLox.run(args);
        // Assert
        Assertions.assertEquals(0, result);
    }
}
