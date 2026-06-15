package com.framework.subcriptions.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// /api/subscriptions REST API 통합 테스트. 실제 컨텍스트 + H2 + data.sql 시드 위에서 검증.
@SpringBootTest
@AutoConfigureMockMvc
class SubscriptionApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void index_목록을_JSON_배열로_반환한다() throws Exception {
        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].serviceName").exists());
    }

    @Test
    void show_존재하는_단건을_반환한다() throws Exception {
        mockMvc.perform(get("/api/subscriptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serviceName").exists());
    }

    @Test
    void show_없는_id는_404_JSON을_반환한다() throws Exception {
        mockMvc.perform(get("/api/subscriptions/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void create_신규_구독을_생성하고_201을_반환한다() throws Exception {
        String body = """
                {"serviceName":"TestSub","price":9900,"currency":"KRW",
                 "billingCycle":"MONTHLY","startedAt":"2026-06-15",
                 "autoRenew":true,"tags":["PRODUCTIVITY"]}""";

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.serviceName").value("TestSub"))
                .andExpect(jsonPath("$.price").value(9900));
    }

    @Test
    void update_생성한_구독을_수정하고_변경을_반영한다() throws Exception {
        long id = createAndGetId();

        String body = """
                {"serviceName":"Edited","price":19900,"currency":"USD",
                 "billingCycle":"YEARLY","startedAt":"2026-06-15",
                 "autoRenew":false,"tags":["AI"]}""";

        mockMvc.perform(patch("/api/subscriptions/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("Edited"))
                .andExpect(jsonPath("$.price").value(19900))
                .andExpect(jsonPath("$.billingCycle").value("YEARLY"));
    }

    @Test
    void delete_생성한_구독을_삭제하면_204이고_재조회는_404다() throws Exception {
        long id = createAndGetId();

        mockMvc.perform(delete("/api/subscriptions/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/subscriptions/" + id))
                .andExpect(status().isNotFound());
    }

    // 쓰기 테스트가 시드 데이터에 의존하지 않도록, 테스트용 구독을 만들고 그 id를 돌려준다.
    private long createAndGetId() throws Exception {
        String body = """
                {"serviceName":"Temp","price":1000,"currency":"KRW",
                 "billingCycle":"MONTHLY","startedAt":"2026-06-15",
                 "autoRenew":true,"tags":["PRODUCTIVITY"]}""";

        String json = mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(json).get("id").asLong();
    }
}
