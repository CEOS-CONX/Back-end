package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSettlement;
import com.conx.server.user.domain.company.Company;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Comparator;

public record SubsidyStatusWrapperDTO(
        long totalSubsidy,
        long expectedSubsidy,
        LocalDate nextExpectedSubsidy,
        long thisMonthSubsidy
) {
    public static SubsidyStatusWrapperDTO from(Company company, Page<ProjectSettlement> settlements){
        long totalSubsidy = company.getTotalExpenditure();

        long expectedSubsidy = settlements.stream().filter(
                ProjectSettlement::isWaiting)
                .mapToLong(ProjectSettlement::getSubsidy)
                .sum();

        LocalDate nextExpectedSubsidy = settlements.stream()
                .filter(ProjectSettlement::isWaiting)
                .min(Comparator.comparing(ProjectSettlement::getExpectedPaymentDate))
                .map(ProjectSettlement::getExpectedPaymentDate)
                .orElse(null);

        long thisMonthSubsidy = settlements.stream()
                .filter(ProjectSettlement::isPaid)
                .filter(ProjectSettlement::isInThisMonth)
                .mapToLong(ProjectSettlement::getSubsidy)
                .sum();

        return new SubsidyStatusWrapperDTO(totalSubsidy, expectedSubsidy, nextExpectedSubsidy, thisMonthSubsidy);
    }

}