package com.stajodev.Repository;

import com.stajodev.Models.User;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public List<User> findAllByDepartment(String department);
    public Boolean deleteByEmail(String email);
    public Optional<User> findByEmail(String email);
}
