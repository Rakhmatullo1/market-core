package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.ProductProposal;
import com.rahmatullo.comfortmarket.repository.ProposalRepository;
import com.rahmatullo.comfortmarket.service.ProposalService;
import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;
import com.rahmatullo.comfortmarket.service.enums.ProposalStatus;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.ProposalMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final ProposalMapper proposalMapper;

    @Override
    public ProposalDto create(ProposalRequestDto proposalRequestDto) {
        log.info("Requested to create new proposal");
        ProductProposal proposal = proposalMapper.toProposal(proposalRequestDto);

        return proposalMapper.toProposalDto(proposalRepository.save(proposal));
    }

    @Deprecated
    @Override
    public ProposalDto approveOrReject(Long id, boolean isApproved) {
        log.info("");
        ProductProposal proposal = toProposal(id);

        proposal.setStatus(isApproved ? ProposalStatus.ACCEPTED : ProposalStatus.REJECTED);
        return null;
    }

    private ProductProposal toProposal(Long id){
        return proposalRepository.findById(id).orElseThrow(()->new NotFoundException("Proposal is not found"));
    }
}
