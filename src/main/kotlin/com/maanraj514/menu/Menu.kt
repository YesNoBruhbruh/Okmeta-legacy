package com.maanraj514.menu

import com.maanraj514.utils.toColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * This is the menu class
 * subclasses extend from.
 */
abstract class Menu(private var playerMenuUtility: PlayerMenuUtility) : InventoryHolder {

    protected var player: Player = playerMenuUtility.owner

    // The Inventory used in the menu.
    @JvmField
    protected var inventory: Inventory? = null

    /**
     * @return Menu name.
     */
    abstract val menuName: String?

    /**
     * @return Inventory slots.
     */
    abstract val slots: Int

    /**
     * @return True or false for cancelling clicks.
     */
    abstract fun cancelAllClicks(): Boolean

    /**
     * Handles the inventory click event.
     *
     * @param event The event for InventoryClicking.
     */
    abstract fun handleMenu(event: InventoryClickEvent)

    /**
     * Set the menu items.
     */
    abstract fun setMenuItems()

    /**
     * Creates the inventory and sets
     * the items, sets the player
     * to the playerMenuUtility, and
     * opens the inventory.
     */
    fun open() {
        inventory = if (menuName == null){
            Bukkit.createInventory(this, slots)
        }else{
            Bukkit.createInventory(this, slots, menuName!!.toColor())
        }

        //grab all the items specified to be used for this menu and add to inventory
        setMenuItems()

        //open the inventory for the player
        playerMenuUtility.owner.openInventory(inventory!!)
        playerMenuUtility.pushMenu(this)
    }

    /**
     * Opens the previous menu the player was on.
     */
    fun back() {
        MenuManager.openMenu(playerMenuUtility.lastMenu().javaClass, playerMenuUtility.owner)
    }

    /**
     * Reloads the items.
     */
    protected fun reloadItems() {
        for (i in 0..<inventory!!.size) {
            inventory!!.setItem(i, null)
        }
        setMenuItems()
    }

    /**
     * Reloads the inventory.
     */
    protected fun reload() {
        player.closeInventory()
        MenuManager.openMenu(this.javaClass, player)
    }

    //Overridden method from the InventoryHolder interface
    override fun getInventory(): Inventory {
        return inventory!!
    }

    /**
     * Sets the default filler item
     * (GRAY_STAINED_GLASS_PANE).
     */
    fun setFillerItem() {
        val fillerItem = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        for (i in 0..<slots) {
            if (inventory!!.getItem(i) == null) {
                inventory!!.setItem(i, fillerItem)
            }
        }
    }

    /**
     * Sets the filler item.
     *
     * @param itemStack the item for the filler item.
     */
    fun setFillerItem(itemStack: ItemStack?) {
        for (i in 0..<slots) {
            if (inventory!!.getItem(i) == null) {
                inventory!!.setItem(i, itemStack)
            }
        }
    }
}