package com.lititi.exams.web.service.impl;

import com.github.pagehelper.PageHelper;
import com.lititi.exams.commons2.log.LttLogger;
import com.lititi.exams.web.dao.MomentMapper;
import com.lititi.exams.web.dao.UserFriendMapper;
import com.lititi.exams.web.dto.MomentDto;
import com.lititi.exams.web.entity.Moment;
import com.lititi.exams.web.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Moment service.
 */
@Service("MomentService")
public class MomentServiceImpl implements MomentService {

    private static final LttLogger logger = new LttLogger(MomentServiceImpl.class);

    @Resource
    private MomentMapper momentMapper;

    @Autowired
    private UserFriendMapper userFriendMapper;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${exam.cache-moment-page-number:5}")
    private Integer cachePages;

    private static final String MOMENT_REDIS_KEY = "moment_limit_%s_%s";

    private static final Integer PAGE_BATCH_SIZE = 1000;

    private static final String MOMENT_10DAY_CACHE_REDIS_KEY = "moment_10Days";
    private static final Integer MOMENT_CACHE_DAY_NUM = 10;


    @Override
    public List<MomentDto> getPage(Long userId, Integer pageNum, Integer pageSize) {
        try {
            String redisKey = String.format(MOMENT_REDIS_KEY, pageNum, userId);
            logger.info("get redis key:", redisKey);
            List<MomentDto> cacheList = (List<MomentDto>) redisTemplate.opsForValue().get(redisKey);

            if (cacheList != null) {
                return cacheList;
            }
            logger.info("get redis");
            List<Long> friendIdList = userFriendMapper.getUserFriendId(userId);
            friendIdList.add(userId);
            PageHelper.startPage(pageNum, pageSize);
            List<MomentDto> momentDtoList = momentMapper.getByQuery(friendIdList);
            redisTemplate.opsForValue().set(redisKey, momentDtoList, 60, TimeUnit.MINUTES);
            return momentDtoList;
        } catch (Exception e) {
            logger.error("getPage error userId:", userId, e);
            throw new RuntimeException("getPage error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Moment addMoment(Moment moment) {
        try {
            logger.info("addMoment start");
            momentMapper.addMoment(moment);
            momentMapper.batchAddMomentImg(moment.getMomentId(), moment.getImgPathList());
            List<Long> friendIdList = userFriendMapper.getUserFriendId(moment.getUserId());
            friendIdList.add(moment.getUserId());
            friendIdList.forEach(userId -> {
                for (int pageNum = 1; pageNum <= cachePages; pageNum++) {
                    redisTemplate.delete(String.format(MOMENT_REDIS_KEY, pageNum, userId));
                }
            });
            return moment;
        } catch (Exception e) {
            logger.error("addMoment error userId:{}", moment.getUserId(), e);
            throw new RuntimeException("addMoment error");
        }
    }

    @Override
    @Scheduled(fixedRate  = 86400000L ,initialDelay = 0)
    public Boolean getMomentByDays() {
        try {
            logger.info("getMomentByDays start");
            Integer batchSize = PAGE_BATCH_SIZE;
            Integer count = momentMapper.countMomentByDays(MOMENT_CACHE_DAY_NUM);

            int pageTotal = (count / batchSize) + 1;
            CompletableFuture<List<MomentDto>>[] futures = new CompletableFuture[pageTotal];
            for (int pageNum = 0; pageNum < pageTotal; pageNum++) {
                PageHelper.startPage(pageNum, batchSize);
                futures[pageNum] = CompletableFuture.supplyAsync(() -> {
                    List<MomentDto> momentDtoList = momentMapper.getMomentByDays(10);
                    return momentDtoList;
                });
            }
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures);
            CompletableFuture<List<MomentDto>> allResultsFuture = allFutures.thenApply(v -> {
                List<MomentDto> list = new ArrayList<>();
                for (int i = 0; i < futures.length; i++) {
                    list.addAll(futures[i].join());
                }
                return list;
            });
            List<MomentDto> result = allResultsFuture.get();
            Map<Integer, List<MomentDto>> userIdToMomentMap =
                    result.stream().collect(Collectors.groupingBy(MomentDto::getUserId));
            redisTemplate.opsForValue().set(MOMENT_10DAY_CACHE_REDIS_KEY, userIdToMomentMap, 1, TimeUnit.DAYS);
            return true;
        } catch (Exception e) {
            logger.error("getMomentByDays error", e);
            throw new RuntimeException("getMomentByDays error");
        } finally {
            logger.info("getMomentByDays end");
        }
    }
}
