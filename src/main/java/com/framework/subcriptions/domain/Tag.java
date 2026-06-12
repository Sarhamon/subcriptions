package com.framework.subcriptions.domain;

import lombok.Getter;

// 구독 분류용 태그 enum. 미리 정의된 목록만 체크박스/필터에 노출된다. 라벨은 화면 표시용.
@Getter
public enum Tag {
    ENTERTAINMENT("엔터테인먼트"),
    MUSIC("음악"),
    AI("AI"),
    PRODUCTIVITY("생산성"),
    GAME("게임"),
    DEV("개발");

    // 화면 표시용 한국어 라벨.
    private final String label;

    Tag(String label) {
        this.label = label;
    }
}
