package com.gangjust.kingosoftapi;

import com.gangjust.kingosoftapi.error.KingosoftLoginFailureException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                try {
                    //System.out.println(KingosoftApi.getSchoolList());

                    KingosoftApi kingosoftApi = KingosoftApi.login("10680", 账号, 密码, false);

                    if (kingosoftApi.isLogin()) {
                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String schoolYear = kingosoftApi.getSchoolYear();
                        System.out.println(schoolYear);

                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String course = kingosoftApi.getCourse("", "", "15", "20200");
                        System.out.println(course);

                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String score = kingosoftApi.getScore("0","20191");
                        System.out.println(score);

                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String warningSituation = kingosoftApi.getWarningSituation();
                        System.out.println(warningSituation);

                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String trainingProgram = kingosoftApi.getTrainingProgram();
                        System.out.println(trainingProgram);

                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String nationalLevelExamination = kingosoftApi.getNationalLevelExamination();
                        System.out.println(nationalLevelExamination);

                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String learningDetails = kingosoftApi.getLearningDetails();
                        System.out.println(learningDetails);

                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String relearning = kingosoftApi.getRelearning();
                        System.out.println(relearning);

                        sleep(1000); //延迟一秒钟，避免操作过快，连接超时
                        String timetable = kingosoftApi.getTimetable();
                        System.out.println(timetable);

                    } else {
                        System.out.println(kingosoftApi.getLoginMsg());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (KingosoftLoginFailureException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
