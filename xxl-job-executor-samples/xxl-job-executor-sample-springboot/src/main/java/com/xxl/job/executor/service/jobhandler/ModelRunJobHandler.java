package com.xxl.job.executor.service.jobhandler;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.util.DateUtils;
import com.google.common.collect.ImmutableMap;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.domain.AjaxResult;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.executor.core.config.BigDataServerConfig;
import com.xxl.job.executor.core.config.DesignerServerConfig;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 模型运行任务处理器
 * </p>
 *
 * @author zhao.yuhui
 * @e-mail zhaoyuhui@taiji.com.cn
 * @date 2024/12/10
 */
@Component
@RequiredArgsConstructor
public class ModelRunJobHandler {

    private final static Logger logger = LoggerFactory.getLogger(ModelRunJobHandler.class);

    private final BigDataServerConfig bigData;

    private final DesignerServerConfig designer;

    @XxlJob(value = "modelRunJobHandler", init = "init", destroy = "destroy")
    private void modelRunJob() {
        String jobParam = XxlJobHelper.getJobParam();
        logger.info("jobParam --> {}", jobParam);
        Map<String, String> map = JSON.parseObject(jobParam, Map.class);
        map.put("params[$_cjsj]", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("params[$_maxId]", this.getMaxId(map.get("params[$_bmid]")));
        this.httpRequest(map);
    }

    private String getMaxId(String bmid) {

        RestTemplate restTemplate = new RestTemplate();
        String reqUrl = String.format("http://%s:%s/core/modelClue/getMaxId?key={key}", bigData.getIp(),
            bigData.getPort());
        logger.info("reqUrl --> {}", reqUrl);
        ResponseEntity<AjaxResult> responseEntity = restTemplate.getForEntity(reqUrl, AjaxResult.class,
            ImmutableMap.of("key", bmid));
        logger.info("responseEntity --> {}", responseEntity);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody().get("data").toString();
        }
        return null;
    }

    private void httpRequest(Map<String, String> params) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            multiValueMap.add(entry.getKey(), entry.getValue());
        }
        logger.info("multiValueMap --> {}", multiValueMap);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String reqUrl = String.format("http://%s:%s/trans/run", designer.getIp(), designer.getPort());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueMap, headers);
        ResponseEntity<AjaxResult> responseEntity = restTemplate.exchange(reqUrl, HttpMethod.POST, httpEntity,
            AjaxResult.class);
        logger.info("responseEntity --> {}", responseEntity);
    }

    private void init() {
        logger.info("init model run job handler");
    }

    private void destroy() {
        logger.info("destroy model run job handler");
    }

}
