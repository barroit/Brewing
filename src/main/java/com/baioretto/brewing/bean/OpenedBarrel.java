package com.baioretto.brewing.bean;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.block.Barrel;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
@ToString
public class OpenedBarrel {
    Barrel barrel;
    @Singular
    ArrayList<HumanEntity> viewers = new ArrayList<>();

    public OpenedBarrel viewers(HumanEntity entity) {
        viewers.add(entity);
        return this;
    }
}
