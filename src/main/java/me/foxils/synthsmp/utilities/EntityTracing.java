package me.foxils.synthsmp.utilities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class EntityTracing {

    @Nullable
    public static LivingEntity getEntityLookingAt(Player player) {
        World world = player.getWorld();

        Location eyeLocation = player.getEyeLocation().clone();

        Vector direction = eyeLocation.getDirection().clone();

        RayTraceResult traceResult = world.rayTraceEntities(eyeLocation.add(direction.clone().multiply(0.5)), eyeLocation.getDirection(), 5);

        if (traceResult == null) {
            return null;
        }

        Entity tracedEntity = traceResult.getHitEntity();

        if (!(tracedEntity instanceof LivingEntity livingEntity) || livingEntity.isInvulnerable() || livingEntity.equals(player)) {
            return null;
        }

        return livingEntity;
    }
}
