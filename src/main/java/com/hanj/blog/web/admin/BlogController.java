package com.hanj.blog.web.admin;


import com.hanj.blog.po.Blog;
import com.hanj.blog.po.Tag;
import com.hanj.blog.po.Type;
import com.hanj.blog.po.User;
import com.hanj.blog.service.BlogService;
import com.hanj.blog.service.TagService;
import com.hanj.blog.service.TypeService;
import org.hibernate.annotations.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @RequestMapping("/blogs")
    public String blogs(@PageableDefault(size = 6,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable
            ,Model model){
        model.addAttribute("page",blogService.findAllBlogs(pageable));
        model.addAttribute("types",typeService.findAll());
        return "admin/blogs";
    }

    //通过一定的条件查询，然后动态返回一片前端代码块
    @RequestMapping("/findBlogsByConditions")
    public String findBlogsByConditions(Pageable pageable,Model model,@RequestBody Map<String,String> queryMap){

        //得到当前页数
        int page = Integer.parseInt(queryMap.get("page"));
        //得到每页的条数
        int size = 6;
        //排序是倒序还是正序，排序字段
        Sort sort = new Sort(Sort.Direction.DESC, "updateTime");

        pageable = PageRequest.of(page,size,sort);
        Page<Blog> blogList = blogService.findByConditions(pageable, queryMap);
        model.addAttribute("page",blogList);
        if(blogList.getTotalElements()!=0){
            model.addAttribute("message","查询成功了哈哈");
            return "admin/blogs::blogList";
//            model.addAttribute("types",typeService.findAll());
//            return "admin/blogs";
        }else {
            model.addAttribute("message","查询不到指定条件的数据，灰常抱歉");
            return "admin/blogs::blogList";
        }


    }


    @RequestMapping("/goInputBlog")
    public String goInputBlog(Model model){
        model.addAttribute("blog",new Blog());
        model.addAttribute("types",typeService.findAll());
        model.addAttribute("tags",tagService.findAll());

        return "admin/blogs-input";
    }

    @RequestMapping("/saveBlog")
    public String saveBlog(Blog blog, RedirectAttributes attr, HttpSession httpSession){
        //这里额外对blog中的tag集合做个赋值，根据表单得到的tagIds字符串
        //这一步很重要，不然不能把选择的标签信息持久化到博客标签的中间表
        List<Tag> tags = tagService.listTag(blog.getTagIds());
        blog.setTags(tags);

        //user也需要持久化
        blog.setUser((User) httpSession.getAttribute("user"));

        Blog blog1 = blogService.findByName(blog.getTitle());
        if(blog1!=null){
            attr.addFlashAttribute("message","已经存在相同标题的博客了，麻烦换个不一样的标题");
            return "redirect:/admin/goInputBlog";
        }

        Blog b = blogService.saveBlog(blog);
        System.out.println("来看看保存后tags相关有没有值："+blog.getTags()+"+++"+blog.getTagIds());
        if(b==null){
            attr.addFlashAttribute("message","保存失败");
        }else {
            attr.addFlashAttribute("message","保存成功");
        }
        return "redirect:/admin/blogs";
    }


    //我决定采取局部刷新，就是下面那个删除的方法
    @RequestMapping("/deleteBlog/{id}")
    public String deleteBlog(@PathVariable Long id){
        blogService.deleteBlog(id);
        return "redirect:/admin/blogs";
    }

    //删除按钮ajax请求的方法，局部刷新表格
    @RequestMapping("/deleteBlogById")
    public String deleteBlogAndShow(Pageable pageable,Model model,@RequestBody Map<String,String> queryMap){
        Long id = Long.valueOf(0);
        if(queryMap!=null&&queryMap.containsKey("blogId")){
            id = Long.parseLong(queryMap.get("blogId"));
        }

        blogService.deleteBlog(id);
        //得到当前页数
//        int page = Integer.parseInt(queryMap.get("page"));
        //删除之后，回到首页，首页是page=0
        int page = 0;
        //得到每页的条数
        int size = 6;
        //排序是倒序还是正序，排序字段
        Sort sort = new Sort(Sort.Direction.DESC, "updateTime");

        pageable = PageRequest.of(page,size,sort);
        Page<Blog> blogList = blogService.findByConditions(pageable, queryMap);
        model.addAttribute("page",blogList);
        if(blogList.getTotalElements()!=0){
            model.addAttribute("message","删除成功");
            return "admin/blogs::blogList";
        }else {
            model.addAttribute("message","数据已经全部删除完了");
            return "admin/blogs::blogList";
        }
    }

    //在到更新页面之前，要原样显示要更新的博客内容，这里做处理
    @RequestMapping("/beforeUpdateBlog/{id}")
    public String goUpdateBlog(Model model,@PathVariable Long id){
            Blog targetBlog = blogService.findBlogById(id);

        System.out.println("tagIds:"+targetBlog.getTagIds());
        //这里需要把集合转标签字符串，标签字符串没有持久化到数据库，所以查出来是空
        targetBlog.initTagIdsByTags();
        System.out.println("tagIds2:"+targetBlog.getTagIds());

            model.addAttribute("blog",targetBlog);
            model.addAttribute("types",typeService.findAll());
            model.addAttribute("tags",tagService.findAll());
            return "admin/blogs-input";
    }

    //这里更新
    @RequestMapping("/updateBlog/{id}")
    public String updateBlog(Blog blog,@PathVariable Long id,RedirectAttributes attr){

        //这里额外对blog中的tag集合做个赋值，根据表单得到的tagIds字符串
        //这一步很重要，不然不能把选择的标签信息持久化到博客标签的中间表
        List<Tag> tags = tagService.listTag(blog.getTagIds());
        blog.setTags(tags);

        blogService.updateBlog(blog,id);

        attr.addFlashAttribute("message","更新博客："+blog.getTitle()+"成功");

        return "redirect:/admin/blogs";
    }

}
