1.tomcat把所有错误消息存储在一个properties文件中，properties文件分到不同的包中。

每个properties文件都是用一个StringManager类实例来处理的

StringManager的关键代码:
private static Hashtable managers = new Hashtable();
 
public synchronized static StringManager getManager(String packageName) {
    StringManager mgr = (StringManager)managers.get(packageName);
    if (mgr == null) {
        mgr = new StringManager(packageName);
        managers.put(packageName, mgr);
    }
    return mgr;
}

2.tomcat的默认连接器SocketInputStream从套接字的inputStream里读取字节流对象



3.解析http请求的5个步骤
读取套接字输入流->解析请求行->解析请求头->解析cookie->获取参数