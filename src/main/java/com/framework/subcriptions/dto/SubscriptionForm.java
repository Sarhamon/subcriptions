package com.framework.subcriptions.dto;

import com.framework.subcriptions.domain.BillingCycle;
import com.framework.subcriptions.domain.Currency;
import com.framework.subcriptions.domain.Subscription;
import com.framework.subcriptions.domain.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// 등록/수정 폼에서 받는 입력 DTO. Spring MVC가 form 필드를 setter로 바인딩한다.
@Getter
@Setter
@NoArgsConstructor
public class SubscriptionForm {

    private String serviceName;
    private Integer price;
    private Currency currency;
    private BillingCycle billingCycle;
    private LocalDate startedAt;
    private boolean autoRenew;
    // 체크된 태그 값들이 바인딩된다. 하나도 안 고르면 null이 들어올 수 있다.
    private Set<Tag> tags;

    // 폼 값으로 신규 엔티티를 만든다. 생성자/빌더 캡슐화 덕에 여기서 한 곳에서만 호출.
    public Subscription toEntity() {
        return Subscription.builder()
                .serviceName(serviceName)
                .price(price)
                .currency(currency)
                .billingCycle(billingCycle)
                .startedAt(startedAt)
                .autoRenew(autoRenew)
                .tags(tags == null ? new HashSet<>() : tags)
                .build();
    }
}
