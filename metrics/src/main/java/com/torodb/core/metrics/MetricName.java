package com.torodb.core.metrics;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;


import com.codahale.metrics.MetricRegistry;

public class MetricName implements Comparable<MetricName>
{
    private String group;
    private String category;
    private String name;
    private String mBeanName;

	public String getGroup() {
		return group;
	}

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public MetricName(Class<?> clasz, String name) {
		String group = "";
		if (clasz.getPackage() != null){
			group = clasz.getPackage().getName();
		}
		String type = removeTailDollar(clasz.getSimpleName());
		build(group , type, name);
	}

	public MetricName(String group, String type, String name) {
		build(group, type, name);
	}

	public MetricName(String group, String category, String name, String mBeanName) {
		build(group, category, name, mBeanName);
	}
	
	private void build(String group, String category, String name) {
		String mBeanName = createMBeanName(group, category, name);
		build(group, category, name, mBeanName);
	}
	
	private void build(String group, String category, String name, String mBeanName) {
		if (group == null || category == null) {
			throw new IllegalArgumentException("Both group and category must be specified");
		}
		if (name == null) {
			throw new IllegalArgumentException("Name must be specified");
		}
		this.group = group;
		this.category = category;
		this.name = name;
		this.mBeanName = mBeanName;
	}

	public String getMetricName() {
		return MetricRegistry.name(group, category, name);
	}

	public ObjectName getMBeanName() {
		String mname = mBeanName;
		if (mname == null){
			mname = getMetricName();
		}
		try {
			return new ObjectName(mname);
		} catch (MalformedObjectNameException e) {
			try {
				return new ObjectName(ObjectName.quote(mname));
			} catch (MalformedObjectNameException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MetricName other = (MetricName) o;
		return mBeanName.equals(other.mBeanName);
	}

	@Override
	public int hashCode() {
		return mBeanName.hashCode();
	}

	@Override
	public String toString() {
		return mBeanName;
	}

	@Override
	public int compareTo(MetricName o) {
		return mBeanName.compareTo(o.mBeanName);
	}

	private String createMBeanName(String group, String type, String name) {
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(group);
		nameBuilder.append(":type=");
		nameBuilder.append(type);
		if (!name.isEmpty()) {
			nameBuilder.append(",name=");
			nameBuilder.append(name);
		}
		return nameBuilder.toString();
	}

	private static String removeTailDollar(String s) {
		if (s.endsWith("$")) {
			return s.substring(0, s.length() - 1);
		}
		return s;
	}
}
