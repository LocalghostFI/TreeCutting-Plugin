package fi.localghost.TreeCutting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TreeCutting extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        if (player.hasPermission("treecutting.use") || player.hasPermission("*") || player.isOp()) {
            if (isAxe(mainHandItem.getType())) {
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
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -3; y <= 64; y++) { // Muokkaa korkeuden mukaan
                    Block relativeBlock = treePart.getRelative(x, y, z);

                    if (isTreePart(relativeBlock) && relativeBlock.getType() == treeType) {
                        relativeBlock.breakNaturally();
                    }
                }
            }
        }

        // Ilmoita serverin konsoliin
        Bukkit.getServer().getConsoleSender().sendMessage("Player " + player.getName() + " is chopping down " + treeType.toString() + " with a " + player.getInventory().getItemInMainHand().getType().toString() + " (height: " + 7 + ")");
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
