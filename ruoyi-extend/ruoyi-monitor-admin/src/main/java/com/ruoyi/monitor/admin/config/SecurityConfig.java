package com.ruoyi.monitor.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * admin 监控 安全配置
 *
 * @author LBZ
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String adminContextPath;

    public SecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");
        // admin监控 用户鉴权
        httpSecurity.authorizeRequests()
            //授予对所有静态资产和登录页面的公共访问权限。
            .antMatchers(adminContextPath + "/assets/**").permitAll()
            .antMatchers(adminContextPath + "/login").permitAll()
            .antMatchers("/actuator").permitAll()
            .antMatchers("/actuator/**").permitAll()
            //必须对每个其他请求进行身份验证
            .anyRequest().authenticated().and()
            //配置登录和注销
            .formLogin().loginPage(adminContextPath + "/login")
            .successHandler(successHandler).and()
            .logout().logoutUrl(adminContextPath + "/logout").and()
            //启用HTTP-Basic支持。这是Spring Boot Admin Client注册所必需的
            .httpBasic().and().csrf().disable()
            .headers().frameOptions().disable();
    }

}
