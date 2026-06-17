package com.preptrack.preptrack.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.preptrack.preptrack.model.Problem;
import com.preptrack.preptrack.repository.ProblemRepository;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository repository;

    public void saveCsv(MultipartFile file) throws Exception {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream())
        );

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