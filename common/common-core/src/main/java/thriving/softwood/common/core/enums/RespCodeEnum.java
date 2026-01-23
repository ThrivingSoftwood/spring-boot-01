package thriving.softwood.common.core.enums;

/**
 * 网络请求返回 status code 的含义枚举
 * 
 * @author Eastean
 * @since version 2023-07-11
 */
public enum RespCodeEnum {
    CONTINUE(100, "Continue", "继续。客户端应继续其请求"),

    SWITCH_PROTOCOLS(101, "Switching Protocols", "切换协议。服务器根据客户端的请求切换协议。只能切换到更高级的协议，例如，切换到HTTP的新版本协议"),
    PROCESSING(102, "Processing", "RFC 2518, obsoleted by RFC 4918"), OK(200, "OK", "请求成功。一般用于GET与POST请求"),
    CREATED(201, "Created", "已创建。成功请求并创建了新的资源"), ACCEPTED(202, "Accepted", "已接受。已经接受请求，但未处理完成"),
    NON_AUTH_INFO(203, "Non-Authoritative Information", "非授权信息。请求成功。但返回的meta信息不在原始的服务器，而是一个副本"),
    NO_CONTENT(204, "No Content", "无内容。服务器成功处理，但未返回内容。在未更新网页的情况下，可确保浏览器继续显示当前文档"),
    RESET_CONTENT(205, "Reset Content", "重置内容。服务器处理成功，用户终端（例如：浏览器）应重置文档视图。可通过此返回码清除浏览器的表单域"),
    PARTIAL_CONTENT(206, "Partial Content", "部分内容。服务器成功处理了部分GET请求"), MULTI_STATUS(207, "Multi-Status", "RFC 4918"),
    ALREADY_REPORTED(208, "Already Reported", ""), IM_USED(226, "IM Used", ""),
    MULTI_CHOICES(300, "Multiple Choices", "多种选择。请求的资源可包括多个位置，相应可返回一个资源特征与地址的列表用于用户终端（例如：浏览器）选择"),
    MOVED_PERMANENTLY(301, "Moved Permanently",
        "永久移动。请求的资源已被永久的移动到新URI，返回信息会包括新的URI，浏览器会自动定向到新URI。今后任何新的请求都应使用新的URI代替"),
    FOUND(302, "Found", "临时移动。与301类似。但资源只是临时被移动。客户端应继续使用原有URI"),
    SEE_OTHER(303, "See Other", "查看其它地址。与301类似。使用GET和POST请求查看"),
    NOT_MODIFIED(304, "Not Modified",
        "未修改。所请求的资源未修改，服务器返回此状态码时，不会返回任何资源。客户端通常会缓存访问过的资源，通过提供一个头信息指出客户端希望只返回在指定日期之后修改的资源"),
    USE_PROXY(305, "Use Proxy", "使用代理。所请求的资源必须通过代理访问"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect", "临时重定向。与302类似。使用GET请求重定向"),
    PERMANENT_REDIRECT(308, "Permanent Redirect", "RFC 7238"), BAD_REQ(400, "Bad Request", "客户端请求的语法错误，服务器无法理解"),
    UNAUTHORIZED(401, "Unauthorized", "请求要求用户的身份认证"), PAYMENT_REQUIRED(402, "Payment Required", "保留，将来使用"),
    FORBIDDEN(403, "Forbidden", "服务器理解请求客户端的请求，但是拒绝执行此请求"),
    NOT_FOUND(404, "Not Found", "服务器无法根据客户端的请求找到资源（网页）。通过此代码，网站设计人员可设置\"您所请求的资源无法找到\"的个性页面"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "客户端请求中的方法被禁止"),
    NOT_ACCEPTABLE(406, "Not Acceptable", "服务器无法根据客户端请求的内容特性完成请求"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required", "请求要求代理的身份认证，与401类似，但请求者应当使用代理进行授权"),
    REQUEST_TIMEOUT(408, "Request Timeout", "服务器等待客户端发送的请求时间过长，超时"),
    CONFLICT(409, "Conflict", "服务器完成客户端的PUT请求是可能返回此代码，服务器处理请求时发生了冲突"),
    GONE(410, "Gone", "客户端请求的资源已经不存在。410不同于404，如果资源以前有现在被永久删除了可使用410代码，网站设计人员可通过301代码指定资源的新位置"),
    LENGTH_REQUIRED(411, "Length Required", "服务器无法处理客户端发送的不带Content-Length的请求信息"),
    PRECONDITION_FAILED(412, "Precondition Failed", "客户端请求信息的先决条件错误"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large",
        "由于请求的实体过大，服务器无法处理，因此拒绝请求。为防止客户端的连续请求，服务器可能会关闭连接。如果只是服务器暂时无法处理，则会包含一个Retry-After的响应信息"),
    URI_TOO_LONG(414, "URI Too Long", "请求的URI过长（URI通常为网址），服务器无法处理"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", "服务器无法处理请求附带的媒体格式"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable", "客户端请求的范围无效"),
    EXPECTATION_FAILED(417, "Expectation Failed", "服务器无法满足Expect的请求头信息"), IM_A_TEAPOT(418, "I'm a teapot", "RFC 2324"),
    MISDIRECTED_REQUEST(421, "Misdirected Request", ""), UNPROCESSABLE_ENTITY(422, "Unprocessable Entity", "RFC 4918"),
    LOCKED(423, "Locked", "RFC 4918"), FAILED_DEPENDENCY(424, "Failed Dependency", "RFC 4918"),
    UNORDERED_COLLECTION(425, "Unordered Collection", "RFC 4918"),
    UPGRADE_REQUIRED(426, "Upgrade Required", "RFC 2817"),
    PRECONDITION_REQUIRED(428, "Precondition Required", "RFC 6585"),
    TOO_MANY_REQUESTS(429, "Too Many Requests", "RFC 6585"),
    REQ_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large", "RFC 6585"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons", ""),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "服务器内部错误，无法完成请求"),
    NOT_IMPLEMENTED(501, "Not Implemented", "服务器不支持请求的功能，无法完成请求"),
    BAD_GATEWAY(502, "Bad Gateway", "充当网关或代理的服务器，从远端服务器接收到了一个无效的请求"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable", "由于超载或系统维护，服务器暂时的无法处理客户端的请求。延时的长度可包含在服务器的Retry-After头信息中"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout", "充当网关或代理的服务器，未及时从远端服务器获取请求"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported", "服务器不支持请求的HTTP协议的版本，无法完成处理"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates", "RFC 2295"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage", "RFC 4918"), LOOP_DETECTED(508, "Loop Detected", ""),
    BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded", ""), NOT_EXTENDED(510, "Not Extended", "RFC 2774"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required", "RFC 6585"),

    /* 上方是官方定义的错误编码,下方是自定义的错误编码 */

    LOGIN_INVALID_USER(100001, "User you tried to Login do not exist.", "尝试登录的用户不存在");

    private final Integer statusCode;
    private final String enDetail;
    private final String cnDetail;

    /**
     * 构造器
     * 
     * @author Eastean
     * @since version 2023-07-11
     * @param statusCode 状态码
     * @param enDetail 英文描述
     * @param cnDetail 详细描述
     */
    RespCodeEnum(Integer statusCode, String enDetail, String cnDetail) {
        this.statusCode = statusCode;
        this.enDetail = enDetail;
        this.cnDetail = cnDetail;
    }

    public Integer code() {
        return statusCode;
    }

    public String enDesc() {
        return enDetail;
    }

    public String cnDesc() {
        return cnDetail;
    }
}
