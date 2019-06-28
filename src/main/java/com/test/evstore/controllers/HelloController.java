package com.test.evstore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "hello")
public class HelloController {

    @RequestMapping(value = "/{name}")
    @ResponseBody
    public String helloUrlSegment(@PathVariable String name) {
        return "Hello " + name;
    }

    @RequestMapping(value = "")
    @ResponseBody
    public String index(HttpServletRequest request) {

        String name = request.getParameter("name");

        return "Hello " + ((name != null) ? name : "World") + "!!";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public String helloForm() {

        String html = "<form method='POST'>" +
                "<input type='text' name='name' />" +
                "<input type='submit' value='Greet Me!' />" +
                "</form>";

        return html;

    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public String helloPost(HttpServletRequest request) {
        String name = request.getParameter("name");

        return "Hello " + ((name != null) ? name : "World") + "!!";
    }

    @RequestMapping(value = "goodbye")
    public String goodBye() {
        return "index";
    }

}

