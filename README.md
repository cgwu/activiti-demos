# activiti-demos

Activiti-Explorer 用户名与密码:
账号 	密码 	角色
kermit 	kermit 	admin
gonzo 	gonzo 	manager
fozzie 	fozzie 	user

/apache-tomcat-8.5.11/webapps/activiti-explorer/WEB-INF/classes/db.properties
#db=h2
#jdbc.driver=org.h2.Driver
#jdbc.url=jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000
#jdbc.username=sa
#jdbc.password=
db=postgres
jdbc.driver=org.postgresql.Driver
jdbc.url=jdbc:postgresql://localhost:5432/activiti_demo
jdbc.username=postgres
jdbc.password=postgres

Lombok使用
https://blog.csdn.net/motui/article/details/79012846

file:///home/sam/dev/activiti-5.22.0/docs/userguide/index.html
file:///home/sam/dev/activiti-5.22.0/docs/javadocs/index.html
Activiti系列————根据activiti的动态表单和外置表单来创建自己的表单框架
https://blog.csdn.net/java_dotar_01/article/details/80563176

https://github.com/mplushnikov/lombok-intellij-plugin
Required IntelliJ Configuration
In your project: Click Preferences -> Build, Execution, Deployment -> Compiler, Annotation Processors. Click Enable Annotation Processing
Afterwards you might need to do a complete rebuild of your project via Build -> Rebuild Project.

<!-- spring-security会影响url jsessionid,需要设置disable-url-rewriting="false". -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!-- https://mvnrepository.com/artifact/org.thymeleaf.extras/thymeleaf-extras-springsecurity4 -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity4</artifactId>
    <version>3.0.2.RELEASE</version>
</dependency>




