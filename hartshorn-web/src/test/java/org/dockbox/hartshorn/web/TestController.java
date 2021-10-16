package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.web.annotations.RequestBody;
import org.dockbox.hartshorn.web.annotations.RestController;
import org.dockbox.hartshorn.web.annotations.http.HttpGet;
import org.dockbox.hartshorn.web.annotations.http.HttpPost;

@RestController
public class TestController {

    @HttpGet("/get")
    public String get() {
        return "JUnit GET";
    }

    @HttpPost("/post")
    public String post(@RequestBody final String body) {
        return body;
    }
}
