package org.yqj.net.bio;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by yaoqijun.
 * Date:2016-02-05
 * Email:yaoqj@terminus.io
 * Descirbe:
 */
@Slf4j
public class TestContent {

    @Test
    public void ServerStartTest() {
        try {
            new BioServer(socket -> {
                try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream();) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    PrintWriter writer = new PrintWriter(out, true);
                    while (true) {
                        String readContent = reader.readLine();
                        if (readContent == null) {
                            // all content finished
                            log.info("server client read content empty finished");
                            break;
                        }
                        log.info("accept request content from client :{} content: {}", socket.getRemoteSocketAddress().toString(), readContent);
                        Thread.sleep(200);
                        writer.println(String.format("current time stamp is %d", System.currentTimeMillis()));
                    }
                }
            }).start();
        } catch (Exception e) {
            log.error("catch exception condition", e);
        }
    }

    @Test
    public void testClient1() {
        startClient();
    }

    @Test
    public void testClient2() {
        startClient();
    }

    public static void startClient() {
        new BioClient("localhost", 8888, socket -> {
            try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream();) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintWriter writer = new PrintWriter(out, true);
                for (int i = 0; i < 100; i++) {
                    writer.println(String.format("post content is %d", i));
                    System.out.println(reader.readLine());
                    Thread.sleep(1000);
                }
            }
        });
    }
}
