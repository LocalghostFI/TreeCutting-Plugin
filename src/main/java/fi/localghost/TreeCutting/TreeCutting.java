package fi.localghost.TreeCutting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class TreeCutting extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("treecutting").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("version")) {
            sender.sendMessage("Treecutting " + getDescription().getVersion() + ", Update: 13.11.2023"); // Korvaa Update haluamallasi päivämäärällä
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        if (player.hasPermission("treecutting.use") || player.hasPermission("*") || player.isOp()) {
            if (isAxe(mainHandItem.getType()) && !player.isSneaking()) { // Lisää tarkistus pelaajan kyykistymisestä
                Block targetBlock = player.getTargetBlock(null, 5);
                Material blockType = targetBlock.getType();

                if (isTreePart(targetBlock)) {
                    cutDownTree(targetBlock, player);
                }
            }
        }
    }

    private void cutDownTree(Block treePart, Player player) {
        Material treeType = treePart.getType();
        int height = 0;
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -3; y <= 64; y++) { // Muokkaa korkeuden mukaan
                    Block relativeBlock = treePart.getRelative(x, y, z);

                    if (isTreePart(relativeBlock) && relativeBlock.getType() == treeType) {
                        relativeBlock.breakNaturally();
                        height++; // Lisää korkeutta aina kun kaadetaan osa puusta
                    }
                }
            }
        }

        // Kutsu uutta funktiota istuttamaan saplingit
        plantSaplings(player, treeType, height);

        // Ilmoita serverin konsoliin
        Bukkit.getServer().getConsoleSender().sendMessage(
                "Player " + player.getName() + " is chopping down " + treeType.toString() + " with a " + player.getInventory().getItemInMainHand().getType().toString() + " (height: " + height + ")");
    }

    private void plantSaplings(Player player, Material treeType, int height) {
        ItemStack[] inventory = player.getInventory().getContents();

        Material saplingType = Material.AIR; // Oletuksena ilmaa

        // Tarkista kaadetun puun tyyppi ja aseta vastaava sapling-tyyppi
        if (treeType == Material.OAK_LOG) {
            saplingType = Material.OAK_SAPLING;
        } else if (treeType == Material.SPRUCE_LOG) {
            saplingType = Material.SPRUCE_SAPLING;
        } else if (treeType == Material.BIRCH_LOG) {
            saplingType = Material.BIRCH_SAPLING;
        } else if (treeType == Material.JUNGLE_LOG) {
            saplingType = Material.JUNGLE_SAPLING;
        } else if (treeType == Material.ACACIA_LOG) {
            saplingType = Material.ACACIA_SAPLING;
        } else if (treeType == Material.DARK_OAK_LOG) {
            saplingType = Material.DARK_OAK_SAPLING;
        } else if (treeType == Material.CHERRY_LOG) {
            saplingType = Material.CHERRY_SAPLING;
        }

        int saplingCountToPlant = 1; // Oletuksena istutetaan aina yksi sapling

        if (saplingType == Material.DARK_OAK_SAPLING) {
            saplingCountToPlant = 4; // Istutetaan 2x2 (yhteensä 4)
        }

        // Tarkista, että pelaajalla on saplingType voimassa ja löytyy invista
        if (saplingType != Material.AIR) {
            int availableSaplings = 0;

            for (ItemStack item : inventory) {
                if (item != null && item.getType() == saplingType) {
                    availableSaplings += item.getAmount();
                }
            }

            saplingCountToPlant = Math.min(saplingCountToPlant, availableSaplings);
        }

        // Tarkista, että pelaajalla on saplingeja ennen istuttamista ja vähentää tarvittavan määrän
        if (saplingCountToPlant > 0) {
            for (ItemStack item : inventory) {
                if (item != null && item.getType() == saplingType) {
                    int itemAmount = item.getAmount();
                    if (saplingCountToPlant > 0) {
                        int saplingsToPlant = Math.min(saplingCountToPlant, itemAmount);
                        item.setAmount(itemAmount - saplingsToPlant);
                        saplingCountToPlant -= saplingsToPlant;
                    }
                }
            }

            // Istuta taimet maahan pelaajan sijaintiin
            if (saplingType == Material.DARK_OAK_SAPLING) {
                for (int i = -1; i <= 0; i++) {
                    for (int j = -1; j <= 0; j++) {
                        player.getWorld().getBlockAt(player.getLocation().getBlockX() + i, player.getLocation().getBlockY(), player.getLocation().getBlockZ() + j).setType(saplingType);
                    }
                }
            } else {
                player.getLocation().add(0, 0, 0).getBlock().setType(saplingType);
            }
        }
    }



    private boolean isAxe(Material material) {
        return material == Material.WOODEN_AXE || material == Material.STONE_AXE ||
                material == Material.IRON_AXE || material == Material.GOLDEN_AXE ||
                material == Material.DIAMOND_AXE || material == Material.NETHERITE_AXE;
    }
    private boolean isTreePart(Block block) {
        Material material = block.getType();
        int ordinal = material.ordinal();
        return (ordinal >= Material.OAK_LOG.ordinal() && ordinal <= Material.DARK_OAK_LOG.ordinal());
    }
}
