package com.hanj.blog.web;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hanj.blog.po.Blog;
import com.hanj.blog.po.Tag;
import com.hanj.blog.service.BlogService;
import com.hanj.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by limi on 2017/10/23.
 */
@Controller
public class TagShowController {

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/tags/{id}")
    public String tags(@PathVariable Long id, Model model,@RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum) {
        List<Tag> tags = tagService.findAll();
        if (id == -1) {
           id = tags.get(0).getId();
        }

        List<Blog> blogs = blogService.findByContainsTagId(id);
        PageHelper.startPage(pageNum,6);
//        PageHelper.orderBy("id desc");

        PageInfo<Blog> listBlogs = new PageInfo<Blog>(blogs);
        model.addAttribute("tags", tags);
        model.addAttribute("pageInfo", listBlogs);
        model.addAttribute("activeTagId", id);
        return "tags";
    }
}
