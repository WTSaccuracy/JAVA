package ChatRoom;

import java.text.SimpleDateFormat;
import java.util.*;

public class Message {
	private String id,msg;
	Date date;
	static SimpleDateFormat sdf = new SimpleDateFormat ("[yyyyMMdd HH:mm:ss]");
	public Message(String id,String msg) {
		this.id=id;
		this.msg=msg;
		date=new Date();
	}
	public String getId() {
		return id;
	}
	public String getMessage() {
		return msg;
	}
	public Date getDate() {
		return date;
	}
	public String getStr() {
		return sdf.format(date)+" "+id+" : "+msg;
	}
}
