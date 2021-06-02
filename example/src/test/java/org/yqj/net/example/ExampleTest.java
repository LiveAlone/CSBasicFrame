package org.yqj.net.transport;

import org.junit.Ignore;
import org.junit.Test;
import org.yqj.net.transport.basic.BasicClient;
import org.yqj.net.transport.basic.BasicServer;

/**
 * Created by yaoqijun.
 * Date:2016-02-05
 * Email:yaoqj@terminus.io
 * Descirbe:
 */
@Ignore
public class ExampleTest {

    @Test
    public void testBasicServer(){
        new BasicServer().bind(8888);
    }

    @Test
    public void testBasicClient() {
        new BasicClient().connect("localhost", 8888);
    }
}
