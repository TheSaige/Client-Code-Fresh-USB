import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class ATFClient {

    private static final Random random = new Random();

    public static void main(String[] args) {
        String userHome = System.getProperty("user.home");

        List<Path> targetFolders = List.of(
                Paths.get(userHome, "Desktop"),
                Paths.get(userHome, "Documents"),
                Paths.get(userHome, "Downloads"),
                Paths.get(userHome, "Pictures"),
                Paths.get(userHome, "Videos"),
                Paths.get(userHome, "Music")
        );

        for (Path folder : targetFolders) {
            createAlphabetATFs(folder);
        }

        System.out.println("Message-named ATFs created.");
    }

    private static void createAlphabetATFs(Path folder) {
        if (!Files.exists(folder)) return;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        List<String> messages = List.of(
                // Original set
                "Nope not here",
                "Wait what are you looking for again",
                "Nothing to see here",
                "This wasnt the file you wanted",
                "You can stop checking now",
                "Still clean Still empty",
                "Try another folder",
                "Yeah I checked already",
                "Old files Gone",
                "This PC was cleaned on %s",

                // New 20
                "Already checked nothing here",
                "You are very thorough",
                "No files survived",
                "This folder is empty on purpose",
                "Nothing left to find",
                "Cleanup was successful",
                "All done move along",
                "Previous files removed",
                "Fresh start achieved",
                "This space intentionally blank",
                "Still looking huh",
                "Everything old is gone",
                "Clean slate confirmed",
                "You missed it it was here",
                "Files were here once",
                "Nothing but vibes now",
                "System reset complete",
                "All clear no artifacts",
                "Folder verified clean",
                "Nothing to see move on"
        );

        for (char c = 'A'; c <= 'Z'; c++) {
            String baseMessage = messages.get(random.nextInt(messages.size()));
            String timestamp = LocalDateTime.now().format(formatter);

            String message = baseMessage.contains("%s")
                    ? String.format(baseMessage, timestamp)
                    : baseMessage;

            String safeFileName = sanitizeFileName(message) + ".txt";
            Path atfFile = folder.resolve(safeFileName);

            try {
                Files.writeString(
                        atfFile,
                        message,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            } catch (IOException ignored) {}
        }
    }

    private static String sanitizeFileName(String input) {
        return input
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_")
                .trim();
    }
}
