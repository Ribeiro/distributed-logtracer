package br.tec.gtech.utilities.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.env.Environment;

public class RequestResponseLoggingFilter implements Filter{

     @Autowired
    Tracer tracer;

    @Autowired
    Environment env;

    private final static Logger LOG = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
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

        HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

        LOG.info("Logging Request  {} : {}", req.getMethod(), req.getRequestURI());
        chain.doFilter(request, response);
        LOG.info("Logging Response :{}", res.getContentType());
    }
    
}
