package me.gg.service;

/**
 * Created by sam on 18-8-28.
 */
public interface ResumeService {
    void storeResume();
    void storeResume2(String arg);
    void storeResume(String userId, String applyUser, float fee);
}
