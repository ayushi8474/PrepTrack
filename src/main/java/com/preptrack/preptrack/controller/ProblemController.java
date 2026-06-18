package com.preptrack.preptrack.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.preptrack.preptrack.model.Problem;
import com.preptrack.preptrack.repository.ProblemRepository;
import com.preptrack.preptrack.service.ProblemService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/problems")
public class ProblemController {

    @Autowired
private ProblemRepository repository;

@Autowired
private ProblemService problemService;

    @GetMapping
    public List<Problem> getAllProblems() {
        return repository.findAll();
    }

    @GetMapping("/difficulty/{difficulty}")
    public List<Problem> getProblemsByDifficulty(@PathVariable String difficulty) {
        return repository.findByDifficulty(difficulty);
    }

    @GetMapping("/status/{status}")
    public List<Problem> getProblemsByStatus(@PathVariable String status) {
        return repository.findByStatus(status);
    }

    @GetMapping("/revision/due")
    public List<Problem> getRevisionDueProblems() {

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        return repository.findBySolvedDateBefore(sevenDaysAgo);
    }

    @PostMapping
    public Problem addProblem(@RequestBody Problem problem) {

        if (problem.getSolvedDate() == null) {
            problem.setSolvedDate(LocalDate.now());
        }

        // Automatic Revision Logic
        if (problem.getConfidencePercentage() != null) {
            problem.setRevisionNeeded(problem.getConfidencePercentage() < 70);
        }

        // Default Revision Count
        if (problem.getRevisionCount() == null) {
            problem.setRevisionCount(0);
        }

        return repository.save(problem);
    }

    @PutMapping("/{id}")
    public Problem updateProblem(
            @PathVariable Long id,
            @RequestBody Problem updatedProblem) {

        return repository.findById(id).map(problem -> {

            problem.setTitle(updatedProblem.getTitle());

            // LeetCode Difficulty
            problem.setDifficulty(updatedProblem.getDifficulty());

            // Personal Difficulty
            problem.setPersonalDifficulty(
                    updatedProblem.getPersonalDifficulty());

            problem.setTopic(updatedProblem.getTopic());

            problem.setCompany(updatedProblem.getCompany());

            problem.setStatus(updatedProblem.getStatus());

            problem.setLeetcodeUrl(
                    updatedProblem.getLeetcodeUrl());

            problem.setConfidence(
                    updatedProblem.getConfidence());

            problem.setConfidencePercentage(
                    updatedProblem.getConfidencePercentage());

            problem.setRevisionCount(
                    updatedProblem.getRevisionCount());

            problem.setSolvedDate(
                    updatedProblem.getSolvedDate());

            problem.setNotes(
                    updatedProblem.getNotes());

            // Automatic Revision Logic
            if (updatedProblem.getConfidencePercentage() != null) {
                problem.setRevisionNeeded(
                        updatedProblem.getConfidencePercentage() < 70);
            }

            return repository.save(problem);

        }).orElseThrow(() ->
                new RuntimeException(
                        "Problem not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    public String deleteProblem(@PathVariable Long id) {

        repository.deleteById(id);

        return "Problem deleted successfully!";
    }
    @GetMapping("/search")
public List<Problem> searchProblems(
        @RequestParam String title) {

    return repository.findByTitleContainingIgnoreCase(title);
}
@GetMapping("/dashboard")
public Map<String, Object> getDashboardStats() {

    Map<String, Object> stats = new HashMap<>();

    List<Problem> allProblems = repository.findAll();

    long totalQuestions = allProblems.size();

    long needRevision =
            allProblems.stream()
                    .filter(p ->
                            p.getConfidencePercentage() != null &&
                            p.getConfidencePercentage() < 70)
                    .count();

    long mastered =
            allProblems.stream()
                    .filter(p ->
                            p.getConfidencePercentage() != null &&
                            p.getConfidencePercentage() >= 90 &&
                            p.getRevisionCount() != null &&
                            p.getRevisionCount() >= 2)
                    .count();

    double avgConfidence =
            allProblems.stream()
                    .filter(p -> p.getConfidencePercentage() != null)
                    .mapToInt(Problem::getConfidencePercentage)
                    .average()
                    .orElse(0);

    stats.put("totalQuestions", totalQuestions);
    stats.put("needRevision", needRevision);
    stats.put("mastered", mastered);
    stats.put("avgConfidence", avgConfidence);

    return stats;
}
   @PostMapping("/sync-full")
public String syncFullLeetCode(@RequestBody Map<String, Object> payload) {

    try {
        Integer easy = (Integer) payload.get("easy");
        Integer medium = (Integer) payload.get("medium");
        Integer hard = (Integer) payload.get("hard");

        return "Synced Successfully 🚀 | Easy: " + easy +
                " Medium: " + medium +
                " Hard: " + hard;

    } catch (Exception e) {
        return "Sync failed: " + e.getMessage();
    }
}

private int extract(String response, String key) {
    try {
        int index = response.indexOf(key);
        if (index == -1) return 0;

        String sub = response.substring(index);

        StringBuilder num = new StringBuilder();

        for (char c : sub.toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
            } else if (num.length() > 0) {
                break;
            }
        }

        return num.length() == 0 ? 0 : Integer.parseInt(num.toString());

    } catch (Exception e) {
        return 0;
    }
}
@PostMapping("/upload-csv")
public String uploadCsv(@RequestParam("file") MultipartFile file) {
    try {
        problemService.saveCsv(file);
        return "CSV uploaded successfully 🚀";
    } catch (Exception e) {
        return "Upload failed: " + e.getMessage();
    }
}
}