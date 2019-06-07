-- 判断key是否存在
if (redis.call('exists', KEYS[1]) == 0)
then
    --如果锁不存在则发布没有锁的消息给其他redis
    redis.call('publish', KEYS[2], ARGV[1]);
    return 1;
end ;
-- key存在，判断当前线程是否占有锁
if (redis.call('hexists', KEYS[1], ARGV[3]) == 0)
then
    return nil;
end ;
-- 当前线程占用锁
local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1);
if (counter > 0)
then
    -- 没到过期次数，设置过期时间
    redis.call('pexpire', KEYS[1], ARGV[2]);
    return 0;
else
    -- 到达过期次数，删除锁key，并发布释放锁
    redis.call('del', KEYS[1]);
    redis.call('publish', KEYS[2], ARGV[1]);
    return 1;
end ;
return nil;