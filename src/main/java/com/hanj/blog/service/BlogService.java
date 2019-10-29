package com.hanj.blog.service;


import com.hanj.blog.po.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;


public interface BlogService {

    public Page<Blog> findAllBlogs(Pageable pageable);

    Blog saveBlog(Blog blog);

    void deleteBlog(Long id);

    Blog updateBlog(Blog blog,Long id);

    Blog findByName(String title);

    Page<Blog> findByConditions(Pageable pageable, Map<String,String> blogContidions);

    Blog findBlogById(Long id);

    List<Blog> findRecommendBlogs(Integer size);

    Page<Blog> findBySearch(Pageable pageable,String query);

    Blog markdownToHtml(Long id);

    List<Blog> findByContainsTagId(Long id);

    Map<String,List<Blog>> archiveBlog();

    String countBlog();
}
