import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TransposeNotes {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java TransposeNotes <input_file> <semitones> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        int semitones = Integer.parseInt(args[1]);
        String outputFile = args[2];

        try {
            String inputJson = readFile(inputFile);
            JSONArray notes = new JSONArray(inputJson);

            JSONObject result = transposeNotes(notes, semitones);

            if (result.has("error")) {
                System.out.println("Error: " + result.getString("error"));
            } else {
                writeFile(outputFile, result.getJSONArray("transposedNotes").toString(2));
                System.out.println("Transposed notes successfully written to " + outputFile);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject transposeNotes(JSONArray notes, int semitones) throws JSONException {
        JSONArray transposedNotes = new JSONArray();
        JSONObject result = new JSONObject();

        for (int i = 0; i < notes.length(); i++) {
            JSONArray note = notes.getJSONArray(i);
            int octave = note.getInt(0);
            int noteNumber = note.getInt(1);

            int transposedNoteNumber = (noteNumber + semitones) % 12;
            if (transposedNoteNumber <= 0) {
                transposedNoteNumber += 12;
            }

            int transposedOctave = octave + (noteNumber + semitones - 1) / 12;

            if (transposedOctave < -3 || transposedOctave > 5) {
                throw new JSONException("Transposed note out of keyboard range.");
            }

            transposedNotes.put(new JSONArray().put(transposedOctave).put(transposedNoteNumber));
        }

        result.put("transposedNotes", transposedNotes);
        return result;
    }

    private static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
    }

    private static void writeFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, content.getBytes());
    }
}