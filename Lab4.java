
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lab4 {

    private static final BufferedReader console =
            new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        try {
            System.out.println("=== LABORATORNA ROBOTA 4: FILES ===");
            System.out.println("1 - Zavdannia 1, variant 2");
            System.out.println("2 - Zavdannia 2, punkt 2");
            System.out.print("Vyberit zavdannia (1 abo 2): ");

            String choice = console.readLine();

            if ("1".equals(choice)) {
                task1();
            } else if ("2".equals(choice)) {
                task2();
            } else {
                System.out.println("Nepravильnyi vybir.");
            }

        } catch (IOException e) {
            System.out.println("Pomylka vvоду-vyvodu: " + e.getMessage());
        }
    }

    // ZAVDANNIA 1: variant 2
    private static void task1() throws IOException {
        System.out.println("\n=== ZAVDANNIA 1 ===");

        Path inputPath;

        while (true) {
            System.out.print("Vvedit shliakh do vkhidnoho failu: ");
            String inputName = console.readLine();

            if (inputName == null) {
                return;
            }

            inputPath = Paths.get(inputName.trim());

            if (Files.isRegularFile(inputPath)
                    && Files.isReadable(inputPath)) {
                break;
            }

            System.out.println(
                    "Fail ne znaideno abo nedostupnyi. Sprobuit shche raz.");
        }

        List<String> reversedLines = new ArrayList<>();
        List<BigInteger> addends = new ArrayList<>();
        BigInteger sum = BigInteger.ZERO;

        try (BufferedReader reader = Files.newBufferedReader(
                inputPath, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                // Riадок, shcho mistyt lyshe tsyfry
                if (line.matches("[0-9]+")) {
                    BigInteger number = new BigInteger(line);
                    addends.add(number);
                    sum = sum.add(number);
                } else {
                    // Zminiuiemo poriadok sliv na protylezhnyi
                    String trimmed = line.trim();

                    if (trimmed.isEmpty()) {
                        reversedLines.add("");
                    } else {
                        String[] words = trimmed.split("\\s+");
                        StringBuilder result = new StringBuilder();

                        for (int i = words.length - 1; i >= 0; i--) {
                            if (result.length() > 0) {
                                result.append(" ");
                            }
                            result.append(words[i]);
                        }

                        reversedLines.add(result.toString());
                    }
                }
            }
        }

        System.out.println("\nDodatky:");
        if (addends.isEmpty()) {
            System.out.println("U faili ne znaideno riadkiv-tsyfer.");
        } else {
            for (int i = 0; i < addends.size(); i++) {
                System.out.println(addends.get(i));
            }
        }

        System.out.println("Suma: " + sum);

        Path outputPath = askOutputPath(inputPath);
        if (outputPath == null) {
            System.out.println("Zapis skasovano.");
            return;
        }

        boolean append = askWriteMode(outputPath);

        if (append) {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    outputPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                writeLines(writer, reversedLines);
            }
        } else {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    outputPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                writeLines(writer, reversedLines);
            }
        }

        System.out.println("Rezultat zapisano u: " + outputPath);
    }

    // ZAVDANNIA 2: zapys symvoliv z konsoli do failu
    private static void task2() throws IOException {
        System.out.println("\n=== ZAVDANNIA 2 ===");
        System.out.println("Vvodit tekst. Dlia zavershennia natysnit Ctrl+Z, potim Enter.");
        System.out.print("Vvedit shliakh do vykhidnoho failu: ");

        String fileName = console.readLine();

        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("Imia failu ne vказano.");
            return;
        }

        Path outputPath = Paths.get(fileName.trim());
        boolean append = askWriteMode(outputPath);

        try (BufferedWriter writer = append
                ? Files.newBufferedWriter(
                        outputPath,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND)
                : Files.newBufferedWriter(
                        outputPath,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE)) {

            int ch;

            while ((ch = console.read()) != -1) {
                writer.write(ch);
            }
        }

        System.out.println("\nVvedennia zaversheno. Rezultat zapisano u: "
                + outputPath);
    }

    // Zapyt shliakhu do vykhidnoho failu
    private static Path askOutputPath(Path inputPath) throws IOException {
        while (true) {
            System.out.print("Vvedit shliakh do vykhidnoho failu: ");
            String name = console.readLine();

            if (name == null || name.trim().isEmpty()) {
                System.out.println("Vkazhit imia failu.");
                continue;
            }

            Path outputPath = Paths.get(name.trim()).toAbsolutePath().normalize();
            Path absoluteInput = inputPath.toAbsolutePath().normalize();

            if (outputPath.equals(absoluteInput)) {
                System.out.println(
                        "Vykhidnyi fail maie vidrizniatysia vid vkhidnoho.");
                continue;
            }

            return outputPath;
        }
    }

    // Vybir: perezapysaty fail chy dozapysaty v kinets
    private static boolean askWriteMode(Path path) throws IOException {
        if (Files.exists(path)) {
            while (true) {
                System.out.print(
                        "Fail isnue. 1 - perezapysaty, 2 - dozapysaty: ");
                String mode = console.readLine();

                if ("1".equals(mode)) {
                    return false;
                }
                if ("2".equals(mode)) {
                    return true;
                }

                System.out.println("Vyberit 1 abo 2.");
            }
        }

        return false;
    }

    private static void writeLines(
            BufferedWriter writer, List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }
}
