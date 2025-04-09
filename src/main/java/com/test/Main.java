package com.test;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import redis.clients.jedis.Jedis;

public class Main {
    //连接Jedis
//    public static void main(String[] args) {
//        try(Jedis jedis = new Jedis("127.0.0.1", 6379)){
//            jedis.lpush("mylist", "111", "222", "333");  //等同于 lpush mylist 111 222 333 命令
//            jedis.lrange("mylist", 0, -1)
//                    .forEach(System.out::println);    //等同于 lrange mylist 0 -1
//        }
//    }

   //未加锁
//    public static void main(String[] args) {
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
//                try(Jedis jedis = new Jedis("127.0.0.1", 6379)){
//                    for (int j = 0; j < 100; j++) {   //每个客户端获取a然后增加a的值再写回去，如果不加锁那么肯定会出问题
//                        int a = Integer.parseInt(jedis.get("a")) + 1;
//                        jedis.set("a", a+"");
//                    }
//                }
//            }).start();
//        }
//    }

    //加锁
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");   //配置连接的Redis服务器，也可以指定集群
        RedissonClient client =  Redisson.create(config);   //创建RedissonClient客户端
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try(Jedis jedis = new Jedis("127.0.0.1", 6379)){
                    RLock lock = client.getLock("testLock");    //指定锁的名称，拿到锁对象
                    for (int j = 0; j < 100; j++) {
                        lock.lock();    //加锁
                        int a = Integer.parseInt(jedis.get("a")) + 1;
                        jedis.set("a", a+"");
                        lock.unlock();   //解锁
                    }
                }
                System.out.println("结束！");
            }).start();
        }
    }

}

