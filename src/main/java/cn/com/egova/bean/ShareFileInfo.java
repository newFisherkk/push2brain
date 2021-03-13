package cn.com.egova.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;

@Configuration
@PropertySource("classpath:config.properties")
public class ShareFileInfo {

	@Value("${ftp-server-addr}")
	private String addr;
	@Value("${ftp-server-port}")
	private String port;
	@Value("${ftp-server-user}")
	private String user;
	@Value("${ftp-server-password}")
	private String password;

	private String encodeAuthString; // 用于基本验证的base64编码字符

	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncodeAuthString(){
		if(user != null && password != null && encodeAuthString == null) {
			try {
				encodeAuthString = Base64Utils.encodeToString((user + ":" + password).getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return encodeAuthString == null ? "" : encodeAuthString;
	}

	@Override
	public String toString() {
		return "ShareFileInfo{" +
				"addr='" + addr + '\'' +
				", port='" + port + '\'' +
				'}';
	}
}
