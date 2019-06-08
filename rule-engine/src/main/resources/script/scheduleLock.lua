-- 判断锁是否为当前线程占有
if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then
    -- 是当前线程占有，重设过期时间
    redis.call('pexpire', KEYS[1], ARGV[1]);
    return 1;
end ;
return 0;