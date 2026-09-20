package com.example.demo.log;

import com.example.demo.entity.SysLogApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApiAccessLogFilterTest {

    @Test
    void capturesRequestResponseAndDuration() throws Exception {
        ApiLogPublisher publisher = mock(ApiLogPublisher.class);
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.application.name", "test-service")
                .withProperty("spring.profiles.active", "test");
        ApiAccessLogFilter filter = new ApiAccessLogFilter(new ObjectMapper(), environment, publisher);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/retail-system/post/save");
        request.setContentType("application/json");
        request.setContent("{\"postName\":\"仓库管理员\"}".getBytes(StandardCharsets.UTF_8));
        request.setParameter("source", "test");
        request.addHeader("X-Forwarded-For", "192.0.2.10");
        request.addHeader("User-Agent", "test-agent");
        request.setRemoteAddr("192.0.2.20");

        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
            servletRequest.getInputStream().readAllBytes();
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(201);
            httpResponse.getWriter().write("{\"id\":1}");
        });

        var captor = org.mockito.ArgumentCaptor.forClass(SysLogApi.class);
        verify(publisher).publish(captor.capture());
        SysLogApi log = captor.getValue();
        assertEquals("test-service", log.getServiceName());
        assertEquals("POST", log.getMethod());
        assertEquals("/retail-system/post/save", log.getRequestUri());
        assertEquals("192.0.2.10", log.getRequestIp());
        assertEquals(201, log.getHttpStatus());
        assertEquals(1, log.getSuccess());
        assertNotNull(log.getRequestParams());
        assertTrue(log.getRequestParams().contains("仓库管理员"));
        assertTrue(log.getRequestParams().contains("source"));
        assertEquals("{\"id\":1}", log.getResponseParams());
        assertNotNull(log.getDurationMs());
        assertTrue(log.getDurationMs() >= 0);
    }
}
