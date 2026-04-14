package com.fooddelivery.repository;

import com.fooddelivery.domain.repository.DataRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Generic file-backed repository using JSON persistence.
 */
public abstract class FileRepository<T> implements DataRepository<T> {

    protected final Map<String, T> store = new ConcurrentHashMap<>();

    private final String filePath;
    private final Gson gson;
    private final Type mapType;
    private volatile boolean loaded = false;

    protected FileRepository(String filePath, Type mapType) {
        this.filePath = filePath;
        this.mapType = mapType;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class,
                        new com.fooddelivery.util.LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class,
                        new com.fooddelivery.util.LocalDateAdapter())
                .create();

        ensureDataDir();
        load();
    }

    @Override
    public synchronized void save(String id, T entity) {
        ensureLoaded();
        store.put(id, entity);
        flush();
    }

    @Override
    public Optional<T> findById(String id) {
        ensureLoaded();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        ensureLoaded();
        return new ArrayList<>(store.values());
    }

    @Deprecated
    public List<T> findWhere(Predicate<T> predicate) {
        ensureLoaded();
        return store.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void delete(String id) {
        ensureLoaded();
        store.remove(id);
        flush();
    }

    @Override
    public boolean exists(String id) {
        ensureLoaded();
        return store.containsKey(id);
    }

    @Override
    public int count() {
        ensureLoaded();
        return store.size();
    }

    private void ensureDataDir() {
        try {
            Path dir = Paths.get(filePath).getParent();
            if (dir != null) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    private synchronized void load() {
        if (loaded) {
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            loaded = true;
            return;
        }

        try (Reader reader = Files.newBufferedReader(file.toPath())) {
            Map<String, T> loadedMap = gson.fromJson(reader, mapType);
            if (loadedMap != null) {
                store.putAll(loadedMap);
            }
        } catch (IOException e) {
            System.err.println("Failed to load " + filePath + ": " + e.getMessage());
        }

        loaded = true;
    }

    protected synchronized void flush() {
        ensureLoaded();

        try (Writer writer = Files.newBufferedWriter(Paths.get(filePath))) {
            gson.toJson(store, writer);
        } catch (IOException e) {
            System.err.println("Failed to save " + filePath + ": " + e.getMessage());
        }
    }

    private void ensureLoaded() {
        if (!loaded) {
            load();
        }
    }

    protected Gson getGson() {
        return gson;
    }
}