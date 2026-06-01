-- 부팅 시 자동 실행되는 시드 데이터. Hibernate가 subscription 테이블을 만든 직후 INSERT된다.
-- 통화(KRW/USD/JPY) × 주기(MONTHLY/YEARLY) × 자동결제 on/off 조합을 골고루 포함.
INSERT INTO subscription (service_name, price, currency, billing_cycle, started_at, auto_renew) VALUES
    ('Netflix',         17000, 'KRW', 'MONTHLY', '2026-04-12', TRUE),
    ('Spotify',         10900, 'KRW', 'MONTHLY', '2026-05-03', TRUE),
    ('YouTube Premium', 14900, 'KRW', 'MONTHLY', '2026-03-20', TRUE),
    ('Claude Pro',      220,   'USD', 'YEARLY',  '2026-05-25', TRUE),
    ('Notion',             11, 'USD', 'MONTHLY', '2026-05-08', TRUE),
    ('ChatGPT Plus',       22, 'USD', 'MONTHLY', '2026-05-04', FALSE),
    ('Nintendo Online',  2400, 'JPY', 'YEARLY',  '2025-11-30', FALSE);
