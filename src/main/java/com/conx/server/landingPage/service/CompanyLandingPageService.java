package com.conx.server.landingPage.service;

import com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.user.repository.CrewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyLandingPageService {

    private final CrewRepository crewRepository;

    public CompanyLandingPageService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    @Transactional(readOnly = true)
    public List<CrewWrapperForLandingPageDTO> landing(IndustryForLandingPage category){
        if (category.equals(IndustryForLandingPage.ALL)){
            return crewRepository.findAllActiveCrewsWithEvaluation();
        } else {
            return crewRepository.findActiveCrewsByCategoryWithEvaluation(category.toIndustry());
        }
    }
}
