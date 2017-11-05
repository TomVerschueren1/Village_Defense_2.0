package me.TomTheDeveloper.Kits;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.KitAPI.BaseKits.PremiumKit;
import me.TomTheDeveloper.Permissions.PermissionsManager;
import me.TomTheDeveloper.Utils.Util;
import me.TomTheDeveloper.Utils.WeaponHelper;

/**
 * Created by Tom on 28/07/2015.
 */
public class PremiumHardcoreKit extends PremiumKit {

    public PremiumHardcoreKit() {
        setName(ChatManager.colorMessage("Kits.Premium-Hardcore.Kit-Name"));
        List<String> description = Util.splitString(ChatManager.colorMessage("Kits.Premium-Hardcore.Kit-Description"), 40);
        this.setDescription(description.toArray(new String[description.size()]));
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission(PermissionsManager.getVIP()) || player.hasPermission(PermissionsManager.getMVP()) || player.hasPermission(PermissionsManager.getELITE()) || player.hasPermission("villagedefense.kit.premiumhardcore");
    }

    @Override
    public void giveKitItems(Player player) {
        player.getInventory().addItem(WeaponHelper.getEnchanted(new ItemStack(Material.DIAMOND_SWORD), new Enchantment[]{Enchantment.DAMAGE_ALL}, new int[]{11}));
        player.setMaxHealth(6);
        player.getInventory().addItem(new ItemStack(Material.SADDLE));

    }

    @Override
    public Material getMaterial() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public void reStock(Player player) {

    }


}
