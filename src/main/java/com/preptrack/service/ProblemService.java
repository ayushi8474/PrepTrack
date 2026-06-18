package com.preptrack.preptrack.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.preptrack.preptrack.model.Problem;
import com.preptrack.preptrack.repository.ProblemRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository repository;

    // YAHA SE AUTOMATIC LOADER SHURU HOTA HAI
    @PostConstruct
    public void initDefaultData() {
        try {
            // Agar database pehle se hi bhara hua hai, toh dobara load nahi karega
            if (repository.count() > 0) {
                System.out.println("Data already exists in database. Skipping auto-load. 🚀");
                return;
            }

            System.out.println("Starting automatic data initialization from striver.csv... ⏳");
            ClassPathResource resource = new ClassPathResource("striver.csv");
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                boolean firstLine = true;
                List<Problem> list = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue; // Header row skip karega
                    }

                    // CSV lines ko split karega comma se
                    String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // safe split for commas inside quotes
                    if (data.length < 4) continue;

                    Problem p = new Problem();
                    p.setTitle(data[0].replace("\"", ""));
                    p.setDifficulty(data[1].replace("\"", ""));
                    p.setTopic(data[2].replace("\"", ""));
                    p.setLeetcodeUrl(data[3].replace("\"", ""));

                    p.setStatus("Unsolved");
                    p.setConfidencePercentage(50);
                    p.setRevisionCount(0);
                    p.setRevisionNeeded(false);

                    list.add(p);
                }

                repository.saveAll(list);
                System.out.println("Successfully loaded " + list.size() + " problems into the database! 🎉");
            }
        } catch (Exception e) {
            System.out.println("Automatic data load failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Aapka purana manual upload method (waise hi rahega)
    public void saveCsv(MultipartFile file) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        boolean firstLine = true;
        List<Problem> list = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            String[] data = line.split(",");
            Problem p = new Problem();
            p.setTitle(data[0]);
            p.setDifficulty(data[1]);
            p.setTopic(data[2]);
            p.setLeetcodeUrl(data[3]);
            p.setStatus("Unsolved");
            p.setConfidencePercentage(50);
            p.setRevisionCount(0);
            p.setRevisionNeeded(false);
            list.add(p);
        }
        repository.saveAll(list);
    }
}