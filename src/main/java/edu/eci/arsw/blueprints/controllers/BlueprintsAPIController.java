package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.ApiResponse;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("api/v1/blueprints")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    @GetMapping
    public ApiResponse<Set<Blueprint>> getAllResponse(){
        ApiResponse<Set<Blueprint>> response = new ApiResponse<>(200, "execute ok", services.getAllBlueprints());
        return response;
    }

    // GET /blueprints/{author}
    @GetMapping("/{author}")
    public ApiResponse<?> byAuthorResponse(@PathVariable String author){
        try{
            ApiResponse<Set<Blueprint>> response = new ApiResponse<>(200, "execute ok", services.getBlueprintsByAuthor(author));
            return response;
        } catch(BlueprintNotFoundException e){
            ApiResponse<?> response = new ApiResponse<>(404, "not found", null);
            return response;
        }
    }

    // GET /blueprints/{author}/{bpname}
    @GetMapping("/{author}/{bpname}")
    public ApiResponse<?> byAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "execute ok", services.getBlueprint(author, bpname));
            return response;
        } catch (BlueprintNotFoundException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "not found", null);
            return response;
        }
    }

    // POST /blueprints
    @PostMapping
    public ApiResponse<?> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.CREATED.value(), "created ok", bp);
            return response;
        } catch (BlueprintPersistenceException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "invalid data", null);
            return response;
        }
    }

    // PUT /blueprints/{author}/{bpname}/points
    @PutMapping("/{author}/{bpname}/points")
    public ApiResponse<?> addPoint(@PathVariable String author, @PathVariable String bpname,
                                      @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.x(), p.y());
            HashSet<String> data = new HashSet<>();
            data.add(author);
            data.add(bpname);

            ApiResponse<?> response = new ApiResponse<>(HttpStatus.ACCEPTED.value(), "created ok", p);
            return response;
        } catch (BlueprintNotFoundException e) {
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "not found", null);
            return response;
        }
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}
