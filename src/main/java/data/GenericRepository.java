package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Generic repository class for managing collections of objects with JSON persistence.
 *
 * @param <T> the type of elements stored in the repository
 */
public class GenericRepository<T> {
    /**
     * GSON object for JSON serialization and deserialization.
     */
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * The type of elements stored in the repository.
     */
    private final Class<T> elementType;

    /**
     * The list of elements in the repository.
     */
    private List<T> elements;

    /**
     * Constructs a new repository for the specified element type.
     *
     * @param elementType the class of elements to be stored
     */
    public GenericRepository(@NonNull Class<T> elementType) {
        this.elementType = elementType;
        this.elements = new ArrayList<>();
    }

    /**
     * Returns an unmodifiable view of the elements in the repository.
     *
     * @return list of elements
     */
    public List<T> getElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * Returns the number of elements in the repository.
     *
     * @return the size of the repository
     */
    public int size() {
        return elements.size();
    }

    /**
     * Adds an element to the repository.
     *
     * @param element the element to add
     * @throws IllegalArgumentException if the element is null
     */
    public void add(@NonNull T element) {
        elements.add(element);
    }

    /**
     * Removes all elements from the repository.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Loads elements from a JSON file.
     *
     * @param path the path to the JSON file
     * @throws IOException if an I/O error occurs
     */
    public void loadFromFile(@NonNull Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            var listType = TypeToken.getParameterized(List.class, elementType).getType();
            elements = (List<T>) Optional.ofNullable(GSON.fromJson(reader, listType))
                    .orElse(new ArrayList<>());
        }
    }

    /**
     * Loads elements from a JSON file.
     *
     * @param file the file to read from
     * @throws IOException if an I/O error occurs
     */
    public void loadFromFile(@NonNull File file) throws IOException {
        loadFromFile(file.toPath());
    }

    /**
     * Saves elements to a JSON file.
     *
     * @param path the path to save the JSON file
     * @throws IOException if an I/O error occurs
     */
    public void saveToFile(@NonNull Path path) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(elements, writer);
        }
    }

    /**
     * Saves elements to a JSON file.
     *
     * @param file the file to write to
     * @throws IOException if an I/O error occurs
     */
    public void saveToFile(@NonNull File file) throws IOException {
        saveToFile(file.toPath());
    }
}
