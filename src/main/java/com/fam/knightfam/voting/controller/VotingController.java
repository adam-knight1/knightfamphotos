package com.fam.knightfam.voting.controller;


import com.fam.knightfam.voting.service.VotingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fam.knightfam.voting.model.Vote;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/vote")
public class VotingController {

    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    /**
     * Show the form to create a new vote.
     * GET  /vote/create
     */
    @GetMapping("/create")
    public String showCreateForm() {
        return "createvote";  // createvote.html in src/main/resources/templates
    }

    /**
     * Handle the submission of a new vote.
     * POST /vote/create
     */
    @PostMapping("/create")
    public String createVote(@RequestParam String question,
                             @RequestParam("options") List<String> options) {

        // Build the Vote entity
        Vote vote = new Vote();
        vote.setQuestion(question);
        vote.setOptions(options);

        // Persist via JPA
        votingService.createVote(vote);
        return "redirect:/vote/voting";
    }

    /**
     * Display all votes and the casting form.
     * GET /vote/voting
     */
    @GetMapping("/voting")
    public String showVotingPage(Model model) {
        List<Vote> votes = votingService.findAllVotes(); // JPA findAll()
        model.addAttribute("votes", votes);
        return "voting";     // voting.html
    }

    /**
     * Handle casting a vote.
     * POST /vote/submit
     */
    @PostMapping("/submit")
    public String submitVote(@RequestParam("voteId") Long voteId,
                             @RequestParam("selectedOption") String selectedOption) {

        votingService.submitVote(voteId, selectedOption);
        // ← redirect to the results page for this vote
        return "redirect:/vote/results/" + voteId;
    }

    @GetMapping("/results/{voteId}")
    public String showResults(@PathVariable Long voteId, Model model) {
        Vote vote = votingService.getVoteById(voteId);
        model.addAttribute("vote", vote);

        String winner = vote.getVoteCounts().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("—");
        model.addAttribute("winner", winner);

        return "results";  // renders results.html
    }
}

