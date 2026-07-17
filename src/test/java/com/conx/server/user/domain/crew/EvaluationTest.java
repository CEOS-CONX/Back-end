package com.conx.server.user.domain.crew;

import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.company.Company;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EvaluationTest {

    @Test
    @DisplayName("프로젝트 평가의 다섯 항목 평균을 계산한다")
    void calculateProjectEvaluationMean() {
        // given
        Project project = mock(Project.class);
        Crew crew = mock(Crew.class);
        Company company = mock(Company.class);

        // when
        Evaluation evaluation = Evaluation.create(
                project,
                crew,
                company,
                5,
                4,
                5,
                4,
                5
        );

        // then
        assertThat(evaluation.getProject())
                .isEqualTo(project);

        assertThat(evaluation.getCrew())
                .isEqualTo(crew);

        assertThat(evaluation.getCompany())
                .isEqualTo(company);

        assertThat(evaluation.getCompleteness())
                .isEqualTo(5);

        assertThat(evaluation.getSchedule())
                .isEqualTo(4);

        assertThat(evaluation.getAbility())
                .isEqualTo(5);

        assertThat(evaluation.getReCooperation())
                .isEqualTo(4);

        assertThat(evaluation.getCommunication())
                .isEqualTo(5);

        assertThat(evaluation.getMean())
                .isEqualTo(4.6);
    }

    @Test
    @DisplayName("모든 평가 항목이 1점이면 평균은 1점이다")
    void calculateMinimumEvaluationMean() {
        // given
        Project project = mock(Project.class);
        Crew crew = mock(Crew.class);
        Company company = mock(Company.class);

        // when
        Evaluation evaluation = Evaluation.create(
                project,
                crew,
                company,
                1,
                1,
                1,
                1,
                1
        );

        // then
        assertThat(evaluation.getMean())
                .isEqualTo(1.0);
    }

    @Test
    @DisplayName("평가 한 건은 다른 프로젝트 평가와 독립적으로 계산된다")
    void evaluationsAreCalculatedIndependently() {
        // given
        Project firstProject = mock(Project.class);
        Project secondProject = mock(Project.class);
        Crew crew = mock(Crew.class);
        Company company = mock(Company.class);

        // when
        Evaluation firstEvaluation = Evaluation.create(
                firstProject,
                crew,
                company,
                5,
                4,
                5,
                4,
                5
        );

        Evaluation secondEvaluation = Evaluation.create(
                secondProject,
                crew,
                company,
                3,
                4,
                3,
                4,
                3
        );

        // then
        assertThat(firstEvaluation.getMean())
                .isEqualTo(4.6);

        assertThat(secondEvaluation.getMean())
                .isEqualTo(3.4);

        assertThat(firstEvaluation.getProject())
                .isNotEqualTo(secondEvaluation.getProject());
    }
}