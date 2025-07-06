package com.lititi.exams.commons2.constant;

import javax.management.timer.Timer;


public interface Constant {

    /**
     * 主数据库
     */
    String MASTER = "master";
    /**
     * 从数据库
     */
    String SLAVE = "slave";

    String MINUS_SIGN = "-";

    String PLUS_SIGN = "+";

    String COLOR = "#7414F5";

    /**
     * 默认token status
     */
    int DEFAULT_TOKEN_STATUS = 1;

    long DEFAULT_TOKEN_EXPIRED_TIME = 1000L * 3600 * 24 * 30;

    long TEN_THOUSAND = 10000L;

    long ONE_HUNDRED = 100L;

    long VALID_TUTOR_LIMIT = 8000000L;

    long VALID_CAPTAIN_LIMIT = 100L;

    long ONE_HUNDRED_MILLION = 100000000L;

    long BILLION = 10000000000L;

    long ONE_THOUSAND_BILLION = 100000000000000L;

    long ONE_MILLION = 1000000L;

    long DEFAULT_AMOUNT = 0L;

    int TOKEN_STATUS_VALID = 1;

    int DEFAULT_RATIO = 1;

    int TOKEN_STATUS_INVALID = 0;

    int INVITATION_CODE_LENGTH = 6;

    int IS_BEST = 1;

    long THIRTY_DAY_MILLISECONDS = 30 * 24 * 3600 * 1000L;

    // 有效队长判定最低金额 数据库存储 * 10000;
    long THIRTY_EFFECTIVE_CAPTAIN_AMOUNT = 1L;

    // 升级成导师自身已结算最低金额
    long THIRTY_EFFECTIVE_CAPTAIN_SELF_AMOUNT = 3000000L;

    // 有效导师判定最低金额 数据库存储 * 10000;
    long THIRTY_EFFECTIVE_TUTOR_AMOUNT = 8000000L;
    // 升级成金牌导师自身已结算最低金额 数据库存储 * 10000;
    long THIRTY_EFFECTIVE_TUTOR_SELF_AMOUNT = 80000000L;

    int CAPTAIN_UPGRADE_ONE_LEVEL_CAPTAIN_NUMBER = 50;

    int CAPTAIN_UPGRADE_ONE_AND_TWO_LEVEL_CAPTAIN_NUMBER = 150;

    int TUTOR_UPGRADE_ONE_LEVEL_TUTOR_NUMBER = 10;

    int TUTOR_UPGRADE_ONE_AND_TWO_LEVEL_TUTOR_NUMBER = 20;

    // 表格每页记录数
    int TABLE_PAGE_SIZE = 10;

    int TRANSACTION_TIMEOUT = 50;

    /**
     * 金额到毫单位
     */
    int PRICE_UNIT = 10000;
    /**
     * 业务主体是分销人员
     */
    int ENTITY_TYPE_DISTRIBUTION = 0;
    /**
     * 一天的毫秒数
     */
    long ONE_DAY_MILISECONDS = 86400000;

    /**
     * cookie超时时间,设置为1个月
     */
    int ONE_MONTH_COOKIE_EXPIRATION = 2592000;

    int DEFAULT_PAGE_SIZE = 10;

    /**
     * 交易token session key
     */
    String TRADE_TOKEN = "TradeToken";
    /**
     * 默认token前缀
     */
    String TOKEN_PREFIX = "token_";
    /**
     * 分销人员在业务主体中的角色
     */
    String DISTRIBUTION_PERSON = "1";
    /**
     * 最大分页数
     */
    int MAX_PAGE_SIZE = 1000;

    /**
     * mybatisc缓存过期时间
     */
    int MYBATIS_CACHE_EXPIRE_DURATION = 300;

    /**
     * 毫秒值
     */
    int DEFAULT_CONNECTION_TIMEOUT = 30000;

    // 不指定币种
    int CURRENCY_NULL = -1;

    /**
     * 上传的文件大小限制为5M
     */
    long UPLOAD_FILE_MAX_SIZE = 5 * 1024 * 1024L;

    int SUPER_ADMINISTRATOR_AUTH = -1;

    int BUSINESS_ENTITY_OWNER_FLAG = 1;

    String TOKEN_CACHE_KEY_PREFIX = "TOKEN_USER_ACCOUNT_";

    String CUSTOMER_PLATFORM_TOKEN_CACHE_KEY_PREFIX = "CUSTOMER_PLATFORM_TOKEN_%s";

    String CUSTOMER_PLATFORM_POINT_RANK_CACHE_KEY_PREFIX = "CUSTOMER_PLATFORM_POINT_RANK_%s";

    String CUSTOMER_PLATFORM_POINT_RANK_TIME_CACHE_KEY_PREFIX = "CUSTOMER_PLATFORM_POINT_RANK_TIME_%s";

    String USER_ACCOUNT_CACHE_KEY_PREFIX = "USER_ACCOUNT_";

    String TOKEN_KEY_HEADER = "Token";

    String PICTURE_VALIDATE_CODE_CACHE_KEY_PREFIX = "PICTURE_VALIDATE_CODE_";

    String SMS_VALIDATE_CODE_CACHE_KEY_PREFIX = "SMS_VALIDATE_CODE_";

    String BIND_PAY_CHANNEL_ACCOUNT_SMS_VALIDATE_CODE_CACHE_KEY_PREFIX = "BIND_PAY_CHANNEL_ACCOUNT_SMS_VALIDATE_CODE_";

    String BIND_MOBILE_PHONE_SMS_VALIDATE_CODE_CACHE_KEY_PREFIX = "BIND_MOBILE_PHONE_SMS_VALIDATE_CODE_";

    String MOBILE_PHONE_LOGIN_SMS_VALIDATE_CODE_CACHE_KEY_PREFIX = "MOBILE_PHONE_LOGIN_SMS_VALIDATE_CODE_";

    String CHOOSE_RANCH_USER_FULL_RANCH = "CHOOSE_RANCH_USER_FULL_RANCH_%s";

    String USER_FIRST_EXCHANGE = "USER_FIRST_EXCHANGE_%s";

    String DRAW_TOTAL_CACHE_KEY_PREFIX = "PICTURE_BOOK_TOTAL_%s";

    Integer DRAW_MAX_TOTAL = 100;

    long DAY_SECONDS = 24 * 60 * 60;

    String USER_DRAW_CACHE_KEY_PREFIX = "PICTURE_BOOK_USER_%s_%s";

    String TOKEN_HEADER = "Token";

    String TERMINAL_PROGRAM_HEADER = "Terminal-Program";

    String IDENTITY_STATUS_CACHE_KEY_PREFIX = "IDENTITY_STATUS_";

    String UNIT_MEMBER_EHCACHE_CONFIG_NAME = "UNIT_MEMBER";

    String UNIT_MEMBER_EHCACHE_CACHE_KEY = "UNIT_MEMBER";

    String UNIT_EHCACHE_CONFIG_NAME = "UNIT";

    String UNIT_EHCACHE_CACHE_KEY = "UNIT";

    Integer UNIT_MEMBER_MAX_ENTRIES_LOCAL_HEAP = 100000;

    Integer UNIT_MAX_ENTRIES_LOCAL_HEAP = 1000;

    Integer UNIT_MEMBER_EHCACHE_EXPIRE_TIME = 3600;

    Integer UNIT_EHCACHE_EXPIRE_TIME = 3600;

    String DOMAIN_EHCACHE_CONFIG_NAME = "DOMAIN";

    String IDENTITY_CACHE_NAME = "identityCache";

    String IDENTITY_CACHE_KEY_PREFIX = "identity_cache_";

    String FINISH_PREDICT_FEE = "finishPredictFee";

    String PAY_PREDICT_UPSTREAM_PLATFORM_COMMISSION = "payPredictUpstreamPlatformCommission";

    String FINISH_PREDICT_UPSTREAM_PLATFORM_COMMISSION = "finishPredictUpstreamPlatformCommission";

    String PAY_PREDICT_RIGHTS_PLAN_FEE = "payPredictRightsPlanFee";

    String FINISH_PREDICT_RIGHTS_PLAN_FEE = "finishPredictRightsPlanFee";

    String USER_PRE_TAX_RATIO = "userPreTaxRatio";

    String BASIC_REWARD_ONE = "basicRewardOne";

    String BASIC_REWARD_TWO = "basicRewardTwo";

    String BASIC_REWARD_THREE = "basicRewardThree";

    String PAY_PREDICT_FEE = "payPredictFee";

    String TAX_AMOUNT = "taxAmount";

    String ADJUST_AMOUNT = "adjustAmount";

    String ADJUST_FLAG = "adjustFlag";

    String UPPER_TAX_AMOUNT = "upperTaxAmount";

    String BUSINESS_TAX_AMOUNT = "businessTaxAmount";

    String USER_TAX_AMOUNT = "userTaxAmount";

    String CAPT_SELF_PURCHASE = "captSelfPurchase";

    String TUTOR_SELF_PURCHASE = "tutorSelfPurchase";

    String GOLDEN_SELF_PURCHASE = "goldenSelfPurchase";

    String CAPT_DIRECT_INVITATION = "captDirectInvitation";

    String TUTOR_DIRECT_INVITATION = "tutorDirectInvitation";

    String GOLDEN_DIRECT_INVITATION = "goldenDirectInvitation";

    String CAPT_INDIRECT_INVITATION = "captIndirectInvitation";

    String TUTOR_INDIRECT_INVITATION = "tutorIndirectInvitation";

    String GOLDEN_INDIRECT_INVITATION = "goldenIndirectInvitation";

    String TUTOR_DIRECT_COACH_TUTOR = "tutorDirectCoachTutor";

    String GOLDEN_DIRECT_COACH_TUTOR = "goldenDirectCoachTutor";

    String TUTOR_MANAGEMENT_CAPT = "tutorManagementCapt";

    String GOLDEN_MANAGEMENT_CAPT = "goldenManagementCapt";

    String GOLDEN_MANAGEMENT_TUTOR = "goldenManagementTutor";

    String GOLDEN_DIRECT_COACH_GOLDEN = "goldenDirectCoachGolden";

    String TUTOR_INDIRECT_COACH_TUTOR = "tutorIndirectCoachTutor";

    String GOLDEN_INDIRECT_COACH_TUTOR = "goldenIndirectCoachTutor";

    String AMOUNT = "amount";

    String UPPER_SERVICE_FEE = "upperServiceFee";

    String UPPER_PRE_TAXATION = "upperPreTaxation";

    String SYSTEM_FEE = "systemFee";

    String SYSTEM_RETENTION = "systemRetention";

    String AVAILABLE_CASH_AMOUNT = "availableCashAmount";

    String APPROVED_AND_NOT_SETTLED = "approvedAndNotSettled";

    String COMMISSION_UNDER_REVIEW = "commissionUnderReview";

    String COMMISSION_FOR_AUDIT_FAILURE = "commissionForAuditFailure";

    String PUNISHED_COMMISSION = "punishedCommission";

    String JINGDONG_SELF = "g";

    String JINGDONG_POP = "p";

    long JINGDONG_SELF_RATE = 1000000;

    long JINGDONG_POP_RATE = 900000;

    String RECOMMEND_INVITATION_URL = "recommendInvitationUrl";

    String INVITE_CODES = "inviteCode";

    String THE_ROLE_OF_COMMISSION = "theRoleOfCommission";
    /**
     * 商品订单读取url
     */
    String PRODUCT_ORDER_URL_FORMAT =
        "https://pub.alimama.com/openapi/param2/1/gateway.unionpub/report.getTbkOrderDetails.json?" + "t=%s&"
            + "_tb_token_=%s&" + "jumpType=%s&" + "positionIndex=%s&" + "pageNo=%s&" + "startTime=%s&" + "endTime=%s&"
            + "queryType=%s&" + "tkStatus=%s&" + "memberType=%s&" + "pageSize=%s";

    String PRODUCT_ORDER_EXCEL_DOWNLOAD_URL_FORMAT =
        "https://pub.alimama.com/report/getTbkOrderDetails.json?spm=a219t.11816981.0.0.62636a155acqE0&downloadId=DOWNLOAD_REPORT_ORDER_DETAILS&startTime=%s&endTime=%s&queryType=%s&tkStatus=%s&memberType=";

    Integer MONEY_MULTIPLE = 10000;

    String TB_CLIENT_SERVER_URL = "https://eco.taobao.com/router/rest";

    String TB_PASSWORD_FORMAT = "复制这条 %s ，进入【淘宝】即可抢购";

    String TB_PRODUCT_URL_FORMAT = "https://detail.tmall.com/item.htm?id=%s";

    String TB_COMMON_PRODUCT_URL_FORMAT = "https://item.taobao.com/item.htm?id=%s";

    String JD_COMMON_PRODUCT_URL_FORMAT = "item.jd.com/%s.html";

    String TB_COMMON_PRODUCT_URL_FORMAT_WITHNOT_EQUAL_SIGIN = "https://item.taobao.com/item.htm?id%s";

    Long LITITI_COMPANY_BIZ_NO = 161966918016014254L;

    String AUTO_LOGIN_ACCOUNT_URL_FORMAT = "http://101.37.32.12:3000/login?accountName=%s&password=%s";

    String forceplanet_BIZ_ENTITY_NAME = "6600";

    long ONE_HOUR_VALUE = 3600000;

    long PRODUCT_ORDER_START_TIME = 1614528000000L;

    String TWO_KEY_FORMAT = "%s_%s";

    String THREE_KEY_FORMAT = "%s_%s_%s";

    String FOUR_KEY_FORMAT = "%s_%s_%s_%s";

    String FIVE_KEY_FORMAT = "%s_%s_%s_%s_%s";

    int PAGE_SIZE = 1000;

    String UNKNOWN = "unknown";

    String LOCALHOST = "127.0.0.1";

    String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    String COMMA_SEPARATOR = ",";

}
