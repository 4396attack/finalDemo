package album.yyj.zust.aiface.controller;

import album.yyj.zust.aiface.service.PhotoFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cutPhoto")
public class CutController {
    @Autowired
    private PhotoFaceService photoFaceService;

}
