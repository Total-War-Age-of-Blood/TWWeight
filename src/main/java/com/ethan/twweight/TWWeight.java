package com.ethan.twweight;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class TWWeight extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(this, this);
        BukkitTask weight_check = new WeightCheck().runTaskTimer(this, 0, 10);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if ((player.isGliding() || player.isSwimming()) && WeightCheck.shulker_list.containsKey(player.getUniqueId())){
            float box_count = WeightCheck.shulker_list.get(player.getUniqueId());
            if (player.isGliding()){
                player.setVelocity(player.getLocation().getDirection().multiply(0.0));
                player.sendMessage("The shulker is too heavy to glide!");
                return;
            }
            player.setVelocity(player.getLocation().getDirection().multiply(Math.max((0.2f - (0.025f * box_count)), 0.0f)));
            System.out.println("Swimming speed: " + player.getVelocity());
            System.out.println("Multiplier: " + (0.2f - (0.025f * box_count)));
        }
    }

    @EventHandler
    public void onChangeDimension(EntityPortalExitEvent event){
        if (!(event.getEntity() instanceof Player)){return;}
        Player player = (Player) event.getEntity();

        ItemStack[] contents = player.getInventory().getContents();
        float box_count = 0;
        for (ItemStack item : contents){
            if (item == null){continue;}
            if(WeightCheck.SHULKER_NAMES.contains(item.getType())){
                continue;}
            box_count++;
        }

        if (box_count > 0){
            float new_speed = player.getWalkSpeed()-(0.025f * box_count);
            player.setWalkSpeed(Math.max(new_speed, 0));
            player.setFlySpeed(Math.max(new_speed, 0));
            WeightCheck.shulker_list.put(player.getUniqueId(), box_count);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
