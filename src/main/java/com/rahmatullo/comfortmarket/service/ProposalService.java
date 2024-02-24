package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.ProposalRequestDto;

public interface ProposalService {
     ProposalDto create(ProposalRequestDto proposalRequestDto);

     @Deprecated
     ProposalDto approveOrReject(Long id, boolean isApproved);
}
