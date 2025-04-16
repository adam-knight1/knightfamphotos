// src/main/java/com/fam/knightfam/model/Vote.java
package com.fam.knightfam.voting.model;

import jakarta.persistence.*;
import java.util.*;



@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "vote_options",
            joinColumns = @JoinColumn(name = "vote_id")
    )
    @Column(name = "option_text")
    private List<String> options = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "vote_counts",
            joinColumns = @JoinColumn(name = "vote_id")
    )
    @MapKeyColumn(name = "option_text")
    @Column(name = "count")
    private Map<String, Integer> voteCounts = new HashMap<>();

    // No‑arg constructor for JPA
    public Vote() {}

    // Convenience constructor to pre‑populate voteCounts
    public Vote(String question, List<String> options) {
        this.question = question;
        this.options = new ArrayList<>(options);
        options.forEach(opt -> this.voteCounts.put(opt, 0));
    }

    // —— Getters ——
    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public Map<String, Integer> getVoteCounts() {
        return voteCounts;
    }

    // —— Setters ——
    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setVoteCounts(Map<String, Integer> voteCounts) {
        this.voteCounts = voteCounts;
    }

    // —— Helper ——
    public void incrementCount(String option) {
        this.voteCounts.merge(option, 1, Integer::sum);
    }
}


