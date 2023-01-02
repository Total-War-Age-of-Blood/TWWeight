package com.ethan.twweight;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WeightCheck extends BukkitRunnable {
    public static HashMap<UUID, Float> shulker_list = new HashMap<>();
    public static List<Material> SHULKER_NAMES = new ArrayList<>(Arrays.asList(Material.SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
    Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX,
    Material.LIME_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX,
    Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX));

    @Override
    public void run() {
        // System.out.println(shulker_list);
        for (Player player : Bukkit.getOnlinePlayers()){
            // Check to see if player has shulker box
            // If player just dropped all shulker boxes, return his speed to what it should be.
            // Because other plugins may change player speed, we cannot set it to an absolute number.
            // All speed adjustments must be relative to whatever the player's speed currently is.

            // Count the number of shulker boxes in player's inventory
            ItemStack[] contents = player.getInventory().getContents();
            float box_count = 0;
            for (ItemStack item : contents){
                if (item == null){continue;}
                if(!SHULKER_NAMES.contains(item.getType())){
                    continue;}
                box_count++;
            }

            if (box_count == 0){
                if (shulker_list.containsKey(player.getUniqueId())){
                    float new_speed = player.getWalkSpeed() + (0.025f * shulker_list.get(player.getUniqueId()));
                    player.setWalkSpeed(Math.max(new_speed, 0f));
                    player.setFlySpeed(Math.max(new_speed, 0f));
                    player.sendMessage("You feel lighter without any shulker boxes.");
                    shulker_list.remove(player.getUniqueId());
                }
                continue;}

            // Disable gliding on check to prevent circumvention with spam.
            if (player.isGliding()){
                player.setGliding(false);
                player.sendMessage("You can't fly with a shulker box!");
            }

            // Send player a message only if they are newly burdened
            // Then, adjust speed accordingly.
            if (!shulker_list.containsKey(player.getUniqueId())){
                player.sendMessage("The shulker box weighs you down.");
                shulker_list.put(player.getUniqueId(), box_count);
                float new_speed = player.getWalkSpeed()-(0.025f * box_count);
                player.setWalkSpeed(Math.max(new_speed, 0f));
                player.setFlySpeed(Math.max(new_speed, 0f));
                continue;
            }
            float recorded_amount = shulker_list.get(player.getUniqueId());
            // System.out.println(recorded_amount);
            // System.out.println(box_count);
            if (recorded_amount != box_count){
                float new_speed;
                if (recorded_amount > box_count){
                    new_speed = player.getWalkSpeed() + (0.025f *(recorded_amount - box_count));
                }else {
                    new_speed = player.getWalkSpeed()-(0.025f * (box_count - recorded_amount));
                }
                player.setWalkSpeed(Math.max(new_speed, 0f));
                player.setFlySpeed(Math.max(new_speed, 0f));
                shulker_list.put(player.getUniqueId(), box_count);
            }

            // System.out.println(player.getWalkSpeed());
            // System.out.println(shulker_list);
        }

    }
}
