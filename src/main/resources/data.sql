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

-- 위 INSERT 순서대로 부여된 id(1~7)에 분류 태그를 연결한다 (subscription_tags 조인 테이블).
INSERT INTO subscription_tags (subscription_id, tag) VALUES
    (1, 'ENTERTAINMENT'),
    (2, 'MUSIC'),
    (3, 'ENTERTAINMENT'), (3, 'MUSIC'),
    (4, 'AI'),
    (5, 'PRODUCTIVITY'),
    (6, 'AI'),
    (7, 'GAME');

-- 일부 구독(id 1, 4)에 달린 댓글 시드. comment 테이블의 subscription_id가 구독을 가리킨다.
INSERT INTO comment (subscription_id, nickname, body) VALUES
    (1, '집사', '가족 계정 같이 쓰는 중'),
    (1, '나',   '4K 요금제로 변경 고려'),
    (4, '나',   '연간 결제가 더 저렴함');
