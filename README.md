### 项目介绍

## 1. 本项目中的所有认证和授权服务都是使用的springSecurity框架来设计的,并且以下的所有认证服务
    1.1 uaa-service提供普通账号认证服务
    普通账号认证是指使用传统的用户名密码登录,而不是使用传统的jwt或token。

    
    1.2 ldap-service提供LDAP认证服务
    Ldap认证是指使用轻量级目录服务器中提供的用户名和密码登录LDAP服务器，LDAP服务器再对用户身份进行认证。需要注意的是在
    本项目中暂时未建立LDAP服务器(因为在linux服务器上建立ldap服务器的流程比较繁琐,而且本人时间有限所以就暂时先不建立了)，
    LDAP的用户数据都是写死在ldif配置文件中的。
    
    1.3 oauth2-service提供第三方登录认证服务
    第三方登录是指使用诸如微信账号，微博账号，百度账号，gitee或github等这些账号登录第三方系统，目前绝大多数互联网应用都支持使
    用这种方式来登录。在本项目中支持使用gitee账号来登录这个系统，这种登录模式一般都使用oauth2.0协议中的
    授权码模式(authorization code)来进行身份认证，所以在本项目中也是使用的这种模式。
    