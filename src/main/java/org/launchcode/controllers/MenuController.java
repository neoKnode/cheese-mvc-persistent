package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


@Controller
@RequestMapping(value = "menu")
public class MenuController {
    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;


    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menu");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenu(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenu(@ModelAttribute @Valid Menu newMenu,
                                     Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }


    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu (Model model, @PathVariable int menuId) {
        Menu thisMenu = menuDao.findOne(menuId);
        model.addAttribute("title", thisMenu.getName());
        model.addAttribute("menu", thisMenu);
        return "menu/view";

    }


    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String displayAddMenuItemForm(Model model, @PathVariable int menuId) {
        AddMenuItemForm thisMenu = new AddMenuItemForm(menuDao.findOne(menuId), cheeseDao.findAll());
        model.addAttribute("title", "Add item to menu: " + thisMenu.getMenu().getName());
        model.addAttribute("menu", thisMenu);
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String processAddMenuItemForm(@ModelAttribute @Valid AddMenuItemForm menuForm,
                                 Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("menu", menuForm);
            model.addAttribute("title", "Add Menu");
            return "menu/add-item";
        }

        Menu thisMenu = menuDao.findOne(menuForm.getMenuId());
        thisMenu.addItem(cheeseDao.findOne(menuForm.getCheeseId()));
        menuDao.save(thisMenu);

        return "redirect:view/" + thisMenu.getId();
    }

}
