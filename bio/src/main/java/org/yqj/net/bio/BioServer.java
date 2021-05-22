package org.yqj.net.bio;

import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/22
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class BioServer {

    private SocketHandler socketHandler;

    private ExecutorService executorService;

    public BioServer(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            return thread;
        };
        executorService = new ThreadPoolExecutor(BioServerConfig.PROCESSORS,
                BioServerConfig.PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(5000), threadFactory);
    }

    public void start() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(BioServerConfig.PORT)) {
            while (true) {
                try (Socket socket = serverSocket.accept();) {
                    log.info("accept connection from address :{}", socket.getRemoteSocketAddress().toString());

                    executorService.submit(() -> {
                        try {
                            socketHandler.handle(socket);
                        } catch (Exception e) {
                            log.error("socket handler socket error, cause :", e);
                        }
                    });
                } catch (Exception e) {
                    log.error("server accept socket fail cause: ", e);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("server socket create fail, port:{} cause: ", BioServerConfig.PORT, e);
        }
    }
}
