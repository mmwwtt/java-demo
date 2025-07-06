package com.lititi.exams.commons2.cache;

import java.util.Collection;

public interface CacheManager {

    Cache getCache(String name);

    Collection<String> getCacheNames();
}
