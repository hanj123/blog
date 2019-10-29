package com.hanj.blog.service.serviceImpl;

import com.hanj.blog.NotFoundException;
import com.hanj.blog.dao.BlogRepository;
import com.hanj.blog.po.Blog;
import com.hanj.blog.po.Tag;
import com.hanj.blog.po.Type;
import com.hanj.blog.service.BlogService;
import com.hanj.blog.util.MarkdownUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service("blogService")
@Transactional
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Page<Blog> findAllBlogs(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Blog saveBlog(Blog blog) {

        Date createTime = new Date();
        blog.setCreateTime(createTime);
        if (blog.getUpdateTime() == null) {
            blog.setUpdateTime(createTime);
        }
        blog.setViews(0);
        System.out.println(blog);
        return blogRepository.save(blog);
    }

    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Blog updateBlog(Blog blog, Long id) {
        Blog blog1 = blogRepository.findById(id).get();

        if (blog1 == null) {
            throw new NotFoundException("该对象不存在");
        } else {
            blog.setCreateTime(blog1.getCreateTime());
            blog.setUser(blog1.getUser());
            blog.setViews(blog1.getViews());
            blog.setComments(blog1.getComments());
            Date updateTime = new Date();
            blog.setUpdateTime(updateTime);

            BeanUtils.copyProperties(blog, blog1);

            System.out.println(blog1);
//            return blogRepository.save(blog1);
//            blogRepository.updateBlogSomeFileds(blog1.getFlag(),blog1.getTitle(),
//                    blog1.getContent(),blog1.getType().getId(),blog1.getFirstPicture(),
//                    blog1.getDescription(),booleanToInt(blog1.isRecommend()),booleanToInt(blog1.isShareStatement()),
//                    booleanToInt(blog1.isAppreciation()),
//                    booleanToInt(blog1.isCommentabled()), blog1.getId());
            blogRepository.updateBlogSomeFileds(blog1.getFlag(),blog1.getTitle(),
                    blog1.getContent(),blog1.getType().getId(),blog1.getFirstPicture(),
                    blog1.getDescription(),blog1.isRecommend(),blog1.isShareStatement(),
                    blog1.isAppreciation(),
                    blog1.isCommentabled(), blog1.getId());
            List<Tag> tags = blog1.getTags();
            List<Long> oldIds = blogRepository.findTagsId(blog1.getId());
            for(Tag tag:tags){
                if(!oldIds.contains(tag.getId())){
                    blogRepository.updateTags(blog1.getId(),tag.getId());
                }
            }

            return null;
        }
    }

    public int booleanToInt(boolean flag){
        int num = 0;
        num=flag==true ? 1:0;
        return num;
    }

    @Override
    public Blog findByName(String title) {
        return blogRepository.findByTitle(title);
    }

    @Override
    public Page<Blog> findByConditions(Pageable pageable, Map<String, String> blogContidions) {

        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!"".equals(blogContidions.get("title")) && blogContidions.get("title") != null) {
                    predicates.add(cb.like(root.<String>get("title"), "%" + blogContidions.get("title") + "%"));
                }
                if (!"".equals(blogContidions.get("typeId")) && blogContidions.get("typeId") != null) {
                    predicates.add(cb.equal(root.<Type>get("type").get("id"), Long.parseLong(blogContidions.get("typeId"))));
                }
                if (blogContidions.get("recommend") != "false") {
                    predicates.add(cb.equal(root.<Boolean>get("recommend"), Boolean.parseBoolean(blogContidions.get("recommend"))));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        }, pageable);


//        if (blogContidions != null) {
//
//            String title = blogContidions.getTitle();
//            Type type = blogContidions.getType();
//            String typename = "";
//            if(type!=null){
//                typename = type.getName();
//            }
//            boolean recommend = blogContidions.isRecommend();
//
//            if ((title == "" || title == null) && (typename == "" || typename == null)) {
//                return findAllBlogs(pageable);
//            }
//            if ((title != "" && title != null) && (typename == "" || typename == null)) {
//                return blogRepository.findByTitleAndRecommend(title, recommend,pageable);
//            }
//            if ((title == "" || title == null) && (typename != "" && typename != null)) {
//                return blogRepository.findByTypeAndRecommend(typename, recommend,pageable);
//            }
//            if ((title != "" && title != null) && (typename != "" && typename != null)) {
//                return blogRepository.findByTitleAndRecommendAndType(title, recommend, typename,pageable);
//            }
//        }
//        return null;
    }

    @Override
    public Blog findBlogById(Long id) {
        return blogRepository.findById(id).get();
    }

    @Override
    public List<Blog> findRecommendBlogs(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, 6, sort);
        return blogRepository.findRecommendBlogs(pageable);
    }

    @Override
    public Page<Blog> findBySearch(Pageable pageable, String query) {
        return blogRepository.findByQueryStr(pageable, query);
    }


    @Override
    public Blog markdownToHtml(Long id) {
        Blog blog = blogRepository.findById(id).get();
        if (blog == null) {
            throw new NotFoundException("该博客不存在，转换MARKDOWN处抛出");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog, b);
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(b.getContent()));
        return b;
    }

    @Override
    public List<Blog> findByContainsTagId(Long id) {
        List<Blog> allBlogs = blogRepository.findAll();
        List<Blog> blogs = new ArrayList<>();
        for (Blog blog : allBlogs) {
            List<Tag> tags = blog.getTags();
            for (Tag tag : tags) {
                if (tag.getId() == id) {
                    blogs.add(blog);
                }
            }
        }
        return blogs;
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<Blog> blogs = blogRepository.findAll();
        Set<String> years = new HashSet<>();
        int year = 0;
        Calendar c = Calendar.getInstance();
        for(Blog blog:blogs){
            Date updateTime = blog.getUpdateTime();
            c.setTime(updateTime);
            year = c.get(Calendar.YEAR);
            String yearStr = String.valueOf(year);
            years.add(yearStr);
        }
        Map<String,List<Blog>> yearAndBlogs = new HashMap<>();
        for(String str:years){
            List<Blog> byYear = blogRepository.findByYear(str);
            yearAndBlogs.put(str,byYear);
        }
        return yearAndBlogs;
    }

    @Override
    public String countBlog() {
        List<Blog> all = blogRepository.findAll();
        return String.valueOf(all.size());
    }
}
