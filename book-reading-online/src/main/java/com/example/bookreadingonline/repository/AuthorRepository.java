package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.Author;
import com.example.bookreadingonline.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface AuthorRepository extends JpaRepositoryImplementation<Author, Integer> {

}