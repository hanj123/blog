package com.hanj.blog.po;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by limi on 2017/10/14.
 */
@Entity
@Table(name = "t_blog")
public class Blog {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

//    LAZY
    @Basic(fetch = FetchType.EAGER)
    @Lob
    private String content;
    private String firstPicture;
    //标记
    private String flag;
    //浏览次数
    private Integer views;
    //赞赏是否开启
    private boolean appreciation;
    //转载申明是否开启
    private boolean shareStatement;
    //是否可以评论
    private boolean commentabled;
    //是否发布
    private boolean published;
    //是否推荐
    private boolean recommend;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToOne(cascade = {CascadeType.MERGE},fetch = FetchType.EAGER)
    private Type type;


//    CascadeType.PERSIST然后save就报错
    @ManyToMany(cascade = {CascadeType.MERGE},fetch = FetchType.EAGER)
    private List<Tag> tags = new ArrayList<>();

    //这里新加需要一个tagId的集合,此注解不与数据库表映射
    @Transient
    private String tagIds;

    private String description;


    @ManyToOne
    private User user;


    @OneToMany(mappedBy = "blog",cascade = {CascadeType.MERGE})
    private List<Comment> comments = new ArrayList<>();

    public Blog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFirstPicture() {
        return firstPicture;
    }

    public void setFirstPicture(String firstPicture) {
        this.firstPicture = firstPicture;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public boolean isAppreciation() {
        return appreciation;
    }

    public void setAppreciation(boolean appreciation) {
        this.appreciation = appreciation;
    }

    public boolean isShareStatement() {
        return shareStatement;
    }

    public void setShareStatement(boolean shareStatement) {
        this.shareStatement = shareStatement;
    }

    public boolean isCommentabled() {
        return commentabled;
    }

    public void setCommentabled(boolean commentabled) {
        this.commentabled = commentabled;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //初始化标签id字符集，根据属性：标签ID的集合
    public void initTagIdsByTags() {
        this.tagIds = tagsToIds(this.getTags());
    }

    //1,2,3新加的方法,用于赋值tagIds字符串，根据tag List的集合
    private String tagsToIds(List<Tag> tags) {
        if (!tags.isEmpty()) {
            StringBuffer ids = new StringBuffer();
            boolean flag = false;
            for (Tag tag : tags) {
                if (flag) {
                    ids.append(",");
                } else {
                    flag = true;
                }
                ids.append(tag.getId());
            }
            return ids.toString();
        } else {
            return tagIds;
        }
    }

    @Override
    public String toString() {
        return "com.hanj.blog.po.Blog{" +
                "appreciation=" + appreciation +
                ", commentabled=" + commentabled +
                ", comments=" + comments +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", description='" + description + '\'' +
                ", firstPicture='" + firstPicture + '\'' +
                ", flag='" + flag + '\'' +
                ", id=" + id +
                ", published=" + published +
                ", recommend=" + recommend +
                ", shareStatement=" + shareStatement +
                ", tagIds='" + tagIds + '\'' +
                ", tags=" + tags +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", updateTime=" + updateTime +
                ", user=" + user +
                ", views=" + views +
                '}';
    }
}
