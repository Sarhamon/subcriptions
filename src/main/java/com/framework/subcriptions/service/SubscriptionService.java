package com.framework.subcriptions.service;

import com.framework.subcriptions.domain.Subscription;
import com.framework.subcriptions.dto.SubscriptionForm;
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

    @Transactional
    public List<Subscription> findAll() {
        LocalDate today = LocalDate.now();
        List<Subscription> all = repository.findAll();
        all.forEach(s -> applyRenewalIfDue(s, today));
        return all;
    }

    @Transactional
    public Subscription findById(Long id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + id));
        applyRenewalIfDue(subscription, LocalDate.now());
        return subscription;
    }

    @Transactional
    public Subscription create(SubscriptionForm form) {
        return repository.save(form.toEntity());
    }

    private void applyRenewalIfDue(Subscription subscription, LocalDate today) {
        while (!subscription.getNextRenewalDate().isAfter(today)) {
            subscription.slideToNextCycle();
        }
    }
}
