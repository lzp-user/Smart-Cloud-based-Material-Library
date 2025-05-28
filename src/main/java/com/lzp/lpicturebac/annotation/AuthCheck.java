package com.lzp.lpicturebac.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//使用springAOP切面+自定义注解 实现权限校验
@Target(ElementType.METHOD)  //方法才可以使用
@Retention(RetentionPolicy.RUNTIME)  //运行时生效
//使用此注解时,用户必须要登陆,然后校验权限
public @interface AuthCheck {

    /**
     * 必须具有某个角色
     **/
    String mustRole() default "";
}
