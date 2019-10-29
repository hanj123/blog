package com.hanj.blog.dao;

import com.hanj.blog.po.Blog;
import com.hanj.blog.po.Tag;
import com.hanj.blog.po.Type;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog,Long> , JpaSpecificationExecutor<Blog> {


    Blog findByTitle(String title);

    @Query("select b from Blog b where recommend=1 order by updateTime DESC ")
    List<Blog> findRecommendBlogs(Pageable pageable);

    @Query(value = "select * from t_blog where title like CONCAT('%',:query,'%') or content like CONCAT('%',:query,'%')",nativeQuery = true)
    Page<Blog> findByQueryStr(Pageable pageable, @Param("query") String query);

    @Query("select b from Blog b where function('date_format',b.updateTime,'%Y') = :year ")
    List<Blog> findByYear(@Param("year") String str);



    @Modifying
    @Query(value = "update t_Blog set flag=:flag,title=:title,content=:content,type_id=:type_id," +
            "first_picture=:first_picture,description=:description,recommend=:recommend," +
            "share_statement=:share_statement,appreciation=:appreciation,commentabled=:commentabled where id=:id",nativeQuery = true)
    void updateBlogSomeFileds(@Param("flag") String flag, @Param("title") String title, @Param("content") String content,
                              @Param("type_id") Long type_id,@Param("first_picture") String first_picture,
                              @Param("description") String description,@Param("recommend") boolean recommend,@Param("share_statement") boolean share_statement,
                              @Param("appreciation") boolean appreciation,@Param("commentabled") boolean commentabled,@Param("id") Long id);

    @Query(value = "select tags_id from t_blog_tags where blogs_id=:id",nativeQuery = true)
    List<Long> findTagsId(@Param("id") Long id);


    @Modifying
    @Query(value = "insert into t_blog_tags(blogs_id,tags_id) values(:blogId,:tagId) ",nativeQuery = true)
    void updateTags(@Param("blogId")Long blogId,@Param("tagId")Long tagId);


//    @Query("select * from Blog where tagId in (select tags from Blog where )")
//    Page<Blog> findByTagsContaining(Pageable pageable,@Param("tagId") Long tagId);

//    Page<Blog> findByTitleAndRecommend(String title, boolean recommend, Pageable pageable);
//
//    Page<Blog> findByTypeAndRecommend(String typename, boolean recommend, Pageable pageable);
//
//    Page<Blog> findByTitleAndRecommendAndType(String title, boolean recommend,String typename, Pageable pageable);
}
