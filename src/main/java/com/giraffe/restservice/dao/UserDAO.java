package com.giraffe.restservice.dao;

import com.giraffe.restservice.pojo.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {
    User findUserByPhone(String phone);
    boolean existsUserByPhone(String phone);
}
