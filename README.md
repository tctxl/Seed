# Seed
专注于快速Web开发

##Seed-MVC
目前文档尚不完善，目前正在完善中，使用方法可参考[stbackground](https://git.oschina.net/opdar/stbackground)

附件中有[QuickStart文档](https://git.oschina.net/opdar/Seed/attach_files)，5分钟上手使用。

Seed-MVC目前支持Restful形式的接口，可自定义各种返回视图，接口参数支持自定义Content-Type实现定制化的参数流，参数支持RequestBody用法类似于SpringMvc并且更灵活，可支持对象数组。

SeedMVC支持对方法的AOP操作，也支持对控制器的AOP操作，目前使用方法为：
创建一个类，并实现after和before方法，如：
```
public class AuthInterceptor {

    public Object before(){
        if(AuthManagement.checkAuth()){
            CacheUtils.expire(Constants.CacheKey.USER(AuthManagement.getToken()),Constants.Cache.USER_TIMEOUT);
            return true;
        }
        return new RedirectView("/admin/index.html");
    }

    public void after(){
    }
}
```
在控制器或者方法上加上@Before或者@After注解即可，如：

@Before(AuthInterceptor.class)

参见stbackground项目com.opdar.stbackground.auth.AuthInterceptor

##Seed-Database
这是一个简易的数据库操作工具，可使用该工具方便的对数据库进行ORM操作，目前已支持增删改查与事务。

##Seed-Template
自实现语法解析树，目前正在开发中，已经可以使用的语句有

输出语句：
var world = "世界";

printf(你好，${world}！)//你好，世界！

循环：
for(object in objects){...}

switch语句：
switch(x){case 1:...break;case 2:...break;}

变量定义：
var a = 100;

var b = "hello";

var c = "seed"+a;

var d = a+100*(3+1);


##Seed-CPlan
使用CPlan可将Seed开发应用无缝集成至现有项目中，实现逐步替换的目的。
CPlan目前只支持Servlet容器。
使用CPlan的项目，生成的jar包模块下必须含有package.json，如：
```
{
  "module-name":"support",
  "desc":"客服管理",
  "main":"com.xxx.background.module.support.base.SupportEntry",
  "controllers":"com.xxx.background.module.support.controller"
}
```
module-name为模块名称
main为入口点，当该模块被载入时被调用。
controllers为控制器层的包路径，当模块被载入时将自动扫描包下的控制器，并生成路由。

需要使用CPlan必须在web.xml下加入以下参数
```
	<listener>
		<listener-class>com.opdar.cplan.plugins.CPServletSupport</listener-class>
	</listener>
	<servlet>
		<servlet-name>ModuleServlet</servlet-name>
		<servlet-class>com.opdar.framework.server.supports.servlet.SeedServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>ModuleServlet</servlet-name>
		<url-pattern>/module/*</url-pattern>
	</servlet-mapping>
```

url-pattern可以根据自己的需求变换，当按照以上配置完成后，CPlan项目的访问路径将根据module-name作出变化，如：
http://.../module/support/...


目前更多功能还在开发中，希望更多的人能与我一起完善。如有不明白的地方，有能力可以直接阅读代码，或加QQ群 372824396 找到一个叫 群主 的人寻求帮助