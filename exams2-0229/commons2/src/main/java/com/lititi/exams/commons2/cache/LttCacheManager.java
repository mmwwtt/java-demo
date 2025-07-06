package com.lititi.exams.commons2.cache;

import com.lititi.exams.commons2.enumeration.OP;
import com.lititi.exams.commons2.enumeration.RedisDB;

import java.util.Collection;

public class LttCacheManager extends AbstractCacheManager{

    public final String SESSION_CACHE_MASTER = "SessionCacheMaster";

    public final String Other_CACHE_MASTER = "OtherCacheMaster";

    public final String SESSION_CACHE_SLAVE = "SessionCacheSlave";

    public final String OTHER_CACHE_SLAVE = "OtherCacheSlave";

    private Collection<? extends Cache> caches;

    /**
     * Specify the collection of Cache instances to use for this CacheManager.
     */
    public void setCaches(Collection<? extends Cache> caches) {
        this.caches = caches;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return this.caches;
    }

    public Cache getSessionCacheMaster() {
        return getCache(SESSION_CACHE_MASTER);
    }

    public Cache getOtherCacheMaster() {
        return getCache(Other_CACHE_MASTER);
    }

    public Cache getSessionCacheSlave() {
        return getCache(SESSION_CACHE_SLAVE);
    }

    public Cache getOtherCacheSlave() {
        return getCache(OTHER_CACHE_SLAVE);
    }

    public Cache getCacheByDBAndOP(RedisDB db, OP op) {
        if (db == RedisDB.OTHER && op == OP.WRITE) {
            return getOtherCacheMaster();
        } else if (db == RedisDB.OTHER && op == OP.READ) {
            return getOtherCacheSlave();
        } else if (db == RedisDB.SESSION && op == OP.WRITE) {
            return getSessionCacheMaster();
        } else if (db == RedisDB.SESSION && op == OP.READ) {
            return getSessionCacheSlave();
        } else {
            return null;
        }

    }
}
