Code Review

You are reviewing the following code submitted as part of a task to implement an item cache in a highly concurrent application. The anticipated load includes: thousands of reads per second, hundreds of writes per second, tens of concurrent threads.
Your objective is to identify and explain the issues in the implementation that must be addressed before deploying the code to production. Please provide a clear explanation of each issue and its potential impact on production behaviour.

import java.util.concurrent.ConcurrentHashMap

class SimpleCache<K, V> {
private val cache = ConcurrentHashMap<K, CacheEntry<V>>()
private val ttlMs = 60000 // 1 minute

    data class CacheEntry<V>(val value: V, val timestamp: Long)
    
    fun put(key: K, value: V) {
        cache[key] = CacheEntry(value, System.currentTimeMillis())
    }
    
    fun get(key: K): V? {
        val entry = cache[key]
        if (entry != null) {
            if (System.currentTimeMillis() - entry.timestamp < ttlMs) {
                return entry.value
            }
        }
        return null
    }
    
    fun size(): Int {
        return cache.size
    }
}

Issues to be addressed:
1. The key of concurrentHashMap is generic and does not gurantee immutability. If this mutable key is modified after insertion, then the ConcurrentHashMap will not be able to locate this key, which may causing a memory leak and inconsistent cache behavior. The implementation should either document the immutability requirement explicitly or restrict K to immutable value types.
2. The current get method implementation for checking expired records is not atomic, and there is a chance for inconsistency. Use atomic methods like compute or computeIfPresent to process the get logic as synchronized.
3. The get method does not expire records which are less than ttl. This can cause memory leak and eventually could also impact the application with OutOfMemory errors. Use ConcurrentHashMap.compute to handle this.
4. Cleanup of expired key value pairs in the map is not done. This can cause expired records to remain in the memory. Need to introduce clean up logic which can handle this.
5. I would rename the method size to totalCacheEntries inorder to indicate that it is the total of active and inactive cache entries.