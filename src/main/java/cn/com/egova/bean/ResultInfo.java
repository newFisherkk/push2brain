package cn.com.egova.bean;

/**
 * 结果集返回bean
 *
 */
public class ResultInfo {

	protected boolean success = false;
	private String code;
	protected String msg;
	protected String data;

	public ResultInfo(boolean success) {
		this.success = success;
	}

	/**
	 * json字符串转为对象必须要构造方法
	 * @param success
	 * @param code
	 * @param msg
	 * @param data
	 */
	public ResultInfo(boolean success, String code, String msg, String data) {
		this.success = success;
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}