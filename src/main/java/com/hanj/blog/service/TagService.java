package com.hanj.blog.service;

import com.hanj.blog.po.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    Tag findByName(String name);

    Page<Tag> findAll(Pageable pageable);

    Tag save(Tag tag);

    Tag findById(Long id);

    Tag updateTag(Tag tag,Long id);

    void deleteById(Long id);

    List<Tag> findAll();

    public List<Tag> listTag(String ids);

    List<Tag> findByPageable(Integer size);
}
