package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProposalService {
     ProposalDto create(ProposalRequestDto proposalRequestDto);

     List<ProposalDto> findAll(PageRequest pageRequest);

     ProposalDto findById(Long id);

     ProposalDto approveOrReject(Long id, boolean isApproved);

     ProposalDto updateProposal(Long id, ProposalRequestDto proposalRequestDto);
}
