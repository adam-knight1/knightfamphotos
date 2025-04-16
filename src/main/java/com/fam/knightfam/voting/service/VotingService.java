package com.fam.knightfam.voting.service;


import com.fam.knightfam.voting.model.Vote;              // <-- Make sure this is the one!
import com.fam.knightfam.voting.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VotingService {
    private final VoteRepository voteRepository;

    public VotingService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public List<Vote> findAllVotes() {
        return voteRepository.findAll();
    }

    @Transactional
    public Vote createVote(Vote vote) {
        vote.getOptions().forEach(opt ->
                vote.getVoteCounts().putIfAbsent(opt, 0)
        );
        return voteRepository.save(vote);
    }

    @Transactional
    public Vote submitVote(Long voteId, String option) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid vote ID: " + voteId)
                );
        vote.getVoteCounts().merge(option, 1, Integer::sum);
        return voteRepository.save(vote);
    }

    @Transactional(readOnly = true)
    public Vote getVoteById(Long voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vote ID: " + voteId));
    }


}
