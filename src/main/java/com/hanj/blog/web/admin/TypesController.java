package com.hanj.blog.web.admin;

import com.hanj.blog.po.Type;
import com.hanj.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@Transactional
public class TypesController {

    @Autowired
    private TypeService typeService;

    @RequestMapping("/types")
    public String types(@PageableDefault(size = 4,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,Model model){

        model.addAttribute("page",typeService.listType(pageable));
        return "admin/types";
    }

    @RequestMapping("/typesInput")
    public ModelAndView typeInput(ModelAndView mv){

        Type type = new Type();
        mv.setViewName("admin/types-input");
        mv.addObject("type",type);
//        return "admin/types-input";
        return mv;
    }

    @RequestMapping(value = "/saveType",method = RequestMethod.POST)
    public String saveType(Type type,RedirectAttributes attr,Model model){

//        type.setId(System.currentTimeMillis());
        Type type1 = typeService.findByName(type.getName());
        if(type1!=null){
           model.addAttribute("message","分类已经存在了...");
           return "forward:typesInput";
        }
        Type t = typeService.saveType(type);
        if(t==null){
            attr.addFlashAttribute("message","新增失败了");
        }else {
            attr.addFlashAttribute("message","新增成功了");
        }
//        return "forward:typesInput";这就是转发到某个方法，上面写方法的访问路径
//        return "typesInput";这是错误的，如果只写这个，那就是视图解析器也解析不了，找不到页面
        return "redirect:/admin/types";
//        所以有两种写法，如果你要跳转到默认的页面，那就直接写页面的结构，可以不带后缀名html
//                还有一种是你转发到指定的方法去，然后利用这个方法到指定页面
    }


    @RequestMapping("/goUpdate/{id}")
    public String goUpdate(@PathVariable Long id, Model model){
        model.addAttribute("type",typeService.findById(id));
        return "admin/types-input";
    }

    @RequestMapping(value = "/updateType/{id}",method = RequestMethod.POST)
    public String updateType(Type type,@PathVariable Long id,RedirectAttributes attr){
        Type type1 = typeService.findById(id);
        if(type1==null){
            attr.addFlashAttribute("message","这个id对象不存在");
            return "forward:typesInput";
        }
        Type type2 = typeService.updateType(id, type);
        if(type2==null){
            attr.addFlashAttribute("message","更新失败");
        }else {
            attr.addFlashAttribute("message","更新成功");
        }
//        return "admin/types-input";
        return "redirect:/admin/types";
    }


    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable Long id,RedirectAttributes attr){

        typeService.deleteType(id);
        attr.addFlashAttribute("message","删除成功");
        return "redirect:/admin/types";
    }



}
