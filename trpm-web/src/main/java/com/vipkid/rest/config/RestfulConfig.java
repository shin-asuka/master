package com.vipkid.rest.config;

import java.util.HashMap;
import java.util.Map;

import org.community.tools.JsonTools;

import com.fasterxml.jackson.core.type.TypeReference;

public class RestfulConfig {

    public static long SYSTEM_USER_ID = 2;

    public static final class Validate{

        public static String EMAIL_REG = "^(([a-zA-Z0-9\\\"_\\-])\\.?)+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";

        public static String WD_REG = "^([a-zA-Z0-9])+$";

        public static String PASSWORD_REG = "^(?:.*[A-Za-z].*)(?:.*[0-9].*)|(?:.*[0-9].*)(?:.*[A-Za-z].*).{0,}$";

    }

    public static final String JSON_UTF_8 = "application/json;charset=UTF-8";

    public static final class Quiz{

        public static final int NEW_QUIZ_PASS_SCORE = 80;
        public static final int OLD_QUIZ_PASS_SCORE = NEW_QUIZ_PASS_SCORE;

        public static final String NEW_RIGHT_ANSWER = "{\"QP-1-001\": 3,\"QP-1-002\": 3,\"QP-1-003\": 1,\"QP-1-004\": 4,\"QP-1-005\": 5,\"QP-1-006\": 3,\"QP-1-007\": 3,\"QP-1-008\": 4,\"QP-1-009\": 3,\"QP-1-010\": 1,\"QP-1-011\": 4,\"QP-1-012\": 2,\"QP-1-013\": 3,\"QP-1-014\": 4,\"QP-1-015\": 1,\"QP-1-016\": 2,\"QP-1-017\": 4,\"QP-1-018\": 1,\"QP-1-019\": 4,\"QP-1-020\": 3}";
        public static final String OLD_RIGHT_ANSWER = NEW_RIGHT_ANSWER;

        public static final Map<String,Integer> OLD_CORRECT_ANSWER_MAP = JsonTools.readValue(OLD_RIGHT_ANSWER,new TypeReference<HashMap<String,Integer>>(){});
        public static final Map<String,Integer> NEW_CORRECT_ANSWER_MAP = JsonTools.readValue(NEW_RIGHT_ANSWER,new TypeReference<HashMap<String,Integer>>(){});

    }
}