package Data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GsonRepo<T> extends Repo<T>{

    /**
     * GSON object used to convert java objects to their json representation,
     * and json to objects.
     */
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public GsonRepo(Class<T> elementType) {
        super(elementType);
    }

    /**
     * Method for reading from json file.
     * @param file - the file we read from
     * @throws IOException if file doesn't exist, or is empty
     */
    public void loadFromFile(File file) throws IOException {
        try (var reader = new FileReader(file)) {
            var listType = TypeToken.getParameterized(List.class, elementType).getType();
            elements = GSON.fromJson(reader, listType);
        }
    }

    /**
     * Method for writing to json file
     * @param file - the file we write to
     * @throws IOException if file isn't valid
     */
    public void saveToFile(File file) throws IOException {
        try (var writer = new FileWriter(file)) {
            GSON.toJson(elements, writer);
        }
    }
}
