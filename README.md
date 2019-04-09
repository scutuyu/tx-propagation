# 测试Spring提供的7种事务传播行为

a()调用b()，当这两个方法都开启了事务时，如果b()在执行了部分sql之后抛出了异常，
a()捕获（不抛出），后者不捕获（即抛出）异常，对两个方法已执行sql的影响分别是：

```
    @Transactional(
            propagation = Propagation.REQUIRED,
//            propagation = Propagation.SUPPORTS,
//            propagation = Propagation.MANDATORY,
//            propagation = Propagation.REQUIRES_NEW,
//            propagation = Propagation.NOT_SUPPORTED,
//            propagation = Propagation.NEVER,
//            propagation = Propagation.NESTED,
            isolation = Isolation.DEFAULT,
            rollbackFor = Exception.class)
void a () {
    // 先执行一段sql
    executeSql();
    // 不捕获异常
    b();
    // 捕获异常，但是不抛出
    // callBWithTryCatch();
}

void callBWithTryCatch () {
    try {
        b();
    } catch () {
        log.error("捕获了b的异常,但是不抛出，只是打印出来", e);
    }
}

    @Transactional(
//            propagation = Propagation.REQUIRED,
//            propagation = Propagation.SUPPORTS,
//            propagation = Propagation.MANDATORY,
//            propagation = Propagation.REQUIRES_NEW,
//            propagation = Propagation.NOT_SUPPORTED,
            propagation = Propagation.NEVER,
//            propagation = Propagation.NESTED,
            isolation = Isolation.DEFAULT,
            rollbackFor = Exception.class)
void b () {
    // 先执行一段sql
    executeSql();
    // 再抛出异常
    int i = 12 / 0;
}
```


a() -> b() 

## 捕获异常，但是不抛出

### a() --- required

####  b() -- required

a(), b() 都回滚，a()开启了一个新的事务，b()加入当前事务，一起回滚

```
2019-04-09 19:26:05.188 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:26:05.189  INFO 8217 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:26:05.192  WARN 8217 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:26:05.405  INFO 8217 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:26:05.408 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] for JDBC transaction
2019-04-09 19:26:05.411 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] to manual commit
2019-04-09 19:26:05.421  INFO 8217 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:26:05.426 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:26:05.432 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.438 DEBUG 8217 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] will be managed by Spring
2019-04-09 19:26:05.443 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:26:05.469 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:26:05.471 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:26:05.471 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.471 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Participating in existing transaction
2019-04-09 19:26:05.476  INFO 8217 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:26:05.476 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc] from current transaction
2019-04-09 19:26:05.476 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:26:05.476 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:26:05.477 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:26:05.477 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.477 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Participating transaction failed - marking existing transaction as rollback-only
2019-04-09 19:26:05.478 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Setting JDBC transaction [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] rollback-only
2019-04-09 19:26:05.483 ERROR 8217 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1361409513.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$9fdb5d36.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1361409513.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$2c5f99b9.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:26:05.483  INFO 8217 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:26:05.484 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Global transaction is marked as rollback-only but transactional code requested commit
2019-04-09 19:26:05.484 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.484 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.485 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 19:26:05.485 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e]
2019-04-09 19:26:05.487 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] after transaction

org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only

	at org.springframework.transaction.support.AbstractPlatformTransactionManager.processRollback(AbstractPlatformTransactionManager.java:873)
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.commit(AbstractPlatformTransactionManager.java:710)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.commitTransactionAfterReturning(TransactionAspectSupport.java:533)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:304)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688)
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$2c5f99b9.a(<generated>)
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74)
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51)
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)

```

#### b() -- surports

a(), b() 都回滚，a()开启了一个新的事务，b()加入当前事务，一起回滚

```
和上一次实验结果一样
```

#### b() -- mandatory

a(), b() 都回滚，a()开启了一个新的事务，b()加入当前事务，一起回滚

```
和上一次实验结果一样
```

#### b() -- require_new

a()提交, b() 回滚，a()，b()分别开启一个新的事务

```
2019-04-09 19:39:33.579 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:39:33.580  INFO 8353 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:39:33.584  WARN 8353 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:39:33.798  INFO 8353 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:39:33.800 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] for JDBC transaction
2019-04-09 19:39:33.803 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] to manual commit
2019-04-09 19:39:33.809  INFO 8353 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:39:33.813 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:39:33.817 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.825 DEBUG 8353 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] will be managed by Spring
2019-04-09 19:39:33.836 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:39:33.871 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:39:33.872 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:39:33.873 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.873 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Suspending current transaction, creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]
2019-04-09 19:39:33.873 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.877 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad] for JDBC transaction
2019-04-09 19:39:33.877 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad] to manual commit
2019-04-09 19:39:33.883  INFO 8353 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5cbd94b2]
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad] will be managed by Spring
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:39:33.884 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:39:33.884 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5cbd94b2]
2019-04-09 19:39:33.885 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5cbd94b2]
2019-04-09 19:39:33.885 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5cbd94b2]
2019-04-09 19:39:33.885 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 19:39:33.885 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad]
2019-04-09 19:39:33.886 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad] after transaction
2019-04-09 19:39:33.888 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 19:39:33.888 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.893 ERROR 8353 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$501de9c7.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$dca2264a.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:39:33.894  INFO 8353 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:39:33.894 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.894 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.895 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.895 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 19:39:33.895 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3]
2019-04-09 19:39:33.896 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] after transaction

```

#### b() -- not_supported

a(), b() 都提交，b()回滚事务失败，没有合适的事务

```

```

#### b() -- never

a()提交，b()因为不支持事务，直接抛异常

```
2019-04-09 15:45:52.247 DEBUG 5390 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 15:45:52.248  INFO 5390 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 15:45:52.255  WARN 5390 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 15:45:52.443  INFO 5390 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 15:45:52.447 DEBUG 5390 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@294827406 wrapping com.mysql.cj.jdbc.ConnectionImpl@4f8d86e4] for JDBC transaction
2019-04-09 15:45:52.450 DEBUG 5390 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@294827406 wrapping com.mysql.cj.jdbc.ConnectionImpl@4f8d86e4] to manual commit
2019-04-09 15:45:52.457  INFO 5390 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 15:45:52.466 DEBUG 5390 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 15:45:52.477 DEBUG 5390 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c2a027c]
2019-04-09 15:45:52.490 DEBUG 5390 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@294827406 wrapping com.mysql.cj.jdbc.ConnectionImpl@4f8d86e4] will be managed by Spring
2019-04-09 15:45:52.503 DEBUG 5390 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 15:45:52.543 DEBUG 5390 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 15:45:52.547 DEBUG 5390 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 15:45:52.548 DEBUG 5390 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c2a027c]
2019-04-09 15:45:52.559 ERROR 5390 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

org.springframework.transaction.IllegalTransactionStateException: Existing transaction found for transaction marked with propagation 'never'
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.handleExistingTransaction(AbstractPlatformTransactionManager.java:406) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(AbstractPlatformTransactionManager.java:354) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary(TransactionAspectSupport.java:474) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:289) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1271084832.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 15:45:52.560  INFO 5390 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 15:45:52.561 DEBUG 5390 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c2a027c]
2019-04-09 15:45:52.561 DEBUG 5390 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c2a027c]
2019-04-09 15:45:52.562 DEBUG 5390 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c2a027c]
2019-04-09 15:45:52.562 DEBUG 5390 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 15:45:52.562 DEBUG 5390 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@294827406 wrapping com.mysql.cj.jdbc.ConnectionImpl@4f8d86e4]
2019-04-09 15:45:52.563 DEBUG 5390 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@294827406 wrapping com.mysql.cj.jdbc.ConnectionImpl@4f8d86e4] after transaction

```

#### b() -- nested

a()提交, b() 回滚到保存点，只有一个事务，但是使用了保存点技术

```
2019-04-09 16:06:12.960 DEBUG 5585 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 16:06:12.961  INFO 5585 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 16:06:12.966  WARN 5585 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 16:06:13.201  INFO 5585 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 16:06:13.204 DEBUG 5585 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] for JDBC transaction
2019-04-09 16:06:13.206 DEBUG 5585 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] to manual commit
2019-04-09 16:06:13.213  INFO 5585 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 16:06:13.218 DEBUG 5585 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 16:06:13.224 DEBUG 5585 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 16:06:13.230 DEBUG 5585 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] will be managed by Spring
2019-04-09 16:06:13.239 DEBUG 5585 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 16:06:13.271 DEBUG 5585 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 16:06:13.273 DEBUG 5585 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 16:06:13.274 DEBUG 5585 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 16:06:13.275 DEBUG 5585 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating nested transaction with name [com.tuyu.service.impl.BServiceImpl.b]
2019-04-09 16:06:13.281  INFO 5585 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 16:06:13.282 DEBUG 5585 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1] from current transaction
2019-04-09 16:06:13.282 DEBUG 5585 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 16:06:13.282 DEBUG 5585 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 16:06:13.283 DEBUG 5585 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 16:06:13.283 DEBUG 5585 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 16:06:13.283 DEBUG 5585 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back transaction to savepoint
2019-04-09 16:06:13.291 ERROR 5585 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$97cce5ca.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$2451224d.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 16:06:13.292  INFO 5585 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 16:06:13.293 DEBUG 5585 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 16:06:13.293 DEBUG 5585 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 16:06:13.294 DEBUG 5585 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 16:06:13.294 DEBUG 5585 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 16:06:13.294 DEBUG 5585 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3]
2019-04-09 16:06:13.295 DEBUG 5585 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] after transaction

```

### a() -- supports

#### b() -- required
a()提交，b()回滚，a()以非事务的方式运行

```
2019-04-09 17:18:17.183  INFO 6348 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 17:18:17.194 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:18:17.202 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5ed4bc]
2019-04-09 17:18:17.215 DEBUG 6348 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 17:18:17.217  INFO 6348 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 17:18:17.228  WARN 6348 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 17:18:17.630  INFO 6348 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 17:18:17.639 DEBUG 6348 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@780566650 wrapping com.mysql.cj.jdbc.ConnectionImpl@4548d254] will be managed by Spring
2019-04-09 17:18:17.645 DEBUG 6348 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 17:18:17.676 DEBUG 6348 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 17:18:17.679 DEBUG 6348 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 17:18:17.680 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5ed4bc]
2019-04-09 17:18:17.681 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5ed4bc]
2019-04-09 17:18:17.682 DEBUG 6348 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 17:18:17.687 DEBUG 6348 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@937860163 wrapping com.mysql.cj.jdbc.ConnectionImpl@5c7dfc05] for JDBC transaction
2019-04-09 17:18:17.688 DEBUG 6348 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@937860163 wrapping com.mysql.cj.jdbc.ConnectionImpl@5c7dfc05] to manual commit
2019-04-09 17:18:17.695  INFO 6348 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 17:18:17.695 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:18:17.695 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3e8b3b79]
2019-04-09 17:18:17.695 DEBUG 6348 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@937860163 wrapping com.mysql.cj.jdbc.ConnectionImpl@5c7dfc05] will be managed by Spring
2019-04-09 17:18:17.695 DEBUG 6348 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 17:18:17.696 DEBUG 6348 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 17:18:17.696 DEBUG 6348 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 17:18:17.696 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3e8b3b79]
2019-04-09 17:18:17.697 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3e8b3b79]
2019-04-09 17:18:17.697 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3e8b3b79]
2019-04-09 17:18:17.698 DEBUG 6348 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 17:18:17.698 DEBUG 6348 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@937860163 wrapping com.mysql.cj.jdbc.ConnectionImpl@5c7dfc05]
2019-04-09 17:18:17.699 DEBUG 6348 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@937860163 wrapping com.mysql.cj.jdbc.ConnectionImpl@5c7dfc05] after transaction
2019-04-09 17:18:17.700 DEBUG 6348 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 17:18:17.701 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5ed4bc]
2019-04-09 17:18:17.707 ERROR 6348 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1249890505.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$31256d65.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1249890505.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$bda9a9e8.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 17:18:17.707  INFO 6348 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 17:18:17.708 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5ed4bc]
2019-04-09 17:18:17.708 DEBUG 6348 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5ed4bc]

```

#### b() -- supports
a()，b()都提交，a()，b()以非事务的方式运行

```
2019-04-09 17:26:49.819  INFO 6427 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 17:26:49.823 DEBUG 6427 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:26:49.828 DEBUG 6427 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 17:26:49.835 DEBUG 6427 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 17:26:49.836  INFO 6427 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 17:26:49.841  WARN 6427 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 17:26:50.074  INFO 6427 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 17:26:50.082 DEBUG 6427 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1917860633 wrapping com.mysql.cj.jdbc.ConnectionImpl@1cfc2538] will be managed by Spring
2019-04-09 17:26:50.091 DEBUG 6427 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 17:26:50.128 DEBUG 6427 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 17:26:50.131 DEBUG 6427 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 17:26:50.132 DEBUG 6427 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 17:26:50.138  INFO 6427 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 17:26:50.138 DEBUG 6427 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c] from current transaction
2019-04-09 17:26:50.138 DEBUG 6427 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 17:26:50.139 DEBUG 6427 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 17:26:50.139 DEBUG 6427 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 17:26:50.140 DEBUG 6427 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 17:26:50.140 DEBUG 6427 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 17:26:50.145 ERROR 6427 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$31256d65.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$bda9a9e8.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 17:26:50.145  INFO 6427 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 17:26:50.146 DEBUG 6427 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 17:26:50.146 DEBUG 6427 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]

```

#### b() -- mandatory
a()提交，b()抛异常，a()以非事务的方式运行，b()因为当前没有事务，直接抛异常

```
2019-04-09 17:32:52.010  INFO 6485 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 17:32:52.016 DEBUG 6485 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:32:52.021 DEBUG 6485 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@178f268a]
2019-04-09 17:32:52.030 DEBUG 6485 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 17:32:52.031  INFO 6485 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 17:32:52.036  WARN 6485 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 17:32:52.250  INFO 6485 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 17:32:52.256 DEBUG 6485 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@877785117 wrapping com.mysql.cj.jdbc.ConnectionImpl@72503b19] will be managed by Spring
2019-04-09 17:32:52.266 DEBUG 6485 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 17:32:52.303 DEBUG 6485 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 17:32:52.307 DEBUG 6485 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 17:32:52.307 DEBUG 6485 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@178f268a]
2019-04-09 17:32:52.312 ERROR 6485 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

org.springframework.transaction.IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory'
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(AbstractPlatformTransactionManager.java:364) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary(TransactionAspectSupport.java:474) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:289) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/892262157.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 17:32:52.313  INFO 6485 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 17:32:52.314 DEBUG 6485 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@178f268a]
2019-04-09 17:32:52.314 DEBUG 6485 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@178f268a]

```

#### b() -- requires_new
a()提交，b()回滚，a()以非事务的方式运行，b()创建了一个事务

```
2019-04-09 17:36:41.018  INFO 6524 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 17:36:41.023 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:36:41.029 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 17:36:41.038 DEBUG 6524 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 17:36:41.039  INFO 6524 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 17:36:41.044  WARN 6524 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 17:36:41.269  INFO 6524 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 17:36:41.279 DEBUG 6524 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@674667952 wrapping com.mysql.cj.jdbc.ConnectionImpl@30893e08] will be managed by Spring
2019-04-09 17:36:41.287 DEBUG 6524 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 17:36:41.318 DEBUG 6524 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 17:36:41.321 DEBUG 6524 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 17:36:41.322 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 17:36:41.322 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 17:36:41.323 DEBUG 6524 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_REQUIRES_NEW,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 17:36:41.327 DEBUG 6524 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8] for JDBC transaction
2019-04-09 17:36:41.327 DEBUG 6524 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8] to manual commit
2019-04-09 17:36:41.334  INFO 6524 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 17:36:41.334 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:36:41.334 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@345d053b]
2019-04-09 17:36:41.334 DEBUG 6524 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8] will be managed by Spring
2019-04-09 17:36:41.334 DEBUG 6524 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 17:36:41.334 DEBUG 6524 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 17:36:41.335 DEBUG 6524 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 17:36:41.335 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@345d053b]
2019-04-09 17:36:41.336 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@345d053b]
2019-04-09 17:36:41.336 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@345d053b]
2019-04-09 17:36:41.336 DEBUG 6524 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 17:36:41.336 DEBUG 6524 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8]
2019-04-09 17:36:41.337 DEBUG 6524 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8] after transaction
2019-04-09 17:36:41.339 DEBUG 6524 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 17:36:41.339 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 17:36:41.348 ERROR 6524 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1728465884.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$31256d65.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1728465884.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$bda9a9e8.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 17:36:41.348  INFO 6524 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 17:36:41.349 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 17:36:41.349 DEBUG 6524 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]

```

#### b() -- not_supported
a()，b()都提交，a()，b()都以非事务的方式运行

```
2019-04-09 17:40:41.265  INFO 6570 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 17:40:41.270 DEBUG 6570 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:40:41.274 DEBUG 6570 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]
2019-04-09 17:40:41.281 DEBUG 6570 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 17:40:41.282  INFO 6570 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 17:40:41.286  WARN 6570 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 17:40:41.502  INFO 6570 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 17:40:41.506 DEBUG 6570 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@307036850 wrapping com.mysql.cj.jdbc.ConnectionImpl@3451f01d] will be managed by Spring
2019-04-09 17:40:41.514 DEBUG 6570 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 17:40:41.542 DEBUG 6570 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 17:40:41.546 DEBUG 6570 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 17:40:41.547 DEBUG 6570 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]
2019-04-09 17:40:41.553  INFO 6570 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 17:40:41.553 DEBUG 6570 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408] from current transaction
2019-04-09 17:40:41.553 DEBUG 6570 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 17:40:41.554 DEBUG 6570 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 17:40:41.555 DEBUG 6570 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 17:40:41.555 DEBUG 6570 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]
2019-04-09 17:40:41.555 DEBUG 6570 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 17:40:41.560 ERROR 6570 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/356539350.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/356539350.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 17:40:41.560  INFO 6570 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 17:40:41.561 DEBUG 6570 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]
2019-04-09 17:40:41.561 DEBUG 6570 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]

```

#### b() -- never
a()，b()都提交，a()，b()都以非事务的方式运行

```
2019-04-09 17:43:35.709  INFO 6601 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 17:43:35.714 DEBUG 6601 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:43:35.718 DEBUG 6601 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]
2019-04-09 17:43:35.726 DEBUG 6601 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 17:43:35.727  INFO 6601 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 17:43:35.731  WARN 6601 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 17:43:35.938  INFO 6601 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 17:43:35.944 DEBUG 6601 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@307036850 wrapping com.mysql.cj.jdbc.ConnectionImpl@3451f01d] will be managed by Spring
2019-04-09 17:43:35.950 DEBUG 6601 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 17:43:35.981 DEBUG 6601 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 17:43:35.985 DEBUG 6601 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 17:43:35.985 DEBUG 6601 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]
2019-04-09 17:43:35.992  INFO 6601 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 17:43:35.992 DEBUG 6601 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408] from current transaction
2019-04-09 17:43:35.992 DEBUG 6601 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 17:43:35.992 DEBUG 6601 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 17:43:35.993 DEBUG 6601 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 17:43:35.993 DEBUG 6601 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]
2019-04-09 17:43:35.994 DEBUG 6601 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 17:43:36.002 ERROR 6601 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/356539350.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/356539350.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 17:43:36.002  INFO 6601 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 17:43:36.003 DEBUG 6601 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]
2019-04-09 17:43:36.003 DEBUG 6601 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@862f408]

```

#### b() -- nested
a()提交，b()回滚，a()以非事务的方式运行，b()开启一个新事务，并回滚了

```
2019-04-09 17:44:33.130  INFO 6613 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 17:44:33.135 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:44:33.141 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@571a01f9]
2019-04-09 17:44:33.149 DEBUG 6613 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 17:44:33.151  INFO 6613 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 17:44:33.156  WARN 6613 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 17:44:33.395  INFO 6613 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 17:44:33.399 DEBUG 6613 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1780313842 wrapping com.mysql.cj.jdbc.ConnectionImpl@2721044] will be managed by Spring
2019-04-09 17:44:33.406 DEBUG 6613 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 17:44:33.444 DEBUG 6613 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 17:44:33.448 DEBUG 6613 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 17:44:33.448 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@571a01f9]
2019-04-09 17:44:33.449 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@571a01f9]
2019-04-09 17:44:33.450 DEBUG 6613 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 17:44:33.454 DEBUG 6613 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1572557043 wrapping com.mysql.cj.jdbc.ConnectionImpl@4a2e7bcb] for JDBC transaction
2019-04-09 17:44:33.454 DEBUG 6613 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1572557043 wrapping com.mysql.cj.jdbc.ConnectionImpl@4a2e7bcb] to manual commit
2019-04-09 17:44:33.462  INFO 6613 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 17:44:33.463 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 17:44:33.463 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@73c3cd09]
2019-04-09 17:44:33.463 DEBUG 6613 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1572557043 wrapping com.mysql.cj.jdbc.ConnectionImpl@4a2e7bcb] will be managed by Spring
2019-04-09 17:44:33.463 DEBUG 6613 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 17:44:33.463 DEBUG 6613 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 17:44:33.464 DEBUG 6613 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 17:44:33.464 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@73c3cd09]
2019-04-09 17:44:33.465 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@73c3cd09]
2019-04-09 17:44:33.465 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@73c3cd09]
2019-04-09 17:44:33.466 DEBUG 6613 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 17:44:33.466 DEBUG 6613 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1572557043 wrapping com.mysql.cj.jdbc.ConnectionImpl@4a2e7bcb]
2019-04-09 17:44:33.467 DEBUG 6613 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1572557043 wrapping com.mysql.cj.jdbc.ConnectionImpl@4a2e7bcb] after transaction
2019-04-09 17:44:33.468 DEBUG 6613 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 17:44:33.469 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@571a01f9]
2019-04-09 17:44:33.478 ERROR 6613 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1979825302.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$aebaecc7.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1979825302.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$3b3f294a.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 17:44:33.479  INFO 6613 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 17:44:33.479 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@571a01f9]
2019-04-09 17:44:33.479 DEBUG 6613 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@571a01f9]
2019-04-09 17:44:33.486  INFO 6613 --- [       Thread-2] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...

```

### a() --- mandatory

#### b() -- required  
#### b() -- supports  
#### b() -- mandatory  
#### b() -- requires_new 
#### b() -- not_supported
#### b() -- never
#### b() -- nested
a()抛异常，b()不执行

```

org.springframework.transaction.IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory'

	at org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(AbstractPlatformTransactionManager.java:364)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary(TransactionAspectSupport.java:474)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:289)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688)
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$2c5f99b9.a(<generated>)
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74)
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51)
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)

```

### a() --- requires_new

#### b() -- required 
a()，b()都回滚，a()开启一个新的事务，b()加入已有的事务，a()和b()在同一个事务，一起回滚

```
2019-04-09 18:17:00.676  INFO 7549 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
   2019-04-09 18:17:00.681  WARN 7549 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
   2019-04-09 18:17:00.903  INFO 7549 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
   2019-04-09 18:17:00.907 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] for JDBC transaction
   2019-04-09 18:17:00.910 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] to manual commit
   2019-04-09 18:17:00.920  INFO 7549 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
   2019-04-09 18:17:00.926 DEBUG 7549 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
   2019-04-09 18:17:00.930 DEBUG 7549 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
   2019-04-09 18:17:00.936 DEBUG 7549 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] will be managed by Spring
   2019-04-09 18:17:00.941 DEBUG 7549 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
   2019-04-09 18:17:00.970 DEBUG 7549 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
   2019-04-09 18:17:00.972 DEBUG 7549 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
   2019-04-09 18:17:00.973 DEBUG 7549 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
   2019-04-09 18:17:00.973 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Participating in existing transaction
   2019-04-09 18:17:00.977  INFO 7549 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
   2019-04-09 18:17:00.978 DEBUG 7549 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93] from current transaction
   2019-04-09 18:17:00.978 DEBUG 7549 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
   2019-04-09 18:17:00.978 DEBUG 7549 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
   2019-04-09 18:17:00.979 DEBUG 7549 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
   2019-04-09 18:17:00.979 DEBUG 7549 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
   2019-04-09 18:17:00.979 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Participating transaction failed - marking existing transaction as rollback-only
   2019-04-09 18:17:00.979 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Setting JDBC transaction [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] rollback-only
   2019-04-09 18:17:00.984 ERROR 7549 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来
   
   java.lang.ArithmeticException: / by zero
   	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
   	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
   	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1112400678.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
   	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
   	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
   	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
   	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1112400678.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
   	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
   	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
   	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
   	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
   	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
   	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
   	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
   	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
   	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
   	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
   	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
   	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
   	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
   	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
   	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
   	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
   	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
   	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
   	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
   	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
   	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
   	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]
   
   2019-04-09 18:17:00.984  INFO 7549 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
   2019-04-09 18:17:00.984 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Global transaction is marked as rollback-only but transactional code requested commit
   2019-04-09 18:17:00.985 DEBUG 7549 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
   2019-04-09 18:17:00.985 DEBUG 7549 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
   2019-04-09 18:17:00.985 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
   2019-04-09 18:17:00.985 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e]
   2019-04-09 18:17:00.988 DEBUG 7549 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] after transaction
   
   org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only
   
   	at org.springframework.transaction.support.AbstractPlatformTransactionManager.processRollback(AbstractPlatformTransactionManager.java:873)
   	at org.springframework.transaction.support.AbstractPlatformTransactionManager.commit(AbstractPlatformTransactionManager.java:710)
   	at org.springframework.transaction.interceptor.TransactionAspectSupport.commitTransactionAfterReturning(TransactionAspectSupport.java:533)
   	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:304)
   	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98)
   	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
   	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688)
   	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>)
   	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20)
   	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
   	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
   	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
   	at java.lang.reflect.Method.invoke(Method.java:497)
   	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
   	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
   	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
   	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
   	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74)
   	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84)
   	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75)
   	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86)
   	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84)
   	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
   	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251)
   	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97)
   	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
   	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
   	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
   	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
   	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
   	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
   	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
   	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
   	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190)
   	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
   	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
   	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51)
   	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
   	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)


```

#### b() -- supports 
a()，b()都回滚，a()开启一个新的事务，b()加入已有的事务，a()和b()在同一个事务，一起回滚

```
结果与上一个实验一致
```

#### b() -- mandatory 
a()，b()都回滚，a()开启一个新的事务，b()加入已有的事务，a()和b()在同一个事务，一起回滚

```
结果与上一个实验一致
```

#### b() -- requires_new 
a()提交，b()回滚，a()，b()分别开启一个新的事务

```
2019-04-09 18:28:50.144 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_REQUIRES_NEW,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 18:28:50.145  INFO 7655 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:28:50.149  WARN 7655 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:28:50.361  INFO 7655 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:28:50.363 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0] for JDBC transaction
2019-04-09 18:28:50.367 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0] to manual commit
2019-04-09 18:28:50.374  INFO 7655 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:28:50.380 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:28:50.389 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:28:50.395 DEBUG 7655 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0] will be managed by Spring
2019-04-09 18:28:50.403 DEBUG 7655 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:28:50.430 DEBUG 7655 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:28:50.432 DEBUG 7655 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:28:50.432 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:28:50.432 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Suspending current transaction, creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]
2019-04-09 18:28:50.433 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:28:50.438 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1239728853 wrapping com.mysql.cj.jdbc.ConnectionImpl@24a2e565] for JDBC transaction
2019-04-09 18:28:50.438 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1239728853 wrapping com.mysql.cj.jdbc.ConnectionImpl@24a2e565] to manual commit
2019-04-09 18:28:50.443  INFO 7655 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 18:28:50.443 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:28:50.443 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f5cf29b]
2019-04-09 18:28:50.443 DEBUG 7655 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1239728853 wrapping com.mysql.cj.jdbc.ConnectionImpl@24a2e565] will be managed by Spring
2019-04-09 18:28:50.443 DEBUG 7655 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 18:28:50.443 DEBUG 7655 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 18:28:50.444 DEBUG 7655 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 18:28:50.444 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f5cf29b]
2019-04-09 18:28:50.445 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f5cf29b]
2019-04-09 18:28:50.445 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f5cf29b]
2019-04-09 18:28:50.445 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 18:28:50.445 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1239728853 wrapping com.mysql.cj.jdbc.ConnectionImpl@24a2e565]
2019-04-09 18:28:50.450 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1239728853 wrapping com.mysql.cj.jdbc.ConnectionImpl@24a2e565] after transaction
2019-04-09 18:28:50.454 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 18:28:50.455 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:28:50.468 ERROR 7655 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/768669591.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/768669591.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:28:50.469  INFO 7655 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:28:50.471 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:28:50.472 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:28:50.472 DEBUG 7655 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:28:50.472 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 18:28:50.472 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0]
2019-04-09 18:28:50.474 DEBUG 7655 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0] after transaction

```

#### b() -- not_supported 
a()提交，b()以非事务的方式执行，a()开启一个新的事务

```
2019-04-09 18:31:19.484 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_REQUIRES_NEW,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 18:31:19.485  INFO 7678 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:31:19.489  WARN 7678 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:31:19.769  INFO 7678 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:31:19.776 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0] for JDBC transaction
2019-04-09 18:31:19.784 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0] to manual commit
2019-04-09 18:31:19.797  INFO 7678 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:31:19.806 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:31:19.813 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:31:19.821 DEBUG 7678 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0] will be managed by Spring
2019-04-09 18:31:19.828 DEBUG 7678 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:31:19.865 DEBUG 7678 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:31:19.868 DEBUG 7678 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:31:19.869 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:31:19.869 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Suspending current transaction
2019-04-09 18:31:19.871 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:31:19.886  INFO 7678 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 18:31:19.887 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:31:19.887 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@24a2e565]
2019-04-09 18:31:19.888 DEBUG 7678 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 18:31:19.890 DEBUG 7678 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1551760389 wrapping com.mysql.cj.jdbc.ConnectionImpl@345d053b] will be managed by Spring
2019-04-09 18:31:19.890 DEBUG 7678 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 18:31:19.891 DEBUG 7678 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 18:31:19.894 DEBUG 7678 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 18:31:19.894 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@24a2e565]
2019-04-09 18:31:19.895 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@24a2e565]
2019-04-09 18:31:19.895 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@24a2e565]
2019-04-09 18:31:19.898 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 18:31:19.898 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 18:31:19.898 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:31:19.907 ERROR 7678 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/768669591.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/768669591.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:31:19.908  INFO 7678 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:31:19.908 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:31:19.908 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:31:19.908 DEBUG 7678 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@118dcbbd]
2019-04-09 18:31:19.908 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 18:31:19.909 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0]
2019-04-09 18:31:19.909 DEBUG 7678 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1334675172 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f631ca0] after transaction

```

#### b() -- never 
a()提交，b()抛异常，a()开启一个新的事务

```
2019-04-09 18:33:09.082 DEBUG 7699 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_REQUIRES_NEW,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 18:33:09.082  INFO 7699 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:33:09.086  WARN 7699 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:33:09.303  INFO 7699 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:33:09.305 DEBUG 7699 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] for JDBC transaction
2019-04-09 18:33:09.307 DEBUG 7699 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] to manual commit
2019-04-09 18:33:09.317  INFO 7699 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:33:09.324 DEBUG 7699 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:33:09.332 DEBUG 7699 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 18:33:09.338 DEBUG 7699 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] will be managed by Spring
2019-04-09 18:33:09.343 DEBUG 7699 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:33:09.368 DEBUG 7699 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:33:09.370 DEBUG 7699 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:33:09.371 DEBUG 7699 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 18:33:09.376 ERROR 7699 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

org.springframework.transaction.IllegalTransactionStateException: Existing transaction found for transaction marked with propagation 'never'
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.handleExistingTransaction(AbstractPlatformTransactionManager.java:406) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(AbstractPlatformTransactionManager.java:354) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary(TransactionAspectSupport.java:474) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:289) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$31256d65.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$bda9a9e8.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:33:09.377  INFO 7699 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:33:09.377 DEBUG 7699 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 18:33:09.378 DEBUG 7699 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 18:33:09.378 DEBUG 7699 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 18:33:09.378 DEBUG 7699 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 18:33:09.378 DEBUG 7699 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3]
2019-04-09 18:33:09.381 DEBUG 7699 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] after transaction

```

#### b() -- nested 
a()提交，b()回滚到保存点，a()开启一个新的事务，b()在当前事务的基础上执行嵌套事务

```
2019-04-09 18:36:20.205 DEBUG 7730 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_REQUIRES_NEW,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 18:36:20.206  INFO 7730 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:36:20.210  WARN 7730 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:36:20.433  INFO 7730 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:36:20.437 DEBUG 7730 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05] for JDBC transaction
2019-04-09 18:36:20.440 DEBUG 7730 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05] to manual commit
2019-04-09 18:36:20.449  INFO 7730 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:36:20.455 DEBUG 7730 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:36:20.459 DEBUG 7730 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 18:36:20.465 DEBUG 7730 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05] will be managed by Spring
2019-04-09 18:36:20.470 DEBUG 7730 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:36:20.497 DEBUG 7730 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:36:20.498 DEBUG 7730 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:36:20.499 DEBUG 7730 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 18:36:20.499 DEBUG 7730 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating nested transaction with name [com.tuyu.service.impl.BServiceImpl.b]
2019-04-09 18:36:20.508  INFO 7730 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 18:36:20.508 DEBUG 7730 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68] from current transaction
2019-04-09 18:36:20.509 DEBUG 7730 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 18:36:20.509 DEBUG 7730 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 18:36:20.509 DEBUG 7730 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 18:36:20.509 DEBUG 7730 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 18:36:20.510 DEBUG 7730 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back transaction to savepoint
2019-04-09 18:36:20.515 ERROR 7730 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/356539350.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/356539350.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:36:20.516  INFO 7730 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:36:20.517 DEBUG 7730 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 18:36:20.517 DEBUG 7730 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 18:36:20.518 DEBUG 7730 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 18:36:20.518 DEBUG 7730 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 18:36:20.518 DEBUG 7730 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05]
2019-04-09 18:36:20.519 DEBUG 7730 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05] after transaction

```

### a() --- not_supported

#### b() -- required 
a()提交，b()回滚，a()以非事务的方式执行，b()开启一个新的事务

```
2019-04-09 18:40:18.799  INFO 7773 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:40:18.805 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:40:18.810 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 18:40:18.817 DEBUG 7773 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 18:40:18.818  INFO 7773 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:40:18.822  WARN 7773 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:40:19.040  INFO 7773 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:40:19.048 DEBUG 7773 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1917860633 wrapping com.mysql.cj.jdbc.ConnectionImpl@1cfc2538] will be managed by Spring
2019-04-09 18:40:19.056 DEBUG 7773 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:40:19.086 DEBUG 7773 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:40:19.089 DEBUG 7773 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:40:19.090 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 18:40:19.091 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 18:40:19.092 DEBUG 7773 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 18:40:19.097 DEBUG 7773 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1555928242 wrapping com.mysql.cj.jdbc.ConnectionImpl@6824b913] for JDBC transaction
2019-04-09 18:40:19.097 DEBUG 7773 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1555928242 wrapping com.mysql.cj.jdbc.ConnectionImpl@6824b913] to manual commit
2019-04-09 18:40:19.102  INFO 7773 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 18:40:19.102 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:40:19.102 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@40729f01]
2019-04-09 18:40:19.102 DEBUG 7773 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1555928242 wrapping com.mysql.cj.jdbc.ConnectionImpl@6824b913] will be managed by Spring
2019-04-09 18:40:19.102 DEBUG 7773 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 18:40:19.102 DEBUG 7773 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 18:40:19.103 DEBUG 7773 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 18:40:19.103 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@40729f01]
2019-04-09 18:40:19.104 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@40729f01]
2019-04-09 18:40:19.104 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@40729f01]
2019-04-09 18:40:19.104 DEBUG 7773 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 18:40:19.105 DEBUG 7773 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1555928242 wrapping com.mysql.cj.jdbc.ConnectionImpl@6824b913]
2019-04-09 18:40:19.106 DEBUG 7773 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1555928242 wrapping com.mysql.cj.jdbc.ConnectionImpl@6824b913] after transaction
2019-04-09 18:40:19.110 DEBUG 7773 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 18:40:19.111 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 18:40:19.117 ERROR 7773 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$31256d65.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$bda9a9e8.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:40:19.118  INFO 7773 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:40:19.118 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]
2019-04-09 18:40:19.118 DEBUG 7773 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@33ecbd6c]

```

#### b() -- supports 
a()，b()都提交，a()，b()都以非事务的方式执行

```
2019-04-09 18:43:00.295  INFO 7804 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:43:00.301 DEBUG 7804 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:43:00.310 DEBUG 7804 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@100c8b75]
2019-04-09 18:43:00.324 DEBUG 7804 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 18:43:00.325  INFO 7804 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:43:00.329  WARN 7804 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:43:00.550  INFO 7804 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:43:00.555 DEBUG 7804 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1727171103 wrapping com.mysql.cj.jdbc.ConnectionImpl@60a19573] will be managed by Spring
2019-04-09 18:43:00.561 DEBUG 7804 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:43:00.595 DEBUG 7804 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:43:00.601 DEBUG 7804 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:43:00.601 DEBUG 7804 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@100c8b75]
2019-04-09 18:43:00.606  INFO 7804 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 18:43:00.607 DEBUG 7804 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@100c8b75] from current transaction
2019-04-09 18:43:00.607 DEBUG 7804 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 18:43:00.607 DEBUG 7804 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 18:43:00.608 DEBUG 7804 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 18:43:00.608 DEBUG 7804 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@100c8b75]
2019-04-09 18:43:00.608 DEBUG 7804 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 18:43:00.615 ERROR 7804 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/21331934.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$90f91b99.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/21331934.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$1d7d581c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:43:00.616  INFO 7804 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:43:00.617 DEBUG 7804 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@100c8b75]
2019-04-09 18:43:00.618 DEBUG 7804 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@100c8b75]

```

#### b() -- mandatory 
a()提交，b()抛异常，a()以非事务的方式执行

```
2019-04-09 18:45:34.163  INFO 7833 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:45:34.169 DEBUG 7833 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:45:34.175 DEBUG 7833 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]
2019-04-09 18:45:34.187 DEBUG 7833 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 18:45:34.188  INFO 7833 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:45:34.192  WARN 7833 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:45:34.446  INFO 7833 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:45:34.455 DEBUG 7833 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1789376127 wrapping com.mysql.cj.jdbc.ConnectionImpl@6bcc3f27] will be managed by Spring
2019-04-09 18:45:34.460 DEBUG 7833 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:45:34.498 DEBUG 7833 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:45:34.503 DEBUG 7833 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:45:34.504 DEBUG 7833 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]
2019-04-09 18:45:34.510 ERROR 7833 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

org.springframework.transaction.IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory'
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(AbstractPlatformTransactionManager.java:364) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary(TransactionAspectSupport.java:474) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:289) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$b8407bb1.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/925024581.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$44c4b834.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:45:34.511  INFO 7833 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:45:34.512 DEBUG 7833 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]
2019-04-09 18:45:34.514 DEBUG 7833 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]

```

#### b() -- requires_new 
a()提交，b()回滚，a()以非事务的方式执行，b()开启了一个新的事务

```
2019-04-09 18:47:55.907  INFO 7860 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:47:55.914 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:47:55.920 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@68ac9ec5]
2019-04-09 18:47:55.931 DEBUG 7860 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 18:47:55.931  INFO 7860 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:47:55.936  WARN 7860 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:47:56.183  INFO 7860 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:47:56.187 DEBUG 7860 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@674667952 wrapping com.mysql.cj.jdbc.ConnectionImpl@30893e08] will be managed by Spring
2019-04-09 18:47:56.196 DEBUG 7860 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:47:56.230 DEBUG 7860 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:47:56.236 DEBUG 7860 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:47:56.237 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@68ac9ec5]
2019-04-09 18:47:56.237 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@68ac9ec5]
2019-04-09 18:47:56.238 DEBUG 7860 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_REQUIRES_NEW,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 18:47:56.242 DEBUG 7860 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8] for JDBC transaction
2019-04-09 18:47:56.242 DEBUG 7860 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8] to manual commit
2019-04-09 18:47:56.248  INFO 7860 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 18:47:56.248 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:47:56.249 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@345d053b]
2019-04-09 18:47:56.249 DEBUG 7860 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8] will be managed by Spring
2019-04-09 18:47:56.249 DEBUG 7860 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 18:47:56.249 DEBUG 7860 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 18:47:56.250 DEBUG 7860 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 18:47:56.250 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@345d053b]
2019-04-09 18:47:56.250 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@345d053b]
2019-04-09 18:47:56.251 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@345d053b]
2019-04-09 18:47:56.251 DEBUG 7860 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 18:47:56.251 DEBUG 7860 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8]
2019-04-09 18:47:56.252 DEBUG 7860 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@257749659 wrapping com.mysql.cj.jdbc.ConnectionImpl@3c66b7d8] after transaction
2019-04-09 18:47:56.253 DEBUG 7860 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 18:47:56.253 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@68ac9ec5]
2019-04-09 18:47:56.258 ERROR 7860 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/768669591.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/768669591.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:47:56.259  INFO 7860 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:47:56.259 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@68ac9ec5]
2019-04-09 18:47:56.259 DEBUG 7860 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@68ac9ec5]

```

#### b() -- not_supported 
a()，b()都提交，a()，b()都以非事务的方式执行

```
2019-04-09 18:49:35.439  INFO 7874 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 18:49:35.445 DEBUG 7874 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 18:49:35.451 DEBUG 7874 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 18:49:35.459 DEBUG 7874 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 18:49:35.460  INFO 7874 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 18:49:35.465  WARN 7874 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 18:49:35.672  INFO 7874 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 18:49:35.680 DEBUG 7874 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@543028700 wrapping com.mysql.cj.jdbc.ConnectionImpl@6aa7b67f] will be managed by Spring
2019-04-09 18:49:35.686 DEBUG 7874 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 18:49:35.720 DEBUG 7874 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 18:49:35.724 DEBUG 7874 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 18:49:35.724 DEBUG 7874 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 18:49:35.730  INFO 7874 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 18:49:35.731 DEBUG 7874 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f] from current transaction
2019-04-09 18:49:35.731 DEBUG 7874 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 18:49:35.731 DEBUG 7874 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 18:49:35.733 DEBUG 7874 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 18:49:35.733 DEBUG 7874 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 18:49:35.734 DEBUG 7874 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 18:49:35.742 ERROR 7874 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1637000661.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1637000661.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 18:49:35.743  INFO 7874 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 18:49:35.744 DEBUG 7874 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 18:49:35.744 DEBUG 7874 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]

```

#### b() -- never 
a()，b()都提交，a()，b()都以非事务的方式执行

```
2019-04-09 19:08:58.845  INFO 8051 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:08:58.851 DEBUG 8051 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:08:58.859 DEBUG 8051 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 19:08:58.869 DEBUG 8051 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:08:58.870  INFO 8051 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:08:58.876  WARN 8051 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:08:59.124  INFO 8051 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:08:59.132 DEBUG 8051 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@814300680 wrapping com.mysql.cj.jdbc.ConnectionImpl@2e86807a] will be managed by Spring
2019-04-09 19:08:59.137 DEBUG 8051 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:08:59.173 DEBUG 8051 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:08:59.179 DEBUG 8051 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:08:59.180 DEBUG 8051 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 19:08:59.184  INFO 8051 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:08:59.185 DEBUG 8051 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709] from current transaction
2019-04-09 19:08:59.185 DEBUG 8051 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:08:59.185 DEBUG 8051 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:08:59.186 DEBUG 8051 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:08:59.186 DEBUG 8051 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 19:08:59.187 DEBUG 8051 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 19:08:59.195 ERROR 8051 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1728465884.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1728465884.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:08:59.196  INFO 8051 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:08:59.197 DEBUG 8051 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 19:08:59.197 DEBUG 8051 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]

```

#### b() -- nested 
a()提交，b()回滚，a()以非事务的方式执行，b()开启一个新的事务

```
2019-04-09 19:11:08.077  INFO 8074 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:11:08.082 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:11:08.088 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]
2019-04-09 19:11:08.098 DEBUG 8074 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:11:08.099  INFO 8074 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:11:08.106  WARN 8074 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:11:08.335  INFO 8074 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:11:08.343 DEBUG 8074 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1789376127 wrapping com.mysql.cj.jdbc.ConnectionImpl@6bcc3f27] will be managed by Spring
2019-04-09 19:11:08.350 DEBUG 8074 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:11:08.384 DEBUG 8074 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:11:08.388 DEBUG 8074 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:11:08.390 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]
2019-04-09 19:11:08.392 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]
2019-04-09 19:11:08.394 DEBUG 8074 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:11:08.398 DEBUG 8074 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1962420141 wrapping com.mysql.cj.jdbc.ConnectionImpl@73c3cd09] for JDBC transaction
2019-04-09 19:11:08.398 DEBUG 8074 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1962420141 wrapping com.mysql.cj.jdbc.ConnectionImpl@73c3cd09] to manual commit
2019-04-09 19:11:08.403  INFO 8074 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:11:08.403 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:11:08.403 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@49e4c2d5]
2019-04-09 19:11:08.403 DEBUG 8074 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1962420141 wrapping com.mysql.cj.jdbc.ConnectionImpl@73c3cd09] will be managed by Spring
2019-04-09 19:11:08.403 DEBUG 8074 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:11:08.403 DEBUG 8074 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:11:08.404 DEBUG 8074 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:11:08.404 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@49e4c2d5]
2019-04-09 19:11:08.404 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@49e4c2d5]
2019-04-09 19:11:08.405 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@49e4c2d5]
2019-04-09 19:11:08.405 DEBUG 8074 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 19:11:08.405 DEBUG 8074 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1962420141 wrapping com.mysql.cj.jdbc.ConnectionImpl@73c3cd09]
2019-04-09 19:11:08.406 DEBUG 8074 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1962420141 wrapping com.mysql.cj.jdbc.ConnectionImpl@73c3cd09] after transaction
2019-04-09 19:11:08.407 DEBUG 8074 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 19:11:08.407 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]
2019-04-09 19:11:08.413 ERROR 8074 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/925024581.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$31256d65.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/925024581.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$bda9a9e8.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:11:08.414  INFO 8074 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:11:08.414 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]
2019-04-09 19:11:08.414 DEBUG 8074 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@f4a3a8d]

```

### a() --- never

#### b() --- required
a()提交，b()回滚，a()以非事务的方式执行，b()开启一个新的事务

```
2019-04-09 19:14:26.745  INFO 8102 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:14:26.751 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:14:26.755 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1c26273d]
2019-04-09 19:14:26.766 DEBUG 8102 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:14:26.767  INFO 8102 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:14:26.771  WARN 8102 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:14:27.008  INFO 8102 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:14:27.016 DEBUG 8102 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@500646211 wrapping com.mysql.cj.jdbc.ConnectionImpl@526a9908] will be managed by Spring
2019-04-09 19:14:27.026 DEBUG 8102 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:14:27.063 DEBUG 8102 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:14:27.067 DEBUG 8102 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:14:27.067 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1c26273d]
2019-04-09 19:14:27.068 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1c26273d]
2019-04-09 19:14:27.069 DEBUG 8102 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:14:27.072 DEBUG 8102 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@813932100 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f174dd2] for JDBC transaction
2019-04-09 19:14:27.072 DEBUG 8102 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@813932100 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f174dd2] to manual commit
2019-04-09 19:14:27.078  INFO 8102 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:14:27.078 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:14:27.079 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@697173d9]
2019-04-09 19:14:27.079 DEBUG 8102 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@813932100 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f174dd2] will be managed by Spring
2019-04-09 19:14:27.079 DEBUG 8102 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:14:27.079 DEBUG 8102 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:14:27.080 DEBUG 8102 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:14:27.080 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@697173d9]
2019-04-09 19:14:27.080 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@697173d9]
2019-04-09 19:14:27.080 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@697173d9]
2019-04-09 19:14:27.081 DEBUG 8102 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 19:14:27.081 DEBUG 8102 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@813932100 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f174dd2]
2019-04-09 19:14:27.082 DEBUG 8102 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@813932100 wrapping com.mysql.cj.jdbc.ConnectionImpl@5f174dd2] after transaction
2019-04-09 19:14:27.083 DEBUG 8102 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 19:14:27.084 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1c26273d]
2019-04-09 19:14:27.090 ERROR 8102 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/499339307.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$7fbb3caa.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/499339307.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$c3f792d.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:14:27.090  INFO 8102 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:14:27.090 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1c26273d]
2019-04-09 19:14:27.090 DEBUG 8102 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1c26273d]

```

#### b() --- supports
a()，b()都提交，a()，b()都以非事务的方式执行

```
2019-04-09 19:16:56.799  INFO 8124 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:16:56.804 DEBUG 8124 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:16:56.810 DEBUG 8124 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@30f74e79]
2019-04-09 19:16:56.818 DEBUG 8124 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:16:56.819  INFO 8124 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:16:56.827  WARN 8124 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:16:57.031  INFO 8124 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:16:57.037 DEBUG 8124 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1700751834 wrapping com.mysql.cj.jdbc.ConnectionImpl@43b5021c] will be managed by Spring
2019-04-09 19:16:57.043 DEBUG 8124 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:16:57.081 DEBUG 8124 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:16:57.085 DEBUG 8124 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:16:57.086 DEBUG 8124 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@30f74e79]
2019-04-09 19:16:57.090  INFO 8124 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:16:57.091 DEBUG 8124 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@30f74e79] from current transaction
2019-04-09 19:16:57.092 DEBUG 8124 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:16:57.092 DEBUG 8124 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:16:57.094 DEBUG 8124 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:16:57.094 DEBUG 8124 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@30f74e79]
2019-04-09 19:16:57.095 DEBUG 8124 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 19:16:57.105 ERROR 8124 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1112400678.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1112400678.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:16:57.106  INFO 8124 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:16:57.107 DEBUG 8124 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@30f74e79]
2019-04-09 19:16:57.107 DEBUG 8124 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@30f74e79]

```

#### b() --- mandatory
a()提交，b()抛异常，a()以非事务的方式执行

```
2019-04-09 19:18:17.647  INFO 8142 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:18:17.651 DEBUG 8142 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:18:17.655 DEBUG 8142 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2bc378f7]
2019-04-09 19:18:17.662 DEBUG 8142 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:18:17.663  INFO 8142 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:18:17.667  WARN 8142 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:18:17.878  INFO 8142 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:18:17.882 DEBUG 8142 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1621202291 wrapping com.mysql.cj.jdbc.ConnectionImpl@44a085e5] will be managed by Spring
2019-04-09 19:18:17.887 DEBUG 8142 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:18:17.919 DEBUG 8142 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:18:17.922 DEBUG 8142 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:18:17.922 DEBUG 8142 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2bc378f7]
2019-04-09 19:18:17.930 ERROR 8142 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

org.springframework.transaction.IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory'
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(AbstractPlatformTransactionManager.java:364) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary(TransactionAspectSupport.java:474) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:289) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$31256d65.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1872034717.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$bda9a9e8.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:18:17.932  INFO 8142 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:18:17.934 DEBUG 8142 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2bc378f7]
2019-04-09 19:18:17.935 DEBUG 8142 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2bc378f7]

```

#### b() --- requires_new
a()提交，b()回滚，a()以非事务的方式执行，b()开启一个新的事务

```
2019-04-09 19:19:42.817  INFO 8154 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:19:42.821 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:19:42.826 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:19:42.838 DEBUG 8154 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:19:42.839  INFO 8154 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:19:42.843  WARN 8154 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:19:43.063  INFO 8154 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:19:43.070 DEBUG 8154 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@543028700 wrapping com.mysql.cj.jdbc.ConnectionImpl@6aa7b67f] will be managed by Spring
2019-04-09 19:19:43.078 DEBUG 8154 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:19:43.106 DEBUG 8154 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:19:43.109 DEBUG 8154 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:19:43.110 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:19:43.110 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:19:43.111 DEBUG 8154 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_REQUIRES_NEW,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:19:43.114 DEBUG 8154 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad] for JDBC transaction
2019-04-09 19:19:43.115 DEBUG 8154 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad] to manual commit
2019-04-09 19:19:43.120  INFO 8154 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:19:43.120 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:19:43.120 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:19:43.120 DEBUG 8154 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad] will be managed by Spring
2019-04-09 19:19:43.120 DEBUG 8154 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:19:43.120 DEBUG 8154 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:19:43.121 DEBUG 8154 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:19:43.121 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:19:43.122 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:19:43.122 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:19:43.122 DEBUG 8154 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 19:19:43.122 DEBUG 8154 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad]
2019-04-09 19:19:43.123 DEBUG 8154 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad] after transaction
2019-04-09 19:19:43.124 DEBUG 8154 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 19:19:43.125 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:19:43.130 ERROR 8154 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1637000661.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$31256d65.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1637000661.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$bda9a9e8.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:19:43.131  INFO 8154 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:19:43.131 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:19:43.131 DEBUG 8154 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]

```

#### b() --- not_supported
a()，b()都提交，a()，b()都以非事务的方式执行

```
2019-04-09 19:21:39.064  INFO 8175 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:21:39.069 DEBUG 8175 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:21:39.074 DEBUG 8175 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:21:39.082 DEBUG 8175 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:21:39.082  INFO 8175 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:21:39.089  WARN 8175 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:21:39.300  INFO 8175 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:21:39.308 DEBUG 8175 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@543028700 wrapping com.mysql.cj.jdbc.ConnectionImpl@6aa7b67f] will be managed by Spring
2019-04-09 19:21:39.315 DEBUG 8175 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:21:39.346 DEBUG 8175 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:21:39.350 DEBUG 8175 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:21:39.351 DEBUG 8175 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:21:39.356  INFO 8175 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:21:39.357 DEBUG 8175 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f] from current transaction
2019-04-09 19:21:39.357 DEBUG 8175 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:21:39.357 DEBUG 8175 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:21:39.358 DEBUG 8175 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:21:39.358 DEBUG 8175 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:21:39.359 DEBUG 8175 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 19:21:39.367 ERROR 8175 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1637000661.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1637000661.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:21:39.367  INFO 8175 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:21:39.368 DEBUG 8175 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:21:39.368 DEBUG 8175 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]

```

#### b() --- never
a()，b()都提交，a()，b()都以非事务的方式执行

```
2019-04-09 19:22:59.718  INFO 8187 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:22:59.725 DEBUG 8187 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:22:59.730 DEBUG 8187 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 19:22:59.744 DEBUG 8187 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:22:59.745  INFO 8187 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:22:59.749  WARN 8187 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:22:59.963  INFO 8187 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:22:59.968 DEBUG 8187 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@814300680 wrapping com.mysql.cj.jdbc.ConnectionImpl@2e86807a] will be managed by Spring
2019-04-09 19:22:59.973 DEBUG 8187 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:23:00.003 DEBUG 8187 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:23:00.008 DEBUG 8187 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:23:00.008 DEBUG 8187 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 19:23:00.015  INFO 8187 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:23:00.016 DEBUG 8187 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709] from current transaction
2019-04-09 19:23:00.016 DEBUG 8187 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:23:00.016 DEBUG 8187 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:23:00.017 DEBUG 8187 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:23:00.017 DEBUG 8187 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 19:23:00.018 DEBUG 8187 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 19:23:00.024 ERROR 8187 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1728465884.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1728465884.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:23:00.025  INFO 8187 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:23:00.025 DEBUG 8187 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]
2019-04-09 19:23:00.026 DEBUG 8187 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@a50d709]

```

#### b() --- nested
a()提交，b()回滚，a()以非事务的方式执行，b()开启一个新的事务

```
2019-04-09 19:23:49.141  INFO 8197 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:23:49.146 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:23:49.151 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:23:49.160 DEBUG 8197 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:23:49.161  INFO 8197 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:23:49.166  WARN 8197 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:23:49.376  INFO 8197 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:23:49.382 DEBUG 8197 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@543028700 wrapping com.mysql.cj.jdbc.ConnectionImpl@6aa7b67f] will be managed by Spring
2019-04-09 19:23:49.391 DEBUG 8197 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:23:49.430 DEBUG 8197 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:23:49.434 DEBUG 8197 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:23:49.435 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:23:49.435 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:23:49.436 DEBUG 8197 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:23:49.440 DEBUG 8197 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad] for JDBC transaction
2019-04-09 19:23:49.440 DEBUG 8197 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad] to manual commit
2019-04-09 19:23:49.446  INFO 8197 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:23:49.446 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:23:49.447 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:23:49.447 DEBUG 8197 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad] will be managed by Spring
2019-04-09 19:23:49.447 DEBUG 8197 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:23:49.447 DEBUG 8197 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:23:49.449 DEBUG 8197 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:23:49.449 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:23:49.451 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:23:49.451 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:23:49.451 DEBUG 8197 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 19:23:49.451 DEBUG 8197 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad]
2019-04-09 19:23:49.452 DEBUG 8197 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1465663131 wrapping com.mysql.cj.jdbc.ConnectionImpl@74f827ad] after transaction
2019-04-09 19:23:49.454 DEBUG 8197 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 19:23:49.454 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:23:49.461 ERROR 8197 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1637000661.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1637000661.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:23:49.462  INFO 8197 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:23:49.462 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]
2019-04-09 19:23:49.462 DEBUG 8197 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5e0ec41f]

```

### a() --- nested

#### b() --- required
a()，b()都回滚，a()开启一个新的事务，b()加入当前事务，一起回滚

```
2019-04-09 19:26:05.188 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:26:05.189  INFO 8217 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:26:05.192  WARN 8217 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:26:05.405  INFO 8217 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:26:05.408 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] for JDBC transaction
2019-04-09 19:26:05.411 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] to manual commit
2019-04-09 19:26:05.421  INFO 8217 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:26:05.426 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:26:05.432 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.438 DEBUG 8217 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] will be managed by Spring
2019-04-09 19:26:05.443 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:26:05.469 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:26:05.471 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:26:05.471 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.471 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Participating in existing transaction
2019-04-09 19:26:05.476  INFO 8217 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:26:05.476 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc] from current transaction
2019-04-09 19:26:05.476 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:26:05.476 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:26:05.477 DEBUG 8217 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:26:05.477 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.477 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Participating transaction failed - marking existing transaction as rollback-only
2019-04-09 19:26:05.478 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Setting JDBC transaction [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] rollback-only
2019-04-09 19:26:05.483 ERROR 8217 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1361409513.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$9fdb5d36.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1361409513.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$2c5f99b9.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:26:05.483  INFO 8217 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:26:05.484 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Global transaction is marked as rollback-only but transactional code requested commit
2019-04-09 19:26:05.484 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.484 DEBUG 8217 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2c413ffc]
2019-04-09 19:26:05.485 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 19:26:05.485 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e]
2019-04-09 19:26:05.487 DEBUG 8217 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1452445207 wrapping com.mysql.cj.jdbc.ConnectionImpl@dd2856e] after transaction

org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only

	at org.springframework.transaction.support.AbstractPlatformTransactionManager.processRollback(AbstractPlatformTransactionManager.java:873)
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.commit(AbstractPlatformTransactionManager.java:710)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.commitTransactionAfterReturning(TransactionAspectSupport.java:533)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:304)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688)
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$2c5f99b9.a(<generated>)
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74)
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51)
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)

```

#### b() --- supports
a()，b()都回滚，a()开启一个新的事务，b()加入当前事务，一起回滚

```
和上一个实验结果一样

```

#### b() --- mandatory
a()，b()都回滚，a()开启一个新的事务，b()加入当前事务，一起回滚

```
和上一个实验结果一样
```

#### b() --- requres_new
a()提交，b()回滚，a(),b()分别开启一个新的事务

```
2019-04-09 19:39:33.579 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:39:33.580  INFO 8353 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:39:33.584  WARN 8353 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:39:33.798  INFO 8353 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:39:33.800 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] for JDBC transaction
2019-04-09 19:39:33.803 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] to manual commit
2019-04-09 19:39:33.809  INFO 8353 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:39:33.813 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:39:33.817 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.825 DEBUG 8353 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] will be managed by Spring
2019-04-09 19:39:33.836 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:39:33.871 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:39:33.872 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:39:33.873 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.873 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Suspending current transaction, creating new transaction with name [com.tuyu.service.impl.BServiceImpl.b]
2019-04-09 19:39:33.873 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.877 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad] for JDBC transaction
2019-04-09 19:39:33.877 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad] to manual commit
2019-04-09 19:39:33.883  INFO 8353 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5cbd94b2]
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad] will be managed by Spring
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:39:33.883 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:39:33.884 DEBUG 8353 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:39:33.884 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5cbd94b2]
2019-04-09 19:39:33.885 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5cbd94b2]
2019-04-09 19:39:33.885 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@5cbd94b2]
2019-04-09 19:39:33.885 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction rollback
2019-04-09 19:39:33.885 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back JDBC transaction on Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad]
2019-04-09 19:39:33.886 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1592415783 wrapping com.mysql.cj.jdbc.ConnectionImpl@3330f3ad] after transaction
2019-04-09 19:39:33.888 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 19:39:33.888 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.893 ERROR 8353 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$501de9c7.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/117911771.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$dca2264a.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:39:33.894  INFO 8353 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:39:33.894 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.894 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.895 DEBUG 8353 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6d8796c1]
2019-04-09 19:39:33.895 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 19:39:33.895 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3]
2019-04-09 19:39:33.896 DEBUG 8353 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@185209393 wrapping com.mysql.cj.jdbc.ConnectionImpl@4833eff3] after transaction

```

#### b() --- not_supported
a()，b()都提交，a()开启一个新的事务，b()以非事务的方式执行

```
2019-04-09 19:41:46.849 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:41:46.850  INFO 8373 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:41:46.854  WARN 8373 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:41:47.077  INFO 8373 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:41:47.083 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] for JDBC transaction
2019-04-09 19:41:47.086 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] to manual commit
2019-04-09 19:41:47.095  INFO 8373 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:41:47.100 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:41:47.104 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
2019-04-09 19:41:47.111 DEBUG 8373 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] will be managed by Spring
2019-04-09 19:41:47.116 DEBUG 8373 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:41:47.141 DEBUG 8373 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:41:47.143 DEBUG 8373 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:41:47.143 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
2019-04-09 19:41:47.143 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Suspending current transaction
2019-04-09 19:41:47.144 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization suspending SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
2019-04-09 19:41:47.150  INFO 8373 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:41:47.150 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:41:47.150 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:41:47.150 DEBUG 8373 --- [           main] o.s.jdbc.datasource.DataSourceUtils      : Fetching JDBC Connection from DataSource
2019-04-09 19:41:47.154 DEBUG 8373 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@937860163 wrapping com.mysql.cj.jdbc.ConnectionImpl@5c7dfc05] will be managed by Spring
2019-04-09 19:41:47.154 DEBUG 8373 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:41:47.154 DEBUG 8373 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:41:47.156 DEBUG 8373 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:41:47.156 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:41:47.157 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:41:47.158 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@7c1447b5]
2019-04-09 19:41:47.159 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Should roll back transaction but cannot - no transaction available
2019-04-09 19:41:47.159 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Resuming suspended transaction after completion of inner transaction
2019-04-09 19:41:47.159 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization resuming SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
2019-04-09 19:41:47.164 ERROR 8373 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1112400678.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$e1492fb9.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1112400678.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$6dcd6c3c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:41:47.165  INFO 8373 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:41:47.165 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
2019-04-09 19:41:47.166 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
2019-04-09 19:41:47.166 DEBUG 8373 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@203d1d93]
2019-04-09 19:41:47.166 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 19:41:47.166 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e]
2019-04-09 19:41:47.167 DEBUG 8373 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@238612663 wrapping com.mysql.cj.jdbc.ConnectionImpl@1192b58e] after transaction

```

#### b() --- never
a()提交，b()抛异常，a()开启一个新的事务

```
2019-04-09 19:45:04.565 DEBUG 8405 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:45:04.566  INFO 8405 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:45:04.572  WARN 8405 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:45:04.786  INFO 8405 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:45:04.791 DEBUG 8405 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05] for JDBC transaction
2019-04-09 19:45:04.796 DEBUG 8405 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05] to manual commit
2019-04-09 19:45:04.808  INFO 8405 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:45:04.812 DEBUG 8405 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:45:04.816 DEBUG 8405 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 19:45:04.822 DEBUG 8405 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05] will be managed by Spring
2019-04-09 19:45:04.827 DEBUG 8405 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:45:04.856 DEBUG 8405 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:45:04.857 DEBUG 8405 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:45:04.858 DEBUG 8405 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 19:45:04.863 ERROR 8405 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

org.springframework.transaction.IllegalTransactionStateException: Existing transaction found for transaction marked with propagation 'never'
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.handleExistingTransaction(AbstractPlatformTransactionManager.java:406) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.getTransaction(AbstractPlatformTransactionManager.java:354) ~[spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary(TransactionAspectSupport.java:474) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:289) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$90f91b99.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/356539350.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$1d7d581c.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:45:04.864  INFO 8405 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:45:04.864 DEBUG 8405 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 19:45:04.865 DEBUG 8405 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 19:45:04.865 DEBUG 8405 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@3382cf68]
2019-04-09 19:45:04.865 DEBUG 8405 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 19:45:04.865 DEBUG 8405 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05]
2019-04-09 19:45:04.868 DEBUG 8405 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@964094970 wrapping com.mysql.cj.jdbc.ConnectionImpl@2676dc05] after transaction

```

#### b() --- nested
a()提交，b()回滚到保存点，a()开启一个新的事务，b()在当前事务的基础上以嵌套事务的方式执行

```
2019-04-09 19:47:37.187 DEBUG 8432 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating new transaction with name [com.tuyu.service.impl.AServiceImpl.a]: PROPAGATION_NESTED,ISOLATION_DEFAULT,-java.lang.Exception
2019-04-09 19:47:37.188  INFO 8432 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-04-09 19:47:37.194  WARN 8432 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-04-09 19:47:37.406  INFO 8432 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-04-09 19:47:37.408 DEBUG 8432 --- [           main] o.s.j.d.DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@2034046523 wrapping com.mysql.cj.jdbc.ConnectionImpl@60dd0587] for JDBC transaction
2019-04-09 19:47:37.410 DEBUG 8432 --- [           main] o.s.j.d.DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@2034046523 wrapping com.mysql.cj.jdbc.ConnectionImpl@60dd0587] to manual commit
2019-04-09 19:47:37.416  INFO 8432 --- [           main] com.tuyu.service.impl.AServiceImpl       : start a
2019-04-09 19:47:37.421 DEBUG 8432 --- [           main] org.mybatis.spring.SqlSessionUtils       : Creating a new SqlSession
2019-04-09 19:47:37.425 DEBUG 8432 --- [           main] org.mybatis.spring.SqlSessionUtils       : Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e95b653]
2019-04-09 19:47:37.430 DEBUG 8432 --- [           main] o.m.s.t.SpringManagedTransaction         : JDBC Connection [HikariProxyConnection@2034046523 wrapping com.mysql.cj.jdbc.ConnectionImpl@60dd0587] will be managed by Spring
2019-04-09 19:47:37.435 DEBUG 8432 --- [           main] com.tuyu.dao.TestMapper.a                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 1 
2019-04-09 19:47:37.468 DEBUG 8432 --- [           main] com.tuyu.dao.TestMapper.a                : ==> Parameters: 
2019-04-09 19:47:37.470 DEBUG 8432 --- [           main] com.tuyu.dao.TestMapper.a                : <==    Updates: 1
2019-04-09 19:47:37.471 DEBUG 8432 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e95b653]
2019-04-09 19:47:37.471 DEBUG 8432 --- [           main] o.s.j.d.DataSourceTransactionManager     : Creating nested transaction with name [com.tuyu.service.impl.BServiceImpl.b]
2019-04-09 19:47:37.479  INFO 8432 --- [           main] com.tuyu.service.impl.BServiceImpl       : start b
2019-04-09 19:47:37.479 DEBUG 8432 --- [           main] org.mybatis.spring.SqlSessionUtils       : Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e95b653] from current transaction
2019-04-09 19:47:37.480 DEBUG 8432 --- [           main] com.tuyu.dao.TestMapper.b                : ==>  Preparing: update `name` set `name` = concat(`name`, 'new') where id = 2 
2019-04-09 19:47:37.480 DEBUG 8432 --- [           main] com.tuyu.dao.TestMapper.b                : ==> Parameters: 
2019-04-09 19:47:37.480 DEBUG 8432 --- [           main] com.tuyu.dao.TestMapper.b                : <==    Updates: 1
2019-04-09 19:47:37.480 DEBUG 8432 --- [           main] org.mybatis.spring.SqlSessionUtils       : Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e95b653]
2019-04-09 19:47:37.481 DEBUG 8432 --- [           main] o.s.j.d.DataSourceTransactionManager     : Rolling back transaction to savepoint
2019-04-09 19:47:37.486 ERROR 8432 --- [           main] com.tuyu.service.impl.AServiceImpl       : 捕获了b的异常,但是不抛出，只是打印出来

java.lang.ArithmeticException: / by zero
	at com.tuyu.service.impl.BServiceImpl.b(BServiceImpl.java:39) ~[classes/:na]
	at com.tuyu.service.impl.BServiceImpl$$FastClassBySpringCGLIB$$cc897a4.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1979825302.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.BServiceImpl$$EnhancerBySpringCGLIB$$aebaecc7.b(<generated>) ~[classes/:na]
	at com.tuyu.service.impl.AServiceImpl.callBWithTryCatch(AServiceImpl.java:49) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl.a(AServiceImpl.java:42) [classes/:na]
	at com.tuyu.service.impl.AServiceImpl$$FastClassBySpringCGLIB$$516f245.invoke(<generated>) [classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) [spring-core-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:749) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor$$Lambda$230/1979825302.proceedWithInvocation(Unknown Source) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:294) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:98) [spring-tx-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688) [spring-aop-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at com.tuyu.service.impl.AServiceImpl$$EnhancerBySpringCGLIB$$3b3f294a.a(<generated>) [classes/:na]
	at com.tuyu.LearnTxPropagationApplicationTests.contextLoads(LearnTxPropagationApplicationTests.java:20) [test-classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_40]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_40]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_40]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[na:1.8.0_40]
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12) [junit-4.12.jar:4.12]
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47) [junit-4.12.jar:4.12]
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestExecutionCallbacks.evaluate(RunBeforeTestExecutionCallbacks.java:74) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestExecutionCallbacks.evaluate(RunAfterTestExecutionCallbacks.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:75) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:86) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:84) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:251) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:97) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58) [junit-4.12.jar:4.12]
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:70) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363) [junit-4.12.jar:4.12]
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:190) [spring-test-5.1.6.RELEASE.jar:5.1.6.RELEASE]
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137) [junit-4.12.jar:4.12]
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:51) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242) [junit-rt.jar:na]
	at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70) [junit-rt.jar:na]

2019-04-09 19:47:37.487  INFO 8432 --- [           main] com.tuyu.service.impl.AServiceImpl       : end a
2019-04-09 19:47:37.488 DEBUG 8432 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e95b653]
2019-04-09 19:47:37.488 DEBUG 8432 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e95b653]
2019-04-09 19:47:37.488 DEBUG 8432 --- [           main] org.mybatis.spring.SqlSessionUtils       : Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1e95b653]
2019-04-09 19:47:37.488 DEBUG 8432 --- [           main] o.s.j.d.DataSourceTransactionManager     : Initiating transaction commit
2019-04-09 19:47:37.488 DEBUG 8432 --- [           main] o.s.j.d.DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@2034046523 wrapping com.mysql.cj.jdbc.ConnectionImpl@60dd0587]
2019-04-09 19:47:37.491 DEBUG 8432 --- [           main] o.s.j.d.DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@2034046523 wrapping com.mysql.cj.jdbc.ConnectionImpl@60dd0587] after transaction

```