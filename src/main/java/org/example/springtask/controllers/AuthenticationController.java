package org.example.springtask.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.SecurityWorkerDto;
import org.example.springtask.dto.Status;
import org.example.springtask.dto.WorkerDto;
import org.example.springtask.services.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@Controller("AuthenticationController")
public class AuthenticationController {

    private final ProjectService projectService;


    @RequestMapping(value = "/registration")
    public String getSaveWorker(@RequestParam(name = "error", required = false) Boolean error, Model model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", true);
        }
        model.addAttribute("worker", new SecurityWorkerDto());
        return "authenticationAndRegistration/registration";
    }


    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String saveWorker(@ModelAttribute SecurityWorkerDto worker) {
        Status status = projectService.saveWorker(worker.getFirstName(), worker.getLastName(), worker.getUsername(), worker.getPassword());
        return "authenticationAndRegistration/login";
    }

    @RequestMapping("/login")
    public String login(@RequestParam(name = "error", required = false) Boolean error, Model model) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", true);
        }
        model.addAttribute("userForm", new WorkerDto());
        return "authenticationAndRegistration/login";
    }

    @GetMapping("/backLogin")
    public String getLoginPage() {
        return "authenticationAndRegistration/login";
    }

    @PostMapping("/user")
    public String getIndexPage() {
        return "userPages/user";
    }

    @PostMapping("/admin")
    public String getAdminPage() {
        return "adminPages/adminShowObject";
    }

    @PostMapping("/adminWorkProject")
    public String getAdminProjectPage() {
        return "adminPages/functionalWork/adminWorkOnProject";
    }

    @PostMapping("/adminWorkTask")
    public String getAdminTaskPage() {
        return "adminPages/functionalWork/adminWorkOnTask";
    }
}
