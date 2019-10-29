package com.hanj.blog.web.admin;

import com.hanj.blog.dao.TagRepository;
import com.hanj.blog.po.Tag;
import com.hanj.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@Transactional
public class TagsController {

    @Autowired
    private TagService tagService;

    @RequestMapping("/tags")
    public String tags(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.ASC) Pageable pageable,Model model){
        model.addAttribute("page",tagService.findAll(pageable));
        return "admin/tags";
    }

    @RequestMapping("/tagsInput")
    public String tagsInput(Model model){
        model.addAttribute("tag",new Tag());
        return "admin/tags-input";
    }

    @RequestMapping("/saveTag")
    public String insertTag(Tag tag, RedirectAttributes attr){

        Tag tag1 = tagService.findByName(tag.getName());
        if(tag1!=null){
            attr.addFlashAttribute("message","该标签已经有了，不要再输了");
            return "admin/tags-input";
        }
        Tag tag2 = tagService.save(tag);
        if(tag2!=null){
            attr.addFlashAttribute("message","新增成功");
        }else {
            attr.addFlashAttribute("message","新增失败");
        }
        return "redirect:/admin/tags";
    }

    @RequestMapping("/goUpdateTag/{id}")
    public String goUpdate(@PathVariable Long id,Model model){
        model.addAttribute("tag",tagService.findById(id));
        return "admin/tags-input";
    }

    @RequestMapping("/updateTag/{id}")
    public String updateTag(@PathVariable Long id,Tag tag,RedirectAttributes attr){

//        Tag tag1 = tagService.findById(id);
//        if(tag1==null){
//
//            attr.addFlashAttribute("message","没有该ID对应得对象");
//            return "redirect:/admin/tags";
//        }
        Tag t = tagService.updateTag(tag, id);
        if(t==null){
            attr.addFlashAttribute("message","更新失败了啊");
        }else {
            attr.addFlashAttribute("message","更新成功了啊");
        }
        return "redirect:/admin/tags";
    }


    @RequestMapping("/deleteTag/{id}")
    public String deleteTag(@PathVariable Long id,RedirectAttributes attr){

        tagService.deleteById(id);
        attr.addFlashAttribute("message","删除成功啦");
        return "redirect:/admin/tags";
    }
}
