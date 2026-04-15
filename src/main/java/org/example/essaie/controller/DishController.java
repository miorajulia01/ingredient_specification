package org.example.essaie.controller;

import lombok.RequiredArgsConstructor;
import org.example.essaie.exception.BadRequestException;
import org.example.essaie.exception.NotFoundException;
import org.example.essaie.modele.DishIngredient;
import org.example.essaie.repository.DishRepository;
import org.example.essaie.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dishes")
public class DishController  {
    private final DishService dishService;

    @GetMapping
    public ResponseEntity<?> getAllDishes() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dishService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDishById(@PathVariable Integer id) {
        try{
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(dishService.getById(id));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("Dish.id=" + id + " is not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredient(
            @PathVariable Integer id,
            @RequestBody List<DishIngredient> ingredients
    ){
        try{
            dishService.updateIngredients(id, ingredients);
            return ResponseEntity.ok("Dish ingredients updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Dish.id=" + id + " is not found");
        } catch (BadRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
