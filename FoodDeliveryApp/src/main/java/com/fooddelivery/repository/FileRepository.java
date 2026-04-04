package com.fooddelivery.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Generic JSON file-backed repository.
 * All entities are stored in memory (ConcurrentHashMap) and flushed to
 * a JSON file on every write.  Reads load from file on first access.
 *
 * @param <T>  The domain entity type
 */
public abstract class FileRepository<T> {

    protected final Map<String, T> store = new ConcurrentHashMap<>();
    private final String filePath;
    private final Gson   gson;
    private final Type   mapType;
    private boolean      loaded = false;

    protected FileRepository(String filePath, Type mapType) {
        this.filePath = filePath;
        this.mapType  = mapType;
        this.gson     = new GsonBuilder()
                            .setPrettyPrinting()
                            .registerTypeAdapter(LocalDateTime.class,
                                new com.fooddelivery.util.LocalDateTimeAdapter())
                            .registerTypeAdapter(LocalDate.class,
                                new com.fooddelivery.util.LocalDateAdapter())
                            .create();
        ensureDataDir();
        load();
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public void save(String id, T entity) {
        ensureLoaded();
        store.put(id, entity);
        flush();
    }

    public Optional<T> findById(String id) {
        ensureLoaded();
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        ensureLoaded();
        return new ArrayList<>(store.values());
    }

    public List<T> findWhere(Predicate<T> predicate) {
        ensureLoaded();
        return store.values().stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
    }

    public void delete(String id) {
        ensureLoaded();
        store.remove(id);
        flush();
    }

    public boolean exists(String id) {
        ensureLoaded();
        return store.containsKey(id);
    }

    public int count() {
        ensureLoaded();
        return store.size();
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    private void ensureDataDir() {
        try {
            Path dir = Paths.get(filePath).getParent();
            if (dir != null) Files.createDirectories(dir);
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(filePath);
        if (!file.exists()) { loaded = true; return; }
        try (Reader reader = new FileReader(file)) {
            Map<String, T> loaded = gson.fromJson(reader, mapType);
            if (loaded != null) store.putAll(loaded);
        } catch (IOException e) {
            System.err.println("Failed to load " + filePath + ": " + e.getMessage());
        }
        loaded = true;
    }

    protected void flush() {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(store, writer);
        } catch (IOException e) {
            System.err.println("Failed to save " + filePath + ": " + e.getMessage());
        }
    }

    private void ensureLoaded() {
        if (!loaded) load();
    }

    protected Gson getGson() { return gson; }
}
