/**
 * 
 */
package chat01;


public class Message {
	
	private String content;
	private String senderName;
	private String receiverName;
	
	public Message() {
		super();
	}
	
	

	public Message(String content) {
		super();
		this.content = content;
	}



	public Message(String content, String senderName, String receiverName) {
		super();
		this.content = content;
		this.senderName = senderName;
		this.receiverName = receiverName;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the senderName
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * @param senderName the senderName to set
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * @return the receiverName
	 */
	public String getReceiverName() {
		return receiverName;
	}

	/**
	 * @param receiverName the receiverName to set
	 */
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	
	
	
}
