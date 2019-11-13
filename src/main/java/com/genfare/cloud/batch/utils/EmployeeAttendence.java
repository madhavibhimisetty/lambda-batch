package com.genfare.cloud.batch.utils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "employeeattendance")
public class EmployeeAttendence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer employeecode;

	private String date;

	private String intime;

	private String outtime;

	private String duration;

	public EmployeeAttendence(Integer id, Integer employeecode, String date, String intime, String outtime,
			String duration) {
		super();
		this.id = id;
		this.employeecode = employeecode;
		this.date = date;
		this.intime = intime;
		this.outtime = outtime;
		this.duration = duration;
	}

	public String getDuration() {
		return duration;
	}

	public Integer getEmployeecode() {
		return employeecode;
	}

	public void setEmployeecode(Integer employeecode) {
		this.employeecode = employeecode;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public EmployeeAttendence() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String strDate) {
		this.date = strDate;
	}

	public String getIntime() {
		return intime;
	}

	public void setIntime(String intime) {
		this.intime = intime;
	}

	public String getOuttime() {
		return outtime;
	}

	public void setOuttime(String outtime) {
		this.outtime = outtime;
	}

}
