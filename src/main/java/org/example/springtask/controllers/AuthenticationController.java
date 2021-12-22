package org.example.springtask.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.SecurityWorkerDto;
import org.example.springtask.dto.WorkerDto;
import org.example.springtask.services.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Controller("AuthenticationController")
public class AuthenticationController {

    private final ProjectService projectService;
    private final MainController mainController;

    @GetMapping(value = "/registration")
    public String getSaveWorker(@RequestParam(name = "error", required = false) Boolean error, Model model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", true);
        }
        model.addAttribute("worker", new SecurityWorkerDto());
        return "/registration";
    }

    @PostMapping(value = "/registration")
    public String saveWorker(@ModelAttribute SecurityWorkerDto worker) {
        projectService.saveWorker(worker.getFirstName(), worker.getLastName(), worker.getUsername(), worker.getPassword());
        return "redirect:/login";
    }


    @RequestMapping("/login")
    public String login(@RequestParam(name = "error", required = false) Boolean error, Model model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", true);
        }
        model.addAttribute("userForm", new WorkerDto());
        return "login";
    }

    @GetMapping("/index")
    public String getIndexPage(){
        return "index";
    }

}
