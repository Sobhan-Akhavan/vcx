package ir.vcx.api.contrller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Report")
@CrossOrigin("*")
@RequestMapping("/api/v1/reports")
@SecurityRequirement(name = "Bearer")
@RestController
public class ReportController {

}
