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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final ExchangeRateService exchangeRateService;

    @Transactional
    public List<SubscriptionView> findAll() {
        LocalDate today = LocalDate.now();
        List<Subscription> all = repository.findAll();
        all.forEach(s -> applyRenewalIfDue(s, today));
        return all.stream()
                .map(s -> new SubscriptionView(s, exchangeRateService))
                .toList();
    }

    @Transactional
    public SubscriptionView findById(Long id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(id));
        applyRenewalIfDue(subscription, LocalDate.now());
        return new SubscriptionView(subscription, exchangeRateService);
    }

    @Transactional
    public Subscription create(SubscriptionForm form) {
        return repository.save(form.toEntity());
    }

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

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new SubscriptionNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private void applyRenewalIfDue(Subscription subscription, LocalDate today) {
        while (!subscription.getNextRenewalDate().isAfter(today)) {
            subscription.slideToNextCycle();
        }
    }
}
