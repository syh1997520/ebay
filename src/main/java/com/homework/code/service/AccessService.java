package com.homework.code.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AccessService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String dataFilePath = "C:\\work\\test.txt";

    private Map<Long, Set<String>> userAccessMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            File dataFile = new File(dataFilePath);
            if (dataFile.exists()) {
                Map<String, List<Map<String, Object>>> loadedData = objectMapper.readValue(dataFile,
                        TypeFactory.defaultInstance().constructMapType(Map.class, String.class, List.class));
                List<Map<String, Object>> userList = loadedData.getOrDefault("users", Collections.emptyList());
                for (Map<String, Object> user : userList) {
                    Long userId = ((Number) user.get("userId")).longValue();
                    Set<String> resources = new HashSet<>((List<String>) user.get("access"));
                    userAccessMap.put(userId, resources);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading user access data", e);
        }
    }


    private synchronized void saveDataToFile() {
        try {
            Map<String, List<Map<String, Object>>> dataToSave = new HashMap<>();
            dataToSave.put("users", new ArrayList<>());
            for (Map.Entry<Long, Set<String>> entry : userAccessMap.entrySet()) {
                Map<String, Object> userJson = new HashMap<>();
                userJson.put("userId", entry.getKey());
                userJson.put("access", new ArrayList<>(entry.getValue()));
                dataToSave.get("users").add(userJson);
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(dataFilePath), dataToSave);
        } catch (IOException e) {
            throw new RuntimeException("Error saving user access data", e);
        }
    }


    public void addUserAccess(Long userId, List<String> resources) {

        List<String> distinctList = resources.stream().distinct().collect(Collectors.toList());
        Set<String> oldSet = userAccessMap.getOrDefault(userId, new HashSet<>());
        oldSet.addAll(distinctList);
        userAccessMap.put(userId, oldSet);

        saveDataToFile();
    }

    public boolean hasAccess(Long userId, String resources) {
        Set<String> set = userAccessMap.getOrDefault(userId, new HashSet<>());

        return set.contains(resources);
    }
}
