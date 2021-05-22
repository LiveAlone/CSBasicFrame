package org.yqj.net.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Description:
 *
 * @author yaoqijun
 * @date 2021/5/22
 * Email: yaoqijunmail@foxmail.com
 */
@Slf4j
public class BioClient {
    public BioClient(String address, int port, SocketHandler socketHandler) {
        try(Socket socket = new Socket(address, port)) {
            socketHandler.handle(socket);
        } catch (Exception e) {
            log.error("socket connect error, cause: ", e);
        }
    }
}
