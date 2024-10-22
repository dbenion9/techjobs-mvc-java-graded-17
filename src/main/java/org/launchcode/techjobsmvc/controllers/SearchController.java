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


/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("search")
public class SearchController {

    @GetMapping(value = "")
    public String search(Model model) {
        model.addAttribute("columns", columnChoices);
        return "search";
    }

    // TODO #3 - Create a handler to process a search request and render the updated search view.

    @PostMapping("results")
    public String displaySearchResults(Model model, @RequestParam String searchType, @RequestParam String searchTerm) {
        List<Job> jobs;

        // Check if the search term is "all" or empty, in which case return all jobs
        if (searchTerm.toLowerCase().equals("all") || searchTerm.isEmpty()) {
            jobs = JobData.findAll();
        } else {
            // Otherwise, find jobs by the selected search type and term
            jobs = JobData.findByColumnAndValue(searchType, searchTerm);
        }

        // Print out the size of the jobs list
        //System.out.println("Jobs found: " + jobs.size());


        // Add the jobs and column choices to the model to be displayed in the view
        model.addAttribute("jobs", jobs);
        model.addAttribute("columns", ListController.columnChoices);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchTerm", searchTerm);

        // Return the search view template to display the results
        return "search";
    }

}


