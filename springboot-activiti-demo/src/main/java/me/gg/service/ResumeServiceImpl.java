package me.gg.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by sam on 18-8-28.
 */
@Service("resumeService")
public class ResumeServiceImpl implements ResumeService {
    private static Logger log = LoggerFactory.getLogger(ResumeServiceImpl.class);

    @Override
    public void storeResume() {
        log.info("ResumeService任务已执行!...");
    }

    @Override
    public void storeResume2(String arg) {
        log.info("ResumeService任务已执行! 参数:" + arg);
    }

    @Override
    public void storeResume(String userId, String applyUser, float fee) {
        log.info("发起人ID:{}, applyUser:{}, fee:{}", userId, applyUser, fee);
    }
}
