package me.foxils.glitchedSmp.items;

import me.foxils.foxutils.Item;
import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.itemactions.TakeDamageAction;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.glitchedSmp.GlitchedSmp;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;

public class WaterGem extends Item implements TakeDamageAction, PassiveAction, ClickActions {

    private int arrows = 4;

    private static final Plugin plugin = GlitchedSmp.getInstance();

    private static final double damageMultiplierNether = 0.5;

    private static final List<List<FallingBlock>> blocksGroupsThrown = new ArrayList<>();
    private static final HashMap<List<FallingBlock>, Player> thrownBlockGroupPlayerMap = new HashMap<>();
    private static List<List<FallingBlock>> toBeRemoved = new ArrayList<>();

    private static final Vector throwVector = new Vector(0, 0.5, 0);

    public static final PotionEffect waterJetPotionEffect = new PotionEffect(PotionEffectType.MINING_FATIGUE, 300, 0, false, false);
    
    public static final List<PotionEffect> passivePotionEffects = Arrays.asList(
            new PotionEffect(PotionEffectType.CONDUIT_POWER, 200, 0, false, false),
            new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 0, false, false)
    );

    public WaterGem(Material material, String name, NamespacedKey key, List<ItemStack> itemsForRecipe, boolean shapedRecipe) {
        super(material, name, key, itemsForRecipe, shapedRecipe);
    }

    @Override
    public void onTakeDamage(EntityDamageEvent event) {
        Player playerTakingDamage = (Player) event.getEntity();

        if (playerTakingDamage.getWorld().getEnvironment() != World.Environment.NETHER) {
            return;
        }

        double damageAmount = event.getDamage();

        event.setDamage(damageAmount * damageMultiplierNether);
    }

    @Override
    public void passiveAction(Player player, ItemStack item) {
        effectBonus(player);
    }

    @Override
    public void rightClickAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        waterJet(player, item);
    }

    @Override
    public void shiftRightClickAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        rainDropOfGod(player, item);
    }

    @Override
    public void rightClickBlock(PlayerInteractEvent event) {
        rightClickAir(event);
    }

    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event) {
        shiftRightClickAir(event);
    }

    @Override
    public void leftClickAir(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        waterArrow(player, item);
    }

    private static void effectBonus(Player player) {
        player.addPotionEffects(passivePotionEffects);
    }

    private static void waterJet(Player playerAttacking, ItemStack item) {
        LivingEntity tracedEntity = getEntityLookingAt(playerAttacking, 6.5);

        if (tracedEntity == null) {
            return;
        }

        if (ItemUtils.getCooldown(new NamespacedKey(plugin, "water_jet_cooldown"), item, 600)) {
            return;
        }

        World world = playerAttacking.getWorld();

        Location start = playerAttacking.getEyeLocation().clone();
        Location end = tracedEntity.getLocation().clone();
        Vector direction = start.getDirection().clone();

        double size = start.distance(end);

        for (double i = 1; i < size; i+=0.25) {
            direction.multiply(i);
            start.add(direction);

            world.spawnParticle(Particle.FALLING_WATER, start, 1, 0, 0, 0, 1, null, true);
            world.playSound(start, Sound.ENTITY_BOAT_PADDLE_WATER, 1, 0.7F);

            start.subtract(direction);
            direction.normalize();
        }

        tracedEntity.damage(0, playerAttacking);
        tracedEntity.setHealth(Math.max(tracedEntity.getHealth() - 6, 0));
        tracedEntity.addPotionEffect(waterJetPotionEffect);
    }

    @Nullable
    private static LivingEntity getEntityLookingAt(Player player, double distanceAhead) {
        World world = player.getWorld();

        Location eyeLocation = player.getEyeLocation().clone();

        Vector direction = eyeLocation.getDirection().clone();

        RayTraceResult traceResult = world.rayTraceEntities(eyeLocation.add(direction.clone().multiply(0.5)), eyeLocation.getDirection(), distanceAhead);

        if (traceResult == null) {
            return null;
        }

        Entity tracedEntity = traceResult.getHitEntity();

        if (!(tracedEntity instanceof LivingEntity livingEntity) || livingEntity.isInvulnerable() || livingEntity.equals(player)) {
            return null;
        }

        return livingEntity;
    }
    
    private static void rainDropOfGod(Player player, ItemStack item) {
        if (ItemUtils.getCooldown(new NamespacedKey(plugin, "god_drop_cooldown"), item, 900)) {
            return;
        }

        World world = player.getWorld();
        Location playerPosition = player.getLocation();

        List<FallingBlock> blockGroup = new ArrayList<>();

        for (int y = -2; y < 1; y++) {
            if (y == -2 || y == 0) {
                for (int x = -1; x < 2; x++) {
                    if (x == 0) {
                        for (int z = -1; z < 2; z++) {
                            blockGroup.add(createWaterBlock(world, playerPosition.clone().add(x, y, z)));
                        }
                        continue;
                    }

                    blockGroup.add(createWaterBlock(world, playerPosition.clone().add(x, y, 0)));
                }
                continue;
            }
            for (int x = -2; x < 3; x++) {
                if (x > -2 && x < 2) {
                    if (x == 0) {
                        for (int z = -2; z < 3; z++) {
                            blockGroup.add(createWaterBlock(world, playerPosition.clone().add(x, y, z)));
                        }
                        continue;
                    }

                    for (int z = -1; z < 2; z++) {
                        blockGroup.add(createWaterBlock(world, playerPosition.clone().add(x, y, z)));
                    }
                    continue;
                }

                blockGroup.add(createWaterBlock(world, playerPosition.clone().add(x, y, 0)));
            }
        }

        blocksGroupsThrown.add(blockGroup);
        thrownBlockGroupPlayerMap.put(blockGroup, player);
    }

    private static FallingBlock createWaterBlock(World world, Location location) {
        BlockData waterData = Material.LAPIS_BLOCK.createBlockData();

        FallingBlock fallingBlock = world.spawnFallingBlock(location.clone().add(0, 3, 0), waterData);

        fallingBlock.setCancelDrop(true);
        fallingBlock.setVelocity(location.getDirection().clone().add(throwVector).multiply(1));

        return fallingBlock;
    }

    public static void customThrowCollision() {
        if (!toBeRemoved.isEmpty()) {
            for (List<FallingBlock> blockGroupToBeRemoved : toBeRemoved) {
                removeBlockGroup(blockGroupToBeRemoved);
            }
            toBeRemoved = new ArrayList<>();
        }

        if (blocksGroupsThrown.isEmpty()) {
            return;
        }

        for (List<FallingBlock> blockGroup : blocksGroupsThrown) {
            for (FallingBlock block : blockGroup) {
                if (block.isOnGround()) {
                    if (!toBeRemoved.contains(blockGroup)) {
                        toBeRemoved.add(blockGroup);
                    }
                    break;
                }

                if (toBeRemoved.contains(blockGroup)) {
                    break;
                }

                World blockWorld = block.getWorld();

                Collection<? extends Entity> collidingStuff = blockWorld.getNearbyEntities(block.getBoundingBox());

                for (Entity collidingEntity : collidingStuff) {
                    if (!(collidingEntity instanceof LivingEntity hitEntity)) {
                        continue;
                    }

                    Player playerThrower = thrownBlockGroupPlayerMap.get(blockGroup);

                    if (hitEntity == playerThrower) {
                        continue;
                    }

                    if (hitEntity.isDead()) {
                        continue;
                    }

                    hitEntity.damage(0.000001, playerThrower);
                    hitEntity.setHealth(Math.max(0, hitEntity.getHealth() - 6));

                    if (!toBeRemoved.contains(blockGroup)) {
                        toBeRemoved.add(blockGroup);
                    }
                }
            }

        }
    }

    private static void removeBlockGroup(List<FallingBlock> blockGroup) {
        blocksGroupsThrown.remove(blockGroup);
        thrownBlockGroupPlayerMap.remove(blockGroup);
        for (FallingBlock block : blockGroup) {
            block.remove();
        }
        Location blockLocation = blockGroup.getFirst().getLocation();
        World blockWorld = blockGroup.getFirst().getWorld();

        blockWorld.spawnParticle(Particle.BUBBLE_POP, blockLocation, 20, 2, 2, 2, 0);
        blockWorld.spawnParticle(Particle.BUBBLE, blockLocation, 20, 2, 2, 2, 0);
        blockWorld.spawnParticle(Particle.BUBBLE_COLUMN_UP, blockLocation, 20, 2, 2, 2, 0);
        blockWorld.playSound(blockLocation, Sound.ENTITY_PLAYER_SPLASH, 1, 0.8F);
    }

    private void waterArrow(Player player, ItemStack item) {
        if (!ItemUtils.getCooldown(new NamespacedKey(plugin, "water_arrow_restock"), item, 10)) {
            arrows = 4;
        }

        if (arrows < 1) {
            player.sendMessage("You ran out of water arrows! Wait for your restock cooldown.");
            return;
        }

        if (ItemUtils.getCooldown(new NamespacedKey(plugin, "water_arrow_cooldown"), item, 1)) {
            return;
        }

        arrows--;

        World world = player.getWorld();

        Location eyeLocation = player.getEyeLocation().clone();

        Arrow arrow = world.spawn(eyeLocation, Arrow.class);

        arrow.setVelocity(eyeLocation.getDirection());

        arrow.setShooter(player);
        arrow.setWeapon(item);

        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        arrow.setBasePotionType(PotionType.POISON);
        arrow.setDamage(2);
    }
}
