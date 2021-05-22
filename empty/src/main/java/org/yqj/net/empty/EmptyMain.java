package org.yqj.net.empty;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/21
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class EmptyMain {
    public static void main(String[] args) throws InterruptedException {
//        for (int i = 0; i < 10000000; i++) {
//            log.info("this is test value :{}", "123");
//        }
        log.info("this is test value :{}", "123");
        // 日志异步方式延迟
        Thread.sleep(1000);
    }
}
