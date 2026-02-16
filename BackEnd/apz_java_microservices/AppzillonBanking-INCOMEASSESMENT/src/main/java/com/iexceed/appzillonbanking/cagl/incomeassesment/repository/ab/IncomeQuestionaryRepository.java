package com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab;

import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.TbUacoIncomeQuestionary;
import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.TbUacoIncomeQuestionaryId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IncomeQuestionaryRepository extends CrudRepository<TbUacoIncomeQuestionary, TbUacoIncomeQuestionaryId> {

    Optional<TbUacoIncomeQuestionary> findByApplicationId(String applicationId);

}