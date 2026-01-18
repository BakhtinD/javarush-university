package com.javarush;

import com.javarush.entity.Employee;
import com.javarush.entity.EmployeeTask;
import com.javarush.entity.Product;
import com.javarush.entity.User;
import com.javarush.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        // shutdown
        HibernateUtil.shutdown();
    }

}