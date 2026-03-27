package com.n2.gameshop.model;

public class User {
	private int id;
	private String username;
	private String email;
	private String password;
	private String fullName;
	private String phone;
	private String role;
	private boolean isActive;

	public User() {
		this.role = "user";
		this.isActive = true;
	}

	public User(int id, String username, String email, String password, String fullName,
				String phone, String role, boolean isActive) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.phone = phone;
		this.role = role;
		this.isActive = isActive;
	}

	public User(String username, String email, String password, String fullName,
				String phone, String role, boolean isActive) {
		this(0, username, email, password, fullName, phone, role, isActive);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}

