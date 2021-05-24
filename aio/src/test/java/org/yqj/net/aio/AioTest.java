package org.yqj.net.aio;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by yaoqijun.
 * Date:2016-02-05
 * Email:yaoqj@terminus.io
 * Descirbe:
 */
@Slf4j
@Ignore
public class AioTest {

    @Test
    public void testServer() throws Exception {
        new AioServer().start();
    }

    @Test
    public void testClient() throws Exception {
        new AioClient().start();
    }

}
