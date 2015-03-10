package msg.madfox.first;

public enum DaemonActions {
	ADD("msg.madfox.first.ADD_ALARM"),
	DELETE("msg.madfox.first.DELETE_ALARM"),
	DELETE_ALL("msg.madfox.first.DELETE_ALL_ALARM"),
	UPDATE("msg.madfox.first.UPDATE_ALARM");
	
	private String action;
	
	private DaemonActions(String i) {
		this.action=i;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
}
