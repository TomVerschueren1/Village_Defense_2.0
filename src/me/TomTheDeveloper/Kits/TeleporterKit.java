package me.TomTheDeveloper.Kits;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.TomTheDeveloper.GameAPI;
import me.TomTheDeveloper.InvasionInstance;
import me.TomTheDeveloper.VillageDefense;
import me.TomTheDeveloper.Game.GameInstance;
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
public class TeleporterKit extends PremiumKit implements Listener {

    private VillageDefense plugin;
    private GameAPI gameAPI;

    public TeleporterKit(VillageDefense plugin) {
        this.plugin = plugin;
        gameAPI = plugin.getGameAPI();
        
        setName(LanguageManager.getLanguageFile().get("Teleporter-Kit-Name").toString());
        List<String> description = Util.splitString(LanguageManager.getLanguageFile().get("Teleporter-Kit-Description").toString(), 40);
        this.setDescription(description.toArray(new String[description.size()]));
    }

    @Override
    public boolean isUnlockedByPlayer(Player player) {
        return player.hasPermission("minigames.vip") || player.hasPermission("minigames.mvip") || player.hasPermission("minigames.elite") || player.hasPermission("villagedefense.kit.teleporter");
    }

    @Override
    public void giveKitItems(Player player) {
        ArmorHelper.setArmor(player, ArmorHelper.ArmorType.GOLD);
        player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));

        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        player.getInventory().addItem(new ItemStack(Material.SADDLE));
        ItemStack enderpealteleporter = new ItemStack(Material.ENDER_PEARL);
        List<String> teleporationlore = Util.splitString(LanguageManager.getLanguageFile().get("Teleportion-Item-Lore").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"), 40);
        this.setItemNameAndLore(enderpealteleporter, LanguageManager.getLanguageFile().get("Teleportation-Menu-Name").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"), teleporationlore.toArray(new String[teleporationlore.size()]));
        player.getInventory().addItem(enderpealteleporter);
    }

    @Override
    public Material getMaterial() {
        return Material.ENDER_PEARL;
    }

    @Override
    public void reStock(Player player) {

    }

    public void OpenAndCreateTeleportationMenu(World world, Player p) {
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(p);
        Inventory inventory = plugin.getServer().createInventory(null, 18, LanguageManager.getLanguageFile().get("Teleportation-Menu-Name").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"));
        for (Player player : world.getPlayers()) {
            if (gameAPI.getGameInstanceManager().getGameInstance(player) != null && !UserManager.getUser(player.getUniqueId()).isFakeDead()) {
                ItemStack skull = new ItemStack(397, 1, (short) 3);

                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(player.getName());
                meta.setDisplayName(player.getName());
                meta.setLore(Arrays.asList(""));
                skull.setItemMeta(meta);
                inventory.addItem(skull);
            }
        }
        for (Villager villager : ((InvasionInstance) gameInstance).getVillagers()) {


            ItemStack villageritem = new ItemStack(Material.EMERALD);
            this.setItemNameAndLore(villageritem, villager.getCustomName(), new String[]{villager.getUniqueId().toString()});

            inventory.addItem(villageritem);

        }
        p.openInventory(inventory);
    }


    @EventHandler
    public void OpenInventoryRightClickEnderPearl(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (gameAPI.getGameInstanceManager().getGameInstance(e.getPlayer()) == null)
                return;
            if (!(e.getPlayer().getItemInHand() == null)) {
                if (e.getPlayer().getItemInHand().hasItemMeta()) {
                    if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == null)
                        return;

                    if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(LanguageManager.getLanguageFile().get("Teleportion-Item-Name").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"))) {
                        OpenAndCreateTeleportationMenu(e.getPlayer().getWorld(), e.getPlayer());
                    }
                }
            }
            if (e.getPlayer().getItemInHand().getType() == Material.ENDER_PEARL) {
                e.setCancelled(true);
            }

        }
    }


    @EventHandler
    public void PlayerClickToTeleport(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (gameAPI.getGameInstanceManager().getGameInstance(p) == null)
            return;
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(p);
        if (e.getCurrentItem() == null)
            return;
        if (!e.getCurrentItem().hasItemMeta())
            return;
        if (!e.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        if (!e.getCurrentItem().getItemMeta().hasLore())
            return;
        if (e.getCurrentItem().hasItemMeta()) {
            if (e.getInventory().getName().equalsIgnoreCase(LanguageManager.getLanguageFile().get("Teleportation-Menu-Name").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"))) {
                e.setCancelled(true);
                if ((e.isLeftClick() || e.isRightClick())) {
                    if (e.getCurrentItem().getType() == Material.EMERALD) {
                        boolean villagerfound = false;
                        for (Villager villager : ((InvasionInstance) gameInstance).getVillagers()) {
                            if (villager.getCustomName() == null) {
                                villager.remove();
                            }
                            if (villager.getCustomName().equalsIgnoreCase(e.getCurrentItem().getItemMeta().getDisplayName()) && villager.getUniqueId().toString().equalsIgnoreCase(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0)))) {
                                e.getWhoClicked().teleport(villager.getLocation());
                                if (plugin.is1_9_R1() || plugin.is1_12_R1()) {
                                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
                                } else {
                                    p.getWorld().playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
                                }
                                if (!plugin.is1_12_R1())
                                    ParticleEffect.PORTAL.display(1, 1, 1, 10, 30, p.getLocation(), 100);
                                else {
                                    p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30, 1, 1, 1);
                                }
                                villagerfound = true;
                                p.sendMessage(LanguageManager.getLanguageFile().get("Teleported-To-Villager").toString());
                                break;
                            }
                        }
                        if (!villagerfound) {
                            p.sendMessage(LanguageManager.getLanguageFile().get("Didn't-Found-The-Villager").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"));
                        }
                        villagerfound = false;
                        e.setCancelled(true);
                    } else { /*if(e.getCurrentItem().getType() == Material.SKULL_ITEM || e.getCurrentItem().getType() == Material.SKULL)*/

                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        for (Player player : gameInstance.getPlayers()) {
                            if (player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
                                p.sendMessage(ChatManager.formatMessage(LanguageManager.getLanguageFile().get("Teleported-To-Player").toString(), player));
                                p.teleport(player);
                                if (plugin.is1_9_R1() || plugin.is1_12_R1()) {
                                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
                                } else {
                                    p.getWorld().playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
                                }
                                if (!plugin.is1_12_R1())
                                    ParticleEffect.PORTAL.display(1, 1, 1, 10, 30, p.getLocation(), 100);
                                else {
                                    p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 30, 1, 1, 1);
                                }
                                p.closeInventory();
                                e.setCancelled(true);
                                return;

                            }
                        }
                        p.sendMessage(LanguageManager.getLanguageFile().get("Player-Not-Found").toString().replaceAll("(&([a-f0-9]))", "\u00A7$2"));
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

}
