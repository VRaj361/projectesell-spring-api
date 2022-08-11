package com.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bean.FileDB;

public interface FileDBRepository  extends JpaRepository<FileDB, String>{

}
