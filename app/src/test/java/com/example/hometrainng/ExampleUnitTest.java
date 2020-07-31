package com.example.hometrainng;

import com.example.hometrainng.db.executor.ScheduleExecutor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private String token = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6InlhbWEiLCJ0b2tlblR5cGUiOiJhcHAiLCJleHAiOjQ3NDI4NjE0NzZ9.EJYQSlNICvaESpAObyFR4-GRKhpZoS0gLaPrLTFDBV4WTHhvtr4WnQZvFN_ALzF-BpitafCjIaTFtp218-qdOg";

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

//    @Test
//    public void show_counseling() {
//        ScheduleExecutor scheduleExecutor = new ScheduleExecutor();
//        scheduleExecutor.executor("insert", token, "61");
//    }
}