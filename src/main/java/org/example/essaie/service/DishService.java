package org.example.essaie.service;

import lombok.RequiredArgsConstructor;
import org.example.essaie.exception.NotFoundException;
import org.example.essaie.modele.Dish;
import org.example.essaie.modele.DishIngredient;
import org.example.essaie.repository.DishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishRepository dishRepository;

    public List<Dish> getAll() {
        return dishRepository.findAll();
    }

    public Dish getById(Integer id){
       Dish dishy = dishRepository.findDishById(id);
       if (dishy == null)
           throw new NotFoundException("Dish.id=" + id + " is not found");
       return dishy;
    }

    @Transactional
    public Dish updateIngredients(Integer id, List<DishIngredient> ingredients) {
        getById(id);
        dishRepository.updateDishIngredients(id, ingredients);
        return dishRepository.findDishById(id);
    }
}
