package me.foxils.scythesmp.items;

import me.foxils.foxutils.itemactions.ClickActions;
import me.foxils.foxutils.itemactions.PassiveAction;
import me.foxils.foxutils.itemactions.TakeDamageAction;
import me.foxils.foxutils.utilities.ItemAbility;
import me.foxils.foxutils.utilities.ItemUtils;
import me.foxils.scythesmp.utilities.EntityTracing;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
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
import org.bukkit.util.Vector;

import java.util.*;

public class WaterGem extends UpgradeableItem implements TakeDamageAction, PassiveAction, ClickActions {

    private int arrows = 4;

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

    public WaterGem(Material material, int customModelData, String name, Plugin plugin, List<ItemAbility> abilityList) {
        super(material, customModelData, name, plugin, abilityList, 3, 0);
    }

    @Override
    public void onTakeDamage(EntityDamageEvent event, ItemStack itemStack) {
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
    public void rightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        waterJet(player, item);
    }

    @Override
    public void rightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        super.rightClickBlock(event, itemInteracted);

        rightClickAir(event, itemInteracted);
    }

    @Override
    public void shiftRightClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        rainDropOfGod(player, item);
    }

    @Override
    public void shiftRightClickBlock(PlayerInteractEvent event, ItemStack itemInteracted) {
        shiftRightClickAir(event, itemInteracted);
    }

    @Override
    public void leftClickAir(PlayerInteractEvent event, ItemStack itemInteracted) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        waterArrow(player, item);
    }

    private void effectBonus(Player player) {
        player.addPotionEffects(passivePotionEffects);
    }

    private void waterJet(Player playerAttacking, ItemStack item) {
        LivingEntity tracedEntity = EntityTracing.getEntityLookingAt(playerAttacking);

        if (tracedEntity == null) {
            return;
        }

        if (ItemUtils.getCooldown(new NamespacedKey(plugin, "water_jet_cooldown"), item, 600, playerAttacking, new TextComponent(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Used Water Jet"))) {
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
        tracedEntity.setHealth(tracedEntity.getHealth() - 3);
        tracedEntity.addPotionEffect(waterJetPotionEffect);
    }
    
    private void rainDropOfGod(Player player, ItemStack item) {
        if (ItemUtils.getCooldown(new NamespacedKey(plugin, "god_drop_cooldown"), item, 900, player, new TextComponent(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Launched The Wave"))) {
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

                    hitEntity.setHealth(Math.max(0, hitEntity.getHealth() - 8));
                    hitEntity.damage(0.1, playerThrower);

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
        if (!ItemUtils.getCooldown(new NamespacedKey(plugin, "water_arrow_restock"), item, 420, player, new TextComponent(ChatColor.GOLD + "" + ChatColor.BOLD + "Restocked Water Arrows"))) {
            arrows = 4;
        }

        if (arrows < 1) {
            return;
        }

        if (ItemUtils.getCooldown(new NamespacedKey(plugin, "water_arrow_cooldown"), item, 1, player, new TextComponent(ChatColor.BLUE + "" + ChatColor.BOLD + "Launched Water Arrow"))) {
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
        arrow.setColor(Color.AQUA);
        arrow.setDamage(2);
    }
}
