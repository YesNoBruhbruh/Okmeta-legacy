package com.maanraj514.builder

import com.destroystokyo.paper.profile.PlayerProfile
import com.maanraj514.utils.toColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataContainer
import java.util.function.Consumer
import java.util.function.UnaryOperator

/**
 * This class is used for building and editing
 * items easier.
 */
class ItemBuilder {
    /*
     * This is the item.
     */
    private val item: ItemStack

    /*
     * This is the item meta.
     */
    private lateinit var meta: ItemMeta

    /**
     * Creates a new instance of
     * Itembuilder with ItemStack.
     *
     * @param item the item.
     */
    constructor(item: ItemStack) {
        this.item = item
        if (item.itemMeta == null) {
            return
        }
        meta = item.itemMeta
    }

    /**
     * Creates a new instance of
     * ItemBuilder with Material
     *
     * @param material the material of the item.
     */
    constructor(material: Material?) : this(ItemStack(material!!))

    /**
     * Copies the instance of other
     * ItemBuilder and pastes it into
     * this one.
     *
     * @param itemBuilder the itembuilder replacing this one.
     */
    constructor(itemBuilder: ItemBuilder) {
        item = itemBuilder.item
        meta = itemBuilder.meta
    }

    /**
     * Sets the name of the item.
     *
     * @param name the name of the item.
     * @return this.
     */
    fun setName(name: String): ItemBuilder {
        meta.setDisplayName(name.toColor())
        return this
    }

    /**
     * Sets lore.
     * List<String>.
     *
     * @param lines the lore.
     * @return this.
    </String> */
    fun setLore(lines: List<String?>): ItemBuilder {
        val parsed: MutableList<String> = mutableListOf()

        for (line in lines) {
            parsed.add(line!!.toColor())
        }

        meta.lore = parsed
        return this
    }

    /**
     * Sets lore.
     * String[].
     *
     * @param lines the lore.
     * @return this.
     */
    fun setLore(vararg lines: String?): ItemBuilder {
        val parsed: MutableList<String> = mutableListOf()

        for (line in lines) {
            parsed.add(line!!.toColor())
        }

        meta.lore = parsed
        return this
    }

    /**
     * Adds a single line
     * of lore.
     *
     * @param line the lore line.
     * @return this.
     */
    fun addLoreLine(line: String): ItemBuilder {
        val lore: MutableList<String>? = if (meta.lore == null) {
            mutableListOf()
        } else {
            meta.lore
        }
        lore!!.add(line.toColor())
        meta.lore = lore
        return this
    }

    /**
     * Adds multiple lines
     * of lore.
     * String[]
     *
     * @param lines lore lines.
     * @return this.
     */
    fun addLoreLines(vararg lines: String?): ItemBuilder {
        val lore: MutableList<String>? = if (meta.lore == null) {
            mutableListOf()
        } else {
            meta.lore
        }

        for (line in lines) {
            lore!!.add(line!!.toColor())
        }

        meta.lore = lore
        return this
    }

    /**
     * Adds multiple lines
     * of lore.
     * List<String>
     *
     * @param lines lore lines.
     * @return this.
    </String> */
    fun addLoreLines(lines: List<String?>): ItemBuilder {
        val lore: MutableList<String>? = if (meta.lore == null) {
            mutableListOf()
        } else {
            meta.lore
        }

        for (line in lines) {
            lore!!.add(line!!.toColor())
        }

        meta.lore = lore
        return this
    }

    /**
     * Replaces the lore.
     * It requires a function.
     *
     * @param function replace the lore.
     * @return this.
     */
    fun replaceLore(function: UnaryOperator<String>): ItemBuilder {
        var lore = meta.lore

        if (lore == null) lore = mutableListOf()

        lore.replaceAll(function)

        meta.lore = lore

        return this
    }

    /**
     * Uses the other replaceLore().
     *
     * @param placeholder the placeholder.
     * @param replacement the replacement.
     * @return this.
     */
    fun replaceLore(placeholder: String, replacement: String): ItemBuilder {
        return this.replaceLore { line: String -> line.replace(placeholder, replacement) }
    }

    /**
     * Adds the specified enchant.
     *
     * @param enchantment enchantment.
     * @param level level.
     * @param ignoreMinecraftLimit if it ignore's minecraft's enchantment limit.
     * @return this.
     */
    fun addEnchant(enchantment: Enchantment, level: Int, ignoreMinecraftLimit: Boolean): ItemBuilder {
        meta.addEnchant(enchantment, level, ignoreMinecraftLimit)
        return this
    }

    /**
     * Removes the specified enchant.
     *
     * @param enchantment enchantment.
     * @return this.
     */
    fun removeEnchant(enchantment: Enchantment): ItemBuilder {
        meta.removeEnchant(enchantment)
        return this
    }

    /**
     * Adds the itemflags to the item.
     *
     * @param itemFlags itemflags.
     * @return this.
     */
    fun addItemFlags(vararg itemFlags: ItemFlag?): ItemBuilder {
        for (itemFlag in itemFlags){
            if (itemFlag != null){
                meta.addItemFlags(itemFlag)
            }
        }
        return this
    }

    /**
     * Removes the itemflags to the item.
     *
     * @param itemFlags itemflags.
     * @return this.
     */
    fun removeItemFlags(vararg itemFlags: ItemFlag?): ItemBuilder {
        for (itemFlag in itemFlags) {
            if (itemFlag != null) {
                meta.removeItemFlags(itemFlag)
            }
        }
        return this
    }

    /**
     * Sets the skull owner.
     * OfflinePlayer.
     *
     * @param offlinePlayer the offlinePlayer.
     * @return this.
     */
    fun setSkullOwner(offlinePlayer: OfflinePlayer): ItemBuilder {
        val skullMeta = meta as SkullMeta
        skullMeta.owningPlayer = offlinePlayer
        return this
    }

    /**
     * Sets the skull owner.
     * PlayerProfile.
     *
     * @param playerProfile the playerProfile.
     * @return this.
     */
    fun setSkullOwner(playerProfile: PlayerProfile): ItemBuilder {
        val skullMeta = meta as SkullMeta
        skullMeta.ownerProfile = playerProfile
        return this
    }

    /**
     * Sets customModelData on the item.
     *
     * @param modelData modelData.
     * @return this.
     */
    fun setCustomModelData(modelData: Int): ItemBuilder {
        meta.setCustomModelData(modelData)
        return this
    }

    /**
     * Applies persistentData on the item.
     *
     * @param function applyPersistentData.
     * @return this.
     */
    fun applyPersistentData(function: Consumer<PersistentDataContainer?>): ItemBuilder {
        function.accept(meta.persistentDataContainer)
        return this
    }

    /**
     * Add an attribute modifier to the item.
     *
     * @param attribute The attribute to add the modifier to.
     * @param modifier The modifier to add.
     * @return this.
     */
    fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder {
        meta.addAttributeModifier(attribute, modifier)
        return this
    }

    /**
     * Remove an attribute modifier from the item.
     *
     * @param attribute The attribute to remove the modifier from.
     * @return this.
     */
    fun removeAttributeModifier(attribute: Attribute): ItemBuilder {
        meta.removeAttributeModifier(attribute)
        return this
    }

    /**
     * Remove an attribute modifier from the item.
     *
     * @param attribute The attribute to remove the modifier from.
     * @param modifier The modifier to remove.
     * @return this.
     */
    fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder {
        meta.removeAttributeModifier(attribute, modifier)
        return this
    }

    /**
     * Sets the color on
     * leather armor.
     *
     * @param color color.
     * @return this
     */
    fun setLeatherColor(color: Color): ItemBuilder {
        val leatherArmorMeta = meta as LeatherArmorMeta
        leatherArmorMeta.setColor(color)
        return this
    }

    /**
     * Sets if the item would be
     * unbreakable or not.
     *
     * @param unbreakable if true, unbreakable. If false, not unbreakable.
     * @return this
     */
    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        meta.isUnbreakable = unbreakable
        return this
    }

    /**
     * Makes the item glowing.
     *
     * @param glowing true if it should glow, false if not.
     * @return this
     */
    fun setGlowing(glowing: Boolean): ItemBuilder {
        if (glowing) {
            addEnchant(Enchantment.LUCK, 1, false)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        } else {
            if (item.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                item.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
            if (item.enchantments.containsKey(Enchantment.LUCK)) {
                item.removeEnchantment(Enchantment.LUCK)
            }
        }
        return this
    }

    /**
     * This returns an itemstack
     * with all the modifications.
     *
     * @return item
     */
    fun build(): ItemStack {
        item.itemMeta = meta
        return item
    }
}