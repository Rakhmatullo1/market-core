package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.ProductProposal;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.repository.ProposalRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.ProposalService;
import com.rahmatullo.comfortmarket.service.dto.ProposalDto;
import com.rahmatullo.comfortmarket.service.dto.request.ProposalRequestDto;
import com.rahmatullo.comfortmarket.service.enums.Action;
import com.rahmatullo.comfortmarket.service.enums.ProposalStatus;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.ProposalMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final ProductRepository productRepository;
    private final PremiseRepository premiseRepository;
    private final ProposalMapper proposalMapper;
    private final AuthService authService;

    @Override
    public ProposalDto create(ProposalRequestDto proposalRequestDto) {
        log.info("Requested to create new proposal");
        ProductProposal proposal = proposalMapper.toProposal(proposalRequestDto);

        return proposalMapper.toProposalDto(proposalRepository.save(proposal));
    }

    @Override
    public List<ProposalDto> findAll(PageRequest pageRequest) {
        log.info("Requested to get all proposal");
        User user = authService.getOwner();
        return proposalRepository.
                findByToUser(user, pageRequest)
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
    public ProposalDto approveOrReject(Long id, boolean isApproved, Long premiseId) {
        log.info("Requested to approve/reject proposal");
        User user = authService.getOwner();
        ProductProposal proposal = toProposal(id);

        if(!Objects.isNull(proposal.getApprovedBy())){
            throw new ExistsException("Proposal cannot be changed, it has already approved or rejected");
        }

        proposal.setStatus(isApproved ? ProposalStatus.ACCEPTED : ProposalStatus.REJECTED);
        proposal.setApprovedBy(user.getUsername());

        Premise premise = premiseRepository.findByOwnerAndId(user, premiseId).orElseThrow(()->new NotFoundException("Premise is not found") );

        changeOnCountOfProduct(proposal, isApproved, premise);

        return proposalMapper.toProposalDto(proposal);
    }

    @Override
    public ProposalDto updateProposal(Long id, ProposalRequestDto proposalRequestDto) {
        log.info("Requested to update proposal {}", id);
        ProductProposal proposal = toProposal(id);

        if(!Objects.isNull(proposal.getApprovedBy())) {
            log.info("Unable to update because it has already approved or rejected");
            throw new ExistsException("Unable to update because it has already approved or rejected");
        }

        proposal = proposalMapper.toProposal(proposalRequestDto, proposal);
        log.info("Successfully updated");
        return proposalMapper.toProposalDto(proposalRepository.save(proposal));
    }

    private ProductProposal toProposal(Long id){
        return proposalRepository.findById(id).orElseThrow(()->new NotFoundException("Proposal is not found"));
    }

    private void changeOnCountOfProduct(ProductProposal proposal, boolean isApproved, Premise premise) {
        log.info("Requested to add/subtract on count product");
        Product product = proposal.getProduct();

        List<String> productCount = product.getCount();
        String count = productCount.stream()
                .filter(c-> Objects.equals(Long.parseLong(c.split(":")[0]), premise.getId()))
                .findFirst()
                .orElseThrow(()->new NotFoundException("Something went wrong with premise"));

        Integer oldCount = Integer.parseInt(count.split(":")[1]);

        if(oldCount<proposal.getCount() && isApproved) {
            log.info("Unable to approve");
            throw new ExistsException("you cannot approve");
        }

        int result = isApproved ?
                proposal.getAction() == Action.EXPORT
                        ?  (int) (oldCount - proposal.getCount())
                        : (int) (oldCount + proposal.getCount())
                : oldCount;
        product.getCount().remove(count);

        product.getCount().add(String.format("%s:%s", premise.getId(), result));
        productRepository.save(product);
        log.info("Successfully changed");
    }
}
