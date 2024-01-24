package model;

import java.sql.Date;

public class Task {
	private int id;
	private String description;
	private boolean status;
	private Date creationDate;
	private Date dueDate;

	public Task() {
		// Default constructor
	}

	public Task(int id, String description, boolean status, Date creationDate, Date dueDate) {
		this.id = id;
		this.description = description;
		this.status = status;
		this.creationDate = creationDate;
		this.dueDate = dueDate;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}
