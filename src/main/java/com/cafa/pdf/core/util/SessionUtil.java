package com.cafa.pdf.core.util;


import com.cafa.pdf.core.dal.entity.Customer;
import com.cafa.pdf.core.dal.entity.User;

public class SessionUtil {
	
	private static String SESSION_USER = "sessionUser";
	private static String SESSION_CUSTOMER = "sessionCustomer";

	public static Object get(String attrName) {
		return RequestHolder.getSession().getAttribute(attrName);
	}

	public static void add(String attrName, Object val) {
		RequestHolder.getSession().setAttribute(attrName, val);
	}

	public static void del(String attrName) {
		RequestHolder.getSession().setAttribute(attrName, null);
	}

    public static User getUser() {
        return (User) get(SESSION_USER);
    }

    public static void addUser(User user) {
        add(SESSION_USER, user);
    }

    public static void delUser() {
        del(SESSION_USER);
    }


	public static Customer getCustomer() {
		return (Customer) get(SESSION_CUSTOMER);
	}

	public static void addCustomer(Customer user) {
		add(SESSION_CUSTOMER, user);
	}

    public static void delCustomer() {
        del(SESSION_CUSTOMER);
    }

}
