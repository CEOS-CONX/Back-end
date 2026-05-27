package com.conx.server.landingPage.service;

import com.conx.server.landingPage.dto.CrewWrapperDTOForDashboard;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.user.repository.CrewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyLandingService {

    private final CrewRepository crewRepository;

    public CompanyLandingService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    @Transactional(readOnly = true)
    public List<CrewWrapperDTOForDashboard> landing(IndustryForLandingPage category){
        if (category.equals(IndustryForLandingPage.ALL)){
            return crewRepository.findAllActiveCrewsWithEvaluation();
        } else {
            return crewRepository.findActiveCrewsByCategoryWithEvaluation(category.toIndustry());
        }
    }
}
