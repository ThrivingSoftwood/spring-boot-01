package thriving.softwood.common.core.util;

// 注意：具体包前缀可能因你拉取的 V7 里程碑版本 (M1~M5) 略有不同
// v6/v7 中，工具类进行了精细化分包，如 text, map, io 等

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.v7.core.array.ArrayUtil;
import cn.hutool.v7.core.map.MapUtil;
import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.core.util.ObjUtil;
import cn.hutool.v7.http.HttpUtil;
import cn.hutool.v7.http.client.Request;
import cn.hutool.v7.http.client.Response;
import cn.hutool.v7.http.meta.Method;
import cn.hutool.v7.json.JSONObject;
import cn.hutool.v7.json.JSONUtil;
import thriving.softwood.common.core.enums.RespCodeEnum;

/**
 * Http 请求工具类 (基于 Hutool V7 门面模式重构版)
 *
 * @author ThrivingSoftwood
 */
public class ReqUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReqUtil.class);

    public static String get(String url, Map<String, String> headers, int timeout) {
        return get(url, headers, null, timeout);
    }

    public static String get(String url, Map<String, String> headers, Map<String, Object> body, int timeout) {
        logger.info("开始发送GET请求(具体参数不予展示)!");
        if (StrUtil.isEmpty(url)) {
            logger.error("GET请求URL为空,请检查!");
            return null;
        }
        return doGet(url, headers, body, timeout);
    }

    public static String post(String url, Map<String, String> headers, Map<String, Object> bodyMap, int timeout,
        long leastElapsedSec) {
        logger.info("开始发送POST请求(具体参数不予展示)!");
        if (StrUtil.isEmpty(url) || MapUtil.isEmpty(bodyMap)) {
            logger.error("发送POST请求失败:入参为空!");
            return null;
        }
        return doPost(url, headers, JSONUtil.toJsonStr(bodyMap), timeout, leastElapsedSec);
    }

    public static String post(String url, Map<String, String> headers, JSONObject bodyObj, int timeout,
        long leastElapsedSec) {
        logger.info("开始发送POST请求(具体参数不予展示)!");
        if (StrUtil.isEmpty(url) || ObjUtil.isEmpty(bodyObj)) {
            logger.error("发送POST请求失败:入参为空!");
            return null;
        }
        return doPost(url, headers, JSONUtil.toJsonStr(bodyObj), timeout, leastElapsedSec);
    }

    public static String post(String url, Map<String, String> headers, String bodyString, int timeout,
        long leastElapsedSec) {
        logger.info("开始发送POST请求(具体参数不予展示)!");
        if (StrUtil.isEmpty(url) || StrUtil.isEmpty(bodyString)) {
            logger.error("发送POST请求失败:入参为空!");
            return null;
        }
        return doPost(url, headers, bodyString, timeout, leastElapsedSec);
    }

    /**
     * GET 请求发送器 (V7 重构版)
     */
    private static String doGet(String url, Map<String, String> headers, Map<String, Object> formParams, int timeout) {
        // 🔥 V7 核心：使用 Request.of 构建门面请求
        Request req = Request.of(url).method(Method.GET);

        if (MapUtil.isNotEmpty(headers)) {
            headers.forEach(req::header);
        }

        if (MapUtil.isNotEmpty(formParams)) {
            // V7：底层自动 UrlEncode，干掉原先有 bug 的 completeGetUrl 方法
            req.form(formParams);
        }

        // 🔥 V7 核心：使用 HttpUtil.send 调度引擎执行请求
        try (Response resp = HttpUtil.send(req)) {
            if (RespCodeEnum.OK.code() == resp.getStatus()) {
                logger.info("成功发送GET请求!");
                // V7：严谨的 body 获取方式
                return resp.body().getString();
            }
            logger.error("发送GET请求失败!状态码:{}", resp.getStatus());
            return null;
        } catch (Exception e) {
            logger.error("发送GET请求异常!", e);
            return null;
        }
    }

    /**
     * POST 请求发送器 (V7 重构版)
     */
    private static String doPost(String url, Map<String, String> headers, String bodyString, int timeout,
        long leastElapsedSec) {

        // 🔥 V7 核心：使用 Request.of 构建 POST 请求
        // 直接塞入 body
        Request req = Request.of(url).method(Method.POST).body(bodyString);

        if (MapUtil.isNotEmpty(headers)) {
            headers.forEach(req::header);
        }

        long leastElapsedTime = leastElapsedSec * 1000;
        long beginTime = System.currentTimeMillis();

        try (Response resp = HttpUtil.send(req)) {
            if (RespCodeEnum.OK.code() == resp.getStatus()) {
                long leftTime = leastElapsedTime + beginTime - System.currentTimeMillis();
                if (leftTime > 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(leftTime);
                    } catch (InterruptedException e) {
                        logger.warn("发送POST请求延时被打断!", e);
                        // 良好的并发习惯：恢复中断标志
                        Thread.currentThread().interrupt();
                    }
                }
                logger.info("成功发送POST请求!");
                return resp.body().getString();
            }
            logger.error("发送POST请求失败!状态码:{}", resp.getStatus());
            return null;
        } catch (Exception e) {
            logger.error("发送POST请求异常!", e);
            return null;
        }
    }

    /**
     * 获取错误信息
     */
    public static String concatParamInfo(Object... args) {
        if (ArrayUtil.isEmpty(args)) {
            return StrUtil.EMPTY;
        }
        // 使用 Hutool 原生连接方法替代 for 循环
        return ArrayUtil.join(args, ",");
    }

    public static void main(String[] args) {
        String serverAddress = "125.160.2.251";
        int serverPort = 9017;
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            // 创建输出流,发送数据到服务器
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            String message = "acctNo:21197000000666727|idNumber:91130932774441846U";
            writer.println(message);
            // 创建输入流,接收来自服务器的数据
            InputStream inputStream = socket.getInputStream();

            // reader 读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder respBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                respBuilder.append(line);
                logger.info(respBuilder.toString());
            }
            logger.info("outside:" + respBuilder);

            /*
            // stream 读取
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                byteArrayOutputStream.write(data, 0, bytesRead);
                logger.info(new String(byteArrayOutputStream.toByteArray(), "UTF-8"));
            }
            String response = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            logger.info(response);
            */
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
