local res = redis.call("ZRANGE",KEYS[1],ARGV[1],ARGV[2]);
return redis.call("HMGET",KEYS[2],res);
