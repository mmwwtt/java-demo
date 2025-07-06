package com.lititi.exams.commons2.cache;

import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractCacheManager implements CacheManager,InitializingBean{

    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

    private final Set<String> cacheNames = new LinkedHashSet<>(16);

    // Early cache initialization on startup

    @Override
    public void afterPropertiesSet() {
        Collection<? extends Cache> caches = loadCaches();

        // Preserve the initial order of the cache names
        this.cacheMap.clear();
        this.cacheNames.clear();
        for (Cache cache : caches) {
            addCache(cache);
        }
    }

    /**
     * Load the initial caches for this cache manager.
     * <p>
     * Called by {@link #afterPropertiesSet()} on startup. The returned collection may be empty but must not be
     * {@code null}.
     */
    protected abstract Collection<? extends Cache> loadCaches();

    // Lazy cache initialization on access

    @Override
    public Cache getCache(String name) {
        Cache cache = lookupCache(name);
        if (cache != null) {
            return cache;
        } else {
            Cache missingCache = getMissingCache(name);
            if (missingCache != null) {
                addCache(missingCache);
                return lookupCache(name); // may be decorated
            }
            return null;
        }
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheNames);
    }

    // Common cache initialization delegates/callbacks

    protected final void addCache(Cache cache) {
        this.cacheMap.put(cache.getName(), decorateCache(cache));
        this.cacheNames.add(cache.getName());
    }

    protected final Cache lookupCache(String name) {
        return this.cacheMap.get(name);
    }

    /**
     * Decorate the given Cache object if necessary.
     *
     * @param cache
     *            the Cache object to be added to this CacheManager
     * @return the decorated Cache object to be used instead, or simply the passed-in Cache object by default
     */
    protected Cache decorateCache(Cache cache) {
        return cache;
    }

    /**
     * Return a missing cache with the specified {@code name} or {@code null} if such cache does not exist or could not
     * be created on the fly.
     * <p>
     * Some caches may be created at runtime in the native provided. If a lookup by name does not yield any result, a
     * subclass gets a chance to register such a cache at runtime. The returned cache will be automatically added to
     * this instance.
     *
     * @param name
     *            the name of the cache to retrieve
     * @return the missing cache or {@code null} if no such cache exists or could be created
     * @see #getCache(String)
     */
    protected Cache getMissingCache(String name) {
        return null;
    }

}
