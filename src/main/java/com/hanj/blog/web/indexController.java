package com.hanj.blog.web;

import com.hanj.blog.NotFoundException;
import com.hanj.blog.po.Blog;
import com.hanj.blog.po.Comment;
import com.hanj.blog.po.Tag;
import com.hanj.blog.po.Type;
import com.hanj.blog.service.BlogService;
import com.hanj.blog.service.CommentService;
import com.hanj.blog.service.TagService;
import com.hanj.blog.service.TypeService;
import com.hanj.blog.util.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Controller
public class indexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;


    @GetMapping("/")
    public String index(@PageableDefault(size = 6,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){

        Page<Blog> allBlogs = blogService.findAllBlogs(pageable);
        List<Type> allTypes = typeService.findTypeByPageable(6);
        List<Tag> allTags = tagService.findByPageable(10);

        List<Blog> recommendBlogs = blogService.findRecommendBlogs(6);

        HashMap<String,String> updateTimeFmtStr = new HashMap<>();
        for(Blog blog:allBlogs){
            updateTimeFmtStr.put(blog.getTitle(),TimeFormat.timeFormat(blog.getUpdateTime()));
        }

        model.addAttribute("timeMap",updateTimeFmtStr);

        model.addAttribute("page",allBlogs);
        model.addAttribute("recommendBlogs",recommendBlogs);
        model.addAttribute("types",allTypes);
        model.addAttribute("tags",allTags);
        return "index";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id,Model model){

//        Blog blogById = blogService.findBlogById(id);

        model.addAttribute("blog",blogService.markdownToHtml(id));

        List<Comment> comments = commentService.listCommentByBlogId(id);

        model.addAttribute("comments",comments);

        return "blog";
    }

    @RequestMapping("/search")
    public String searchAndShow(String query,Model model){
        Sort sort = new Sort(Sort.Direction.DESC,"update_time");
        Pageable pageable = PageRequest.of(0,6,sort);
        String str = query.trim();
        if("".equals(str)){
            System.out.println("列个就是的"+query.trim().toString());
            return "redirect:/";
        }

        Page<Blog> bySearch = blogService.findBySearch(pageable, query);

        model.addAttribute("page",bySearch);
        model.addAttribute("query",query);


        return "search";
    }
}
