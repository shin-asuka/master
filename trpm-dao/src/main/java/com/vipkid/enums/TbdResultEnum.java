package com.vipkid.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TBD  专门为PES 功能实现的对应关系
 *
 */
public class TbdResultEnum {

    private static Logger logger = LoggerFactory.getLogger(TbdResultEnum.class);

    public static int getResultEnum(String type) {
        try {
            type = type.toUpperCase();
            return ResultEnum.valueOf(type).val;
        } catch (Exception e) {
            logger.error("未知类型" + e.getMessage());
            return 2;
        }
    }

    public static int getStatusEnum(String status) {
        try {
            status = status.toUpperCase();
            return StatusEnum.valueOf(status).val;
        } catch (Exception e) {
            logger.error("未知状态:" + e.getMessage());
            return 5;
        }
    }

    public enum ReScheduleEnum {
        ReSchedule("REAPPLY");

        private String val;

        private ReScheduleEnum(String val) {
            this.val = val;
        }

        public String val() {
            return this.val;
        }
    }

    public enum ResultEnum {
        TBD_FAIL(0),

        TBD(1);

        private int val;

        private ResultEnum(int val) {
            this.val = val;
        }

        public int val() {
            return this.val;
        }
    }

    public enum StatusEnum {

        PASS(1),

        FAIL(2),

        REAPPLY(3),

        PRACTICUM2(4);

        private int val;

        private StatusEnum(int val) {
            this.val = val;
        }

        public int val() {
            return this.val;
        }
    }
}
