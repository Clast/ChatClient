package client_code;

public class InternalMessage 
{
	String 	action 		= 	null;
	String 	data		=	null;
	boolean	isInternal	= 	true;
	String 	sessionID	=	null;
	String	client		=	null;
	
	
	public InternalMessage(String action, String data, String sessionID, String client, boolean isInternal)
	{
		
		this.action 		= 	action;
		this.data			=	data;
		this.sessionID		=	sessionID;
		this.client 		=	client;
		this.isInternal		=	isInternal;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public boolean isInternal() {
		return isInternal;
	}


	public void setInternal(boolean isInternal) {
		this.isInternal = isInternal;
	}


	public String getSessionID() {
		return sessionID;
	}


	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}


	public String getClient() {
		return client;
	}


	public void setClient(String client) {
		this.client = client;
	}
	
}
