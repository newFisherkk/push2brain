package cn.com.egova.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:jdbc.properties")
public class JdbcConfig {
	@Value("${biz.jdbc.url}")
	private String bizUrl;
	@Value("${stat.jdbc.url}")
	private String statUrl;
	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${biz.jdbc.username}")
	private String bizUserName;
	@Value("${biz.jdbc.password}")
	private String bizPassWord;
	@Value("${stat.jdbc.username}")
	private String statUserName;
	@Value("${stat.jdbc.password}")
	private String statPassWord;
	
	@Bean("bizDataSource")
	public DataSource bizDataSource(){
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(bizUrl);
		druidDataSource.setDriverClassName(driverClassName);
		druidDataSource.setUsername(bizUserName);
		druidDataSource.setPassword(bizPassWord);
		return druidDataSource;
	}

	@Bean("statDataSource")
	public DataSource statDataSource(){
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(statUrl);
		druidDataSource.setDriverClassName(driverClassName);
		druidDataSource.setUsername(statUserName);
		druidDataSource.setPassword(statPassWord);
		return druidDataSource;
	}

	@Bean("bizJdbcTemplate")
	public JdbcTemplate bizJdbcTemplate(){
		return new JdbcTemplate(this.bizDataSource());
	}

	@Bean("statJdbcTemplate")
	public JdbcTemplate statJdbcTemplate(){
		return new JdbcTemplate(this.statDataSource());
	}
	
	@Bean("bizTransactionManager")
	public PlatformTransactionManager bizTransactionManager(){
		return new DataSourceTransactionManager(this.bizDataSource());
	}
}
