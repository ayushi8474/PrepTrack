package com.preptrack.preptrack.controller;

import com.preptrack.preptrack.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProblemRepository repository;

    @GetMapping
    public Map<String, Object> getStats() {

        Map<String, Object> stats = new HashMap<>();

        long total = repository.count();
        long solved = repository.countByStatus("Solved");
        long attempted = repository.countByStatus("Attempted");

        long easy = repository.countByDifficulty("Easy");
        long medium = repository.countByDifficulty("Medium");
        long hard = repository.countByDifficulty("Hard");

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        long revisionDue = repository.findBySolvedDateBefore(sevenDaysAgo).size();

        long lowConfidence = repository.findByConfidence("Low").size();

        double completionRate = total == 0 ? 0 : (solved * 100.0 / total);

        stats.put("totalProblems", total);
        stats.put("solved", solved);
        stats.put("attempted", attempted);
        stats.put("easy", easy);
        stats.put("medium", medium);
        stats.put("hard", hard);
        stats.put("revisionDue", revisionDue);
        stats.put("lowConfidence", lowConfidence);
        stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        return stats;
    }
}