package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.ProductProposal;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.repository.ProposalRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.ProposalService;
import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;
import com.rahmatullo.comfortmarket.service.enums.ProposalStatus;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.ProposalMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final ProductRepository productRepository;
    private final ProposalMapper proposalMapper;
    private final AuthService authService;

    @Override
    public ProposalDto create(ProposalRequestDto proposalRequestDto) {
        log.info("Requested to create new proposal");
        ProductProposal proposal = proposalMapper.toProposal(proposalRequestDto);

        return proposalMapper.toProposalDto(proposalRepository.save(proposal));
    }

    @Override
    public List<ProposalDto> findAll() {
        log.info("Requested to get all proposal");
        User user = authService.getOwner();

        return proposalRepository.
                findByToUser(user)
                .stream()
                .map(proposalMapper::toProposalDto).toList();
    }

    @Override
    public ProposalDto findById(Long id) {
        log.info("Requested to get proposal By Id {}", id);
        User user = authService.getUser();

        ProductProposal proposal = proposalRepository.findByToUserAndId(user, id).orElseThrow(()->new NotFoundException("Proposal Not found"));

        return proposalMapper.toProposalDto(proposal);
    }

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
