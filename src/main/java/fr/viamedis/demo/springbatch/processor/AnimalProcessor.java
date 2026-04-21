package fr.viamedis.demo.springbatch.processor;

import fr.viamedis.demo.springbatch.model.Animal;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class AnimalProcessor implements ItemProcessor<Animal, Animal> {

    @Override
    public Animal process(Animal animal) {
        return new Animal(
                animal.getId(),
                animal.getName().toUpperCase(),
                animal.getAge()
        );
    }
}