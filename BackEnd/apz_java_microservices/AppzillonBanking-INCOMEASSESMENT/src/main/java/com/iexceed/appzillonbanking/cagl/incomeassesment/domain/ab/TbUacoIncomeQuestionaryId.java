package com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab;

import jakarta.persistence.Column;
import lombok.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TbUacoIncomeQuestionaryId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID", nullable = false)
    private String appId;

    @Column(name = "APPLICATION_ID", nullable = false)
    private String applicationId;

}
