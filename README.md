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

