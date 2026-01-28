# from http.server import BaseHTTPRequestHandler, HTTPServer
# import json
#
# class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
#     def do_POST(self):
#         # 1. 获取内容长度
#         content_length = int(self.headers['Content-Length'])
#         # 2. 读取请求体
#         post_data = self.rfile.read(content_length)
#
#         print("\n" + "="*50)
#         print(f"收到请求: {self.path}")
#         print("-" * 20 + " HEADERS " + "-" * 20)
#         print(self.headers)
#         print("-" * 20 + " BODY (JSON) " + "-" * 20)
#
#         try:
#             # 尝试格式化 JSON 输出，方便阅读
#             parsed_json = json.loads(post_data.decode('utf-8'))
#             print(json.dumps(parsed_json, indent=4, ensure_ascii=False))
#         except:
#             # 如果不是标准 JSON，直接打印原始字符串
#             print(post_data.decode('utf-8'))
#
#         print("="*50 + "\n")
#
#         # 3. 伪装返回成功 (模拟 ES 的 bulk 响应)
#         # OTel ES Exporter 期望收到 JSON 响应，否则可能会报错重试
#         response = {
#             "took": 1,
#             "errors": False,
#             "items": []
#         }
#         self.send_response(200)
#         self.send_header('Content-type', 'application/json')
#         self.end_headers()
#         self.wfile.write(json.dumps(response).encode('utf-8'))
#
#     # 处理 HEAD/GET 请求防止报错
#     def do_GET(self):
#         self.send_response(200)
#         self.end_headers()
#
# if __name__ == '__main__':
#     # 监听 9999 端口
#     server_address = ('0.0.0.0', 9999)
#     print("🚀 伪装 ES 服务已启动，正在监听 9999 端口...")
#     print("请修改 otel-collector.yml 将 elasticsearch endpoint 指向本机:9999")
#     httpd = HTTPServer(server_address, SimpleHTTPRequestHandler)
#     httpd.serve_forever()
