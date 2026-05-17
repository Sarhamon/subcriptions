package com.framework.subcriptions.service;

import com.framework.subcriptions.domain.Subscription;
import com.framework.subcriptions.domain.SubscriptionNotFoundException;
import com.framework.subcriptions.dto.SubscriptionForm;
import com.framework.subcriptions.dto.SubscriptionView;
import com.framework.subcriptions.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// 구독 도메인의 비즈니스 로직 계층. 기본 readOnly 트랜잭션, 변경 메서드만 @Transactional로 덮어씀.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final ExchangeRateService exchangeRateService;

    // 전체 조회. 조회 시점에 지난 갱신일을 한꺼번에 따라잡아 화면 일관성을 보장.
    @Transactional
    public List<SubscriptionView> findAll() {
        LocalDate today = LocalDate.now();
        List<Subscription> all = repository.findAll();
        all.forEach(s -> applyRenewalIfDue(s, today));
        return all.stream()
                .map(s -> new SubscriptionView(s, exchangeRateService))
                .toList();
    }

    // 단건 조회. 없으면 404 도메인 예외. 조회 시점에 갱신일 보정도 함께 적용.
    @Transactional
    public SubscriptionView findById(Long id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(id));
        applyRenewalIfDue(subscription, LocalDate.now());
        return new SubscriptionView(subscription, exchangeRateService);
    }

    // 신규 등록. 폼 DTO를 엔티티로 변환해 저장.
    @Transactional
    public Subscription create(SubscriptionForm form) {
        return repository.save(form.toEntity());
    }

    // 수정. 더티 체킹을 활용하므로 save 호출 없이 필드 갱신만으로 반영된다.
    @Transactional
    public Subscription update(Long id, SubscriptionForm form) {
        Subscription existing = repository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(id));
        existing.updateDetails(
                form.getServiceName(),
                form.getPrice(),
                form.getCurrency(),
                form.getBillingCycle(),
                form.getStartedAt(),
                form.isAutoRenew()
        );
        return existing;
    }

    // 삭제. 존재 여부를 먼저 확인해 일관된 404 예외 메시지를 제공.
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new SubscriptionNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // 다음 갱신일이 이미 지났다면 오늘 이후가 될 때까지 주기를 반복적으로 밀어준다.
    private void applyRenewalIfDue(Subscription subscription, LocalDate today) {
        while (!subscription.getNextRenewalDate().isAfter(today)) {
            subscription.slideToNextCycle();
        }
    }
}
