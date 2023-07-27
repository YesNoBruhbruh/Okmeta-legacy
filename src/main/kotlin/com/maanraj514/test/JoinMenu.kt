package com.maanraj514.test

import com.maanraj514.builder.ItemBuilder
import com.maanraj514.menu.Menu
import com.maanraj514.menu.PlayerMenuUtility
import com.maanraj514.utils.toColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

class JoinMenu(playerMenuUtility: PlayerMenuUtility) : Menu(playerMenuUtility) {

    override val menuName: String
        get() = "&a&lJoin Menu"
    override val slots: Int
        get() = 9

    override fun cancelAllClicks(): Boolean {
        return true
    }

    override fun handleMenu(event: InventoryClickEvent) {

        when (event.currentItem!!.type){
            Material.WOODEN_SWORD -> {
                if (event.currentItem!!.itemMeta != null && event.currentItem!!.itemMeta.displayName == "&7Click this to be in survival mode!".toColor()){
                    player.gameMode = GameMode.SURVIVAL
                    player.sendMessage("&aYou're gameMode is now Survival!".toColor())
                    player.closeInventory()
                }
            }

            Material.DIAMOND_SWORD -> {
                if (event.currentItem!!.itemMeta != null && event.currentItem!!.itemMeta.displayName == "&dClick this to be in creative mode!".toColor()){
                    player.gameMode = GameMode.CREATIVE
                    player.sendMessage("&dYou're gameMode is now Creative!".toColor())
                    player.closeInventory()
                }
            }

            else -> {
            }
        }
    }

    override fun setMenuItems() {

        val gameModeSurvivalItem = ItemBuilder(Material.WOODEN_SWORD)
            .setName("&7Click this to be in survival mode!")
            .setGlowing(true)
            .addLoreLine("&7Click this to be in survival mode!")
            .build()

        val gameModeCreativeItem = ItemBuilder(Material.DIAMOND_SWORD)
            .setName("&dClick this to be in creative mode!")
            .setGlowing(true)
            .addLoreLine("&dClick this to be in creative mode!")
            .build()

        inventory!!.setItem(3, gameModeSurvivalItem)
        inventory!!.setItem(5, gameModeCreativeItem)

        setFillerItem()
    }
}