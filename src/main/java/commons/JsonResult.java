package commons;

import java.util.List;

public class JsonResult<E> {
    /** 响应状态 */
    private Integer code = 1;
    /** 响应消息 */
    private String message = "success!";
    /** 响应内容 */
    private List<E> list;
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<E> getList() {
		return list;
	}
	public void setList(List<E> list) {
		this.list = list;
	}

    
}
