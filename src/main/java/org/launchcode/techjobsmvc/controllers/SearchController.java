package org.launchcode.techjobsmvc.controllers;

import org.launchcode.techjobsmvc.models.Job;
import org.launchcode.techjobsmvc.models.JobData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.launchcode.techjobsmvc.controllers.ListController.columnChoices;

@Controller
@RequestMapping("search")
public class SearchController {

    @GetMapping(value = "")
    public String search(Model model) {
        model.addAttribute("columns", columnChoices);
        return "search";
    }

    // Existing method to handle search results
    @PostMapping("results")
    public String displaySearchResults(Model model, @RequestParam String searchType, @RequestParam String searchTerm) {
        List<Job> jobs;

        // Map 'skill' to 'coreCompetency' to handle the test case
        if (searchType.equals("skill")) {
            searchType = "coreCompetency";
        }

        if (searchTerm.toLowerCase().equals("all") || searchTerm.isEmpty()) {
            jobs = JobData.findAll();
        } else {
            jobs = JobData.findByColumnAndValue(searchType, searchTerm);
        }

        // Retrieve human-readable label for searchType from columnChoices
        String searchTypeDisplay = ListController.columnChoices.get(searchType);

        // Create the concatenated message: "Jobs with Skill: Ruby"
        String resultMessage = "Jobs with " + searchTypeDisplay + ": " + searchTerm;

        // Add the concatenated message to the model
        model.addAttribute("resultMessage", resultMessage);
        model.addAttribute("jobs", jobs);
        model.addAttribute("columns", ListController.columnChoices);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchTerm", searchTerm);

        return "search";
    }

    // New method to handle job listing by core competency or other columns
    @GetMapping("/list/jobs")
    public String listJobsByCoreCompetency(@RequestParam String column, @RequestParam String value, Model model) {
        List<Job> jobs;

        // Filter jobs by the selected column and value (e.g., coreCompetency and Ruby)
        if (column.equals("coreCompetency")) {
            jobs = JobData.findByColumnAndValue(column, value);
            model.addAttribute("jobs", jobs);
            model.addAttribute("title", "Jobs with " + value + " Skills");
        } else {
            // Default: if the column is not 'coreCompetency', list all jobs
            jobs = JobData.findAll();
            model.addAttribute("jobs", jobs);
        }

        return "list-jobs";  // Render the list-jobs.html template
    }
}
