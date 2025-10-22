-- "if (redis.call('exists', KEYS[1]) == 0)
-- then redis.call('hincrby', KEYS[1], ARGV[2], 1);
-- redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; if (redis.call('hexists', KEYS[1], ARGV[2]) == 1)
--then redis.call('hincrby', KEYS[1], ARGV[2], 1);
-- redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; return redis.call('pttl', KEYS[1]);"

if(redis.call('exists',KEYS[1])==0)
then
    redis.call('hincrby',KEYS[1],ARGV[2],1);
    --设置到期时间
    redis.call('pexpire',KEYS[1],ARGV[1]);
    return nil;
end


-- "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then return nil;end;
-- local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); if (counter > 0) then redis.call('pexpire', KEYS[1], ARGV[2]);
-- return 0; else redis.call('del', KEYS[1]); redis.call('publish', KEYS[2], ARGV[1]); return 1; end; return nil;"

if(redis.call('exists',KEYS[1],ARGV[3])==0)
then
return nil;
end
local counter=redis.call('hincrby',KEYS[1],ARGV[3],-1);
if(counter>0)
then
redis.call('pexpire',KEYS[1],ARGV[2]);
return 0;
else
redis.call('del', KEYS[1]);
redis.call('publish',KEYS[2],ARGV[1]);
return 1;
end;
return nil;
