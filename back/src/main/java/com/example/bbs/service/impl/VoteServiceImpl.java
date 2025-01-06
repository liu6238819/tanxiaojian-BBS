package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.VoteMapper;
import com.example.bbs.mapper.VoteResultMapper;
import com.example.bbs.pojo.Vote;
import com.example.bbs.pojo.VoteResult;
import com.example.bbs.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    VoteMapper voteMapper;
    @Autowired
    VoteResultMapper voteResultMapper;

    @Override
    public void addVote(Vote vote) {
        voteMapper.insert(vote);
    }

    @Override
    public List<Vote> getVotesByContentId(String contentId) {
        QueryWrapper<Vote> voteQueryWrapper = new QueryWrapper<>();
        voteQueryWrapper.eq("contentId", contentId);
        return voteMapper.selectList(voteQueryWrapper);
    }

    @Override
    public void doVote(VoteResult voteResult) {
        voteResultMapper.insert(voteResult);

        String voteId = voteResult.getVoteId();
        QueryWrapper<Vote> voteQueryWrapper = new QueryWrapper<>();
        voteQueryWrapper.eq("voteId",voteId);
        Vote one = voteMapper.selectOne(voteQueryWrapper);
        one.setTicketNum(one.getTicketNum()+1);
        voteMapper.updateById(one);
    }
}
