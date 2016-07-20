package de.master.server.restPojos;

public class UpdateTransportStatusRest {

	private final Integer status;
	private final Integer id;
	
	public UpdateTransportStatusRest(Integer id, Integer status) {
		this.id = id;
		this.status = status;
	}

	private static Status parseStatus(Integer status) {
		switch (status) {
		case 0:
			return Status.DONE;
		case 1:
			return Status.TO_SOURCE;
		case 2:
			return Status.TO_TARGET;
		case 3:
			return Status.WAIT;
		case 4:
			return Status.FINISH;
		default:
			throw new RuntimeException("Status: " + status + " is not a valid transport status");
		}
	}

	public Status getStatus() {
		return parseStatus(status);
	}

	public Integer getId() {
		return id;
	}

	public enum Status {
		DONE, TO_SOURCE, TO_TARGET, WAIT, FINISH;
	}
}
