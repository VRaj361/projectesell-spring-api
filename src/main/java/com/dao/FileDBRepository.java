package com.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bean.FileDB;

public interface FileDBRepository  extends JpaRepository<FileDB, String>{

}
