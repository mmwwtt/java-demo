-- 更新锁脚本
if redis.call('get', KEYS[1]) == ARGV[1] then
    redis.call('pexpire', KEYS[1], ARGV[2]);
    -- pexpire与expire的区别是：pexpire毫秒级,expire秒级
    return true;
else
    return false;
end
