package com.hanj.blog.dao;

import com.hanj.blog.po.Type;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface TypeRepository extends JpaRepository<Type,Long> {

    Type findByName(String name);

    @Query("select t from Type t")
    List<Type> findTypeByPageable(Pageable pageable);
}
