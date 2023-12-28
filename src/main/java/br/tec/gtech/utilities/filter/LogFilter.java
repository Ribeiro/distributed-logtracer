package br.tec.gtech.utilities.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Order
//@Slf4j
public class LogFilter extends OncePerRequestFilter {
    @Autowired
    Tracer tracer;

    @Autowired
    Environment env;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        Span span;
        String parentId = tracer.currentSpan().context().parentId();
        MDC.put("application",env.getProperty("spring.application.name"));
        MDC.put("version",env.getProperty("spring.application.version"));

        //Check whether the parent id exist then use the current span
        //otherwise next span is used. This is used for chaining of span id
        //within a trace id.
        if(parentId == null) {
            span = tracer.nextSpan();
        }else{
            span = tracer.currentSpan();
        }

        TraceContext traceContext = tracer.currentSpan().context();

        //populate trace id, span id and parent span id in MDC
        MDC.put("traceId",traceContext.traceId());
        MDC.put("spanId",traceContext.spanId());
        //MDC.put("parentSpanId",traceContext.parentId());
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
