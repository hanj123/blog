package com.hanj.blog.service.serviceImpl;

import com.hanj.blog.NotFoundException;
import com.hanj.blog.dao.TagRepository;
import com.hanj.blog.po.Tag;
import com.hanj.blog.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("tagService")
@Transactional
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    public List<Tag> findByPageable(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC,"blogs.size");
        Pageable pageable = PageRequest.of(0,size,sort);
        return tagRepository.findByPageable(pageable);
    }

    @Override
    public Page<Tag> findAll(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag findById(Long id) {
        return tagRepository.findById(id).get();
    }

    @Override
    public Tag updateTag(Tag tag,Long id) {
        Tag tag1 = tagRepository.findById(id).get();
        if(tag1==null){
            throw new NotFoundException("id不存在");
        }else {
            BeanUtils.copyProperties(tag,tag1);
            return tagRepository.save(tag1);
        }
    }

    @Override
    public void deleteById(Long id) {

        tagRepository.deleteById(id);
    }

    @Override
    public List<Tag> findAll() {

        return tagRepository.findAll();
    }


    @Override
    public List<Tag> listTag(String ids) { //1,2,3

        List<Long> longIds = convertToList(ids);
        List<Tag> tags = new ArrayList<Tag>();
        for(Long id:longIds){
            Tag t = tagRepository.findById(id).get();
            tags.add(t);
        }
        return tags;

    }

    private List<Long> convertToList(String ids) {
        List<Long> list = new ArrayList<>();
        if (!"".equals(ids) && ids != null) {
            String[] idarray = ids.split(",");
            for (int i=0; i < idarray.length;i++) {
                list.add(new Long(idarray[i]));
            }
        }
        return list;
    }
}
