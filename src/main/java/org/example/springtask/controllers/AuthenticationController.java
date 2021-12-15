package org.example.springtask.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.WorkerDto;
import org.example.springtask.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller("AuthenticationController")
@ComponentScan(basePackages = "org.example.springtask.services")
public class AuthenticationController {

    @Autowired
    private ProjectService projectService;


    @GetMapping(value = "sign_up")
    public String getSaveWorker(Model model) {
        model.addAttribute("worker", new WorkerDto());
        return "sign_up";
    }

    @PostMapping(value = "sign_up")
    public String saveWorker(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String login, @RequestParam String password) {
        projectService.saveWorker(firstName, lastName, login, password);
        return "redirect:/sign_in";
    }


    @RequestMapping("/login")
    public String login(@RequestParam(name = "error", required = false) Boolean error,
                        Model model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", true);
        }
        model.addAttribute("userForm", new WorkerDto());
        return "login";
    }


}
