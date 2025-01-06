package com.example.bbs.service;

import com.example.bbs.pojo.Vote;
import com.example.bbs.pojo.VoteResult;

import java.util.List;

public interface VoteService {

    void addVote(Vote vote);

    List<Vote> getVotesByContentId(String contentId);

    void doVote(VoteResult voteResult);
}
