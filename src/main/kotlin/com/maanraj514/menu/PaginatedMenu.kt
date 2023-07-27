package com.maanraj514.menu

import com.maanraj514.builder.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * This class extends Menu and adds more features
 * to make it a paginatedMenu.
 */
abstract class PaginatedMenu(playerMenuUtility: PlayerMenuUtility?) : Menu(playerMenuUtility!!) {
    //The items being paginated
    protected var data: List<ItemStack>? = null

    //Keep track of what page the menu is on
    protected var page = 0

    //28 is max items because with the border set below,
    //28 empty slots are remaining.
    var maxItemsPerPage = 28
        protected set

    //the index represents the index of the slot
    //that the loop is on
    protected var index = 0

    /**
     * @param object A single element of the data list that you do something with. It is recommended that you turn this into an item if it is not already and then put it into the inventory as you would with a normal Menu. You can execute any other logic in here as well.
     */
    abstract fun loopCode(`object`: Any?)

    /**
     * Adds the default menu border to the menu.
     */
    protected fun addMenuBorder() {
        if (inventory != null) {
            inventory!!.setItem(48, ItemBuilder(Material.DARK_OAK_BUTTON).setName("&aLeft").build())
            inventory!!.setItem(49, ItemBuilder(Material.BARRIER).setName("&cClose").build())
            inventory!!.setItem(50, ItemBuilder(Material.DARK_OAK_BUTTON).setName("&aRight").build())
            val fillerGlassBuilder = ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
            fillerGlassBuilder.setName(" ")
            val fillerGlassItem = fillerGlassBuilder.build()
            for (i in 0..9) {
                if (inventory!!.getItem(i) == null) {
                    inventory!!.setItem(i, fillerGlassItem)
                }
            }
            inventory!!.setItem(17, fillerGlassItem)
            inventory!!.setItem(18, fillerGlassItem)
            inventory!!.setItem(26, fillerGlassItem)
            inventory!!.setItem(27, fillerGlassItem)
            inventory!!.setItem(35, fillerGlassItem)
            inventory!!.setItem(36, fillerGlassItem)
            for (i in 44..53) {
                if (inventory!!.getItem(i) == null) {
                    inventory!!.setItem(i, fillerGlassItem)
                }
            }
        }
    }

    /**
     * Adds a menu border with specified
     * items.
     *
     * @param backItem the back item to go back a page.
     * @param closeItem the close item to close the menu.
     * @param nextItem the next item to go to the next page.
     * @param fillerItem the filler item for the border of the menu.
     */
    protected fun addMenuBorder(
        backItem: ItemStack?,
        closeItem: ItemStack?,
        nextItem: ItemStack?,
        fillerItem: ItemStack?
    ) {
        if (inventory != null) {
            inventory!!.setItem(48, backItem)
            inventory!!.setItem(49, closeItem)
            inventory!!.setItem(50, nextItem)
            for (i in 0..9) {
                if (inventory!!.getItem(i) == null) {
                    inventory!!.setItem(i, fillerItem)
                }
            }
            inventory!!.setItem(17, fillerItem)
            inventory!!.setItem(18, fillerItem)
            inventory!!.setItem(26, fillerItem)
            inventory!!.setItem(27, fillerItem)
            inventory!!.setItem(35, fillerItem)
            inventory!!.setItem(36, fillerItem)
            for (i in 44..53) {
                if (inventory!!.getItem(i) == null) {
                    inventory!!.setItem(i, fillerItem)
                }
            }
        }
    }

    /**
     * Override version of the setMenuItems()
     * from Menu class, modified to work with
     * pages.
     */
    override fun setMenuItems() {
        addMenuBorder()
        if (data!!.isNotEmpty()) {
            for (i in 0..<maxItemsPerPage) {
                index = maxItemsPerPage * page + i
                println(index)
                if (index >= data!!.size) break
                loopCode(data!![index])
            }
        }
    }

    /**
     * @return true if successful, false if already on the first page
     */
    fun prevPage(): Boolean {
        return if (page == 0) {
            false
        } else {
            page -= 1
            reloadItems()
            true
        }
    }

    /**
     * @return true if successful, false if already on the last page
     */
    fun nextPage(): Boolean {
        return if (index + 1 < data!!.size) {
            page += 1
            reloadItems()
            true
        } else {
            false
        }
    }
}