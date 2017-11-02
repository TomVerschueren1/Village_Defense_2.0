package me.TomTheDeveloper.Kits;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.TomTheDeveloper.InvasionInstance;
import me.TomTheDeveloper.VillageDefense;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.KitAPI.BaseKits.PremiumKit;
import me.TomTheDeveloper.Utils.ArmorHelper;
import me.TomTheDeveloper.Utils.ParticleEffect;
import me.TomTheDeveloper.Utils.Util;
import me.TomTheDeveloper.Utils.WeaponHelper;
import pl.Plajer.GameAPI.LanguageManager;

/**
 * Created by Tom on 18/08/2014.
 */
public class CleanerKit extends PremiumKit implements Listener {

    private VillageDefense plugin;

    public CleanerKit(VillageDefense plugin) {
        this.plugin = plugin;
        setName(LanguageManager.getLanguageFile().get("Cleaner-Kit-Name").toString());
        List<String> description = Util.splitString(LanguageManager.getLanguageFile().get("Cleaner-Kit-Description").toString(), 40);
        this.setDescription(description.toArray(new String[description.size()]));
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission("minigames.vip") || player.hasPermission("minigames.mvip") || player.hasPermission("minigames.elite") || player.hasPermission("villagedefense.kit.cleaner");
    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setColouredArmor(Color.YELLOW, player);
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
        ItemStack cleaneritem = new ItemStack(Material.BLAZE_ROD);
        List<String> cleanerWandLore = Util.splitString(LanguageManager.getLanguageFile().get("Cleaner-Item-Lore").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"), 40);
        String[] cleanerWandLoreArray = cleanerWandLore.toArray(new String[cleanerWandLore.size()]);

        ItemStack itemStack = this.setItemNameAndLore(cleaneritem, LanguageManager.getLanguageFile().get("Cleaner-Wand-Name").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"), cleanerWandLoreArray);
        player.getInventory().addItem(cleaneritem);
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
    }

    @Override
    public Material getMaterial() {
        return Material.BLAZE_POWDER;
    }

    @Override
    public void reStock(Player player) {

    }

    @EventHandler
    public void onClean(PlayerInteractEvent event) {
        if (!event.hasItem())
            return;
        if (event.getItem().getType() != Material.BLAZE_ROD)
            return;
        if (!(event.getItem().hasItemMeta()))
            return;
        if (!(event.getItem().getItemMeta().hasDisplayName()))
            return;
        if (!(event.getItem().getItemMeta().getDisplayName().contains(LanguageManager.getLanguageFile().get("Cleaner-Wand-Name").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"))))
            return;
        if (plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        if (UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
            event.getPlayer().sendMessage(LanguageManager.getLanguageFile().get("You-Can't-Clean-You're-Spectator").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"));
            return;
        }
        InvasionInstance invasionInstance = (InvasionInstance) plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer());

        if (UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("clean") > 0 && !UserManager.getUser(event.getPlayer().getUniqueId()).isSpectator()) {
        	String msgstring = LanguageManager.getLanguageFile().get("Ability-Still-On-Cooldown").toString();
        	msgstring = msgstring.replaceFirst("%COOLDOWN%",Long.toString( UserManager.getUser(event.getPlayer().getUniqueId()).getCooldown("clean")));
        	event.getPlayer().sendMessage(msgstring.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
            return;
        }
        if (invasionInstance.getZombies() != null) {
            for (Zombie zombie : invasionInstance.getZombies()) {
                if (!plugin.is1_12_R1()) {
                    ParticleEffect.LAVA.display(1, 1, 1, 1, 20, zombie.getLocation(), 100);
                } else {
                    zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20, 1, 1, 1);
                }
                zombie.remove();
            }
            invasionInstance.getZombies().clear();
        } else {
            event.getPlayer().sendMessage(LanguageManager.getLanguageFile().get("Map-is-already-empty").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"));
            return;
        }
        if (plugin.is1_9_R1() || plugin.is1_12_R1()) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_DEATH, 1, 1);
        } else {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf("ZOMBIE_DEATH"), 1, 1);
        }
        String message = ChatManager.formatMessage(LanguageManager.getLanguageFile().get("Player-has-cleaned-the-map").toString(), event.getPlayer());
        for(Player player1 : plugin.getGameAPI().getGameInstanceManager().getGameInstance(event.getPlayer()).getPlayers()) {
            player1.sendMessage("�a[VillageDefense] " + message);
        }
        UserManager.getUser(event.getPlayer().getUniqueId()).setCooldown("clean", 1000);
    }
}
