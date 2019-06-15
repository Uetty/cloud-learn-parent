--判断锁key是否存在（key：锁名，hashkey：获取锁的线程）
if (redis.call('exists', KEYS[1]) == 0) then
    --如果key不存在，则设置值并设置过期时间（代表没有线程占用锁）
    redis.call('hset', KEYS[1], ARGV[2], 1);
    redis.call('pexpire', KEYS[1], ARGV[1]);
    return 0;
end ;
--如果key存在，并且hashkey存在（有线程占用锁）
if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then
    --对hash值自增1，并且给key设置过期时间（当前线程在使用锁）
    redis.call('hincrby', KEYS[1], ARGV[2], 1);
    redis.call('pexpire', KEYS[1], ARGV[1]);
    return 0;
end ;
-- key存在，hashkey不存在，返回key的过期时间（其他线程在占用锁，直接返回锁的过期时间）
return redis.call('pttl', KEYS[1]);
